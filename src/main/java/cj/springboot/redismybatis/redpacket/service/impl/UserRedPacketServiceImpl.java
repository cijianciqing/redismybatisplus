package cj.springboot.redismybatis.redpacket.service.impl;


import cj.springboot.redismybatis.dao.SysRoleDao;
import cj.springboot.redismybatis.entity.SysRoleEntity;
import cj.springboot.redismybatis.redpacket.dao.RedPacketDao;
import cj.springboot.redismybatis.redpacket.dao.UserRedPacketDao;
import cj.springboot.redismybatis.redpacket.pojo.RedPacket;
import cj.springboot.redismybatis.redpacket.pojo.UserRedPacket;
import cj.springboot.redismybatis.redpacket.service.RedisRedPacketService;
import cj.springboot.redismybatis.redpacket.service.UserRedPacketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserRedPacketServiceImpl extends ServiceImpl<UserRedPacketDao, UserRedPacket> implements UserRedPacketService {

    @Autowired
    private UserRedPacketDao userRedPacketDao;

    @Autowired
    private RedPacketDao redPacketDao;

    private static final int FAILED = 0;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grapRedPacket(Long redPacketId, Long userId) {
        // 不使用锁
//		 RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        // 使用悲观锁
        RedPacket redPacket = redPacketDao.getRedPacketForUpdate(redPacketId);
        if (redPacket.getStock() > 0) {
            redPacketDao.decreaseRedPacket(redPacketId);
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("抢红包" + redPacketId);
            int result = userRedPacketDao.grapRedPacket(userRedPacket);
            return result;
        }
        return FAILED;
    }

    // 使用乐观锁，通过version控制版本--基本
//	@Override
//	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
//	public int grapRedPacketForVersion(Long redPacketId, Long userId) {
//		RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
//		if (redPacket.getStock() > 0) {
//			// 判断此时后台version是否更新
//			int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
//			if (update == 0) {
//				return FAILED;
//			}
//			UserRedPacket userRedPacket = new UserRedPacket();
//			userRedPacket.setRedPacketId(redPacketId);
//			userRedPacket.setUserId(userId);
//			userRedPacket.setAmount(redPacket.getUnitAmount());
//			userRedPacket.setNote("success" + redPacketId);
//
//			int result = userRedPacketDao.grapRedPacket(userRedPacket);
//			return result;
//		}
//		return FAILED;
//	}

    // 使用乐观锁，通过version控制版本--重入机制
    //100ms内可以重复 自动 抢红包
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation =
            Propagation.REQUIRED)
    public int grapRedPacketForVersion(Long redPacketId, Long userId) {
        long start = System.currentTimeMillis();
        while (true) {
            long end = System.currentTimeMillis();
            if (end - start > 100) {
                return FAILED;
            }

            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);

            if (redPacket.getStock() > 0) {
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
                        redPacket.getVersion());
                if (update == 0) {
                    continue;
                }
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(redPacket.getUnitAmount());
                userRedPacket.setNote("success" + redPacketId);
                int result = userRedPacketDao.grapRedPacket(userRedPacket);
                return result;
            } else {
                return FAILED;
            }
        }
    }
    // 使用乐观锁，通过version控制版本--重入机制
    //重复3次 自动 抢红包
    // @Override
    // @Transactional(isolation = Isolation.READ_COMMITTED, propagation =
    // Propagation.REQUIRED)
    // public int grapRedPacketForVersion(Long redPacketId, Long userId) {
    // for (int i = 0; i < 3; i++) {
    // RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
    // if (redPacket.getStock() > 0) {
    // int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
    // redPacket.getVersion());
    // if (update == 0) {
    // continue;
    // }

    // UserRedPacket userRedPacket = new UserRedPacket();
    // userRedPacket.setRedPacketId(redPacketId);
    // userRedPacket.setUserId(userId);
    // userRedPacket.setAmount(redPacket.getUnitAmount());
    // userRedPacket.setNote("success " + redPacketId);
    // int result = userRedPacketDao.grapRedPacket(userRedPacket);
    // return result;
    // } else {
    // return FAILED;
    // }
    // }
    // return FAILED;
    // }

	@Autowired
	private RedisTemplate redisTemplate = null;

	@Autowired
	private RedisRedPacketService redisRedPacketService = null;

    /*
     * 使用redis-基础版
     *
     *结果：30000个请求，7.88s完成
     *
     * 出现的问题：超买
     *
     * 1.初始数据
     *   红包id, 红包数量20000
     * 2.判断当前红包数量是否>0
     *   >0 ：红包id对应的数量-1，用户的红包数量+1
     * */
//    @Override
//    public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
//        //
//        String args = userId + "-" + System.currentTimeMillis();
//        Long result = null;
//        String hongbaoKey = "hb"+redPacketId;
//        String userKey = "us"+userId;
//        int current = (int)redisTemplate.opsForValue().get(hongbaoKey);
//        if(current > 0){
//            redisTemplate.opsForValue().decrement(hongbaoKey);
//            redisTemplate.opsForValue().setIfAbsent(userKey,0);
//            redisTemplate.opsForValue().increment(userKey);
//            result = 1L;
//        }else {
//            result = 0L;
//        }
//
//        return result;
//    }
    /*
     * 使用redis-基础版-2
     * 更改：新增了同步代码块
     *
     *
     * 出现的问题：速度慢  47.003 seconds
     *
     *
     * */
//    Object object = new Object();
//    @Override
//    public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
//        //
//        Long result = null;
//        String hongbaoKey = "hb"+redPacketId;
//        String userKey = "us"+userId;
//        synchronized (object){
//            int current = (int)redisTemplate.opsForValue().get(hongbaoKey);
//            if(current > 0){
//                redisTemplate.opsForValue().decrement(hongbaoKey);
//                redisTemplate.opsForValue().setIfAbsent(userKey,0);
//                redisTemplate.opsForValue().increment(userKey);
//                result = 1L;
//            }else {
//                result = 0L;
//            }
//        }
//
//
//        return result;
//    }


    /*
     * 使用redis-基础版-3
     * 更改：使用redis中的键作为lock
     *
     * 出现的问题：速度慢   127.460 seconds
     *
     *
     * */
//    Object object = new Object();
//    @Override
//    public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
//        //
//        Long result = 0L;
//        String locKey = "lock:" + redPacketId; // 锁住的是每个商品的数据
//        String hongbaoKey = "hb"+redPacketId;
//        String userKey = "us"+userId;
//
//        // 3 获取锁
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent(locKey, userId, 3, TimeUnit.SECONDS);
//        if (lock){
//            int current = (int)redisTemplate.opsForValue().get(hongbaoKey);
//            if(current > 0){
//                redisTemplate.opsForValue().decrement(hongbaoKey);
//                redisTemplate.opsForValue().setIfAbsent(userKey,0);
//                redisTemplate.opsForValue().increment(userKey);
//                redisTemplate.delete(locKey);
//                result = 1L;
//            }
//        }else {
//            // 其他线程等待
//            try {
//                // 睡眠
//                Thread.sleep(1000);
//                // 睡醒了之后，调用方法。
//                int current = (int)redisTemplate.opsForValue().get(hongbaoKey);
//                if(current > 0){//避免出现已经抢完后，重复执行的问题
//                    grapRedPacketByRedis(redPacketId, userId);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        return result;
//
//    }

    /*
     * 使用redis
     * 更改：使用redis+lua
     *
     * 出现的问题：速度慢   7.058 seconds
     *
     *
     * */
    String sha1 = null;

    public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
        // Lua脚本
        String script =
                "local redPacketKey = KEYS[1] \n"
                + "local userKey = KEYS[2] \n"
                + "local stock = tonumber(redis.call('get', redPacketKey)) \n"
                + "if stock <= 0 then return 0 end \n"//返回值为0： 库存清空
                + "stock = stock -1 \n"
                + "redis.call('set', redPacketKey, stock) \n"
                + "redis.call('incr', userKey) \n"
                + "if stock == 0 then return 2 end \n"//返回值如果等于2，从redis写入数据库
                + "return 1 \n";//返回值为1： 继续抢红包

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        List<String> keys = new ArrayList<>();
        keys.add("hb"+redPacketId);//k1
        keys.add("us"+userId);//k2
        Long result = (Long)redisTemplate.execute(redisScript, keys);
        System.out.println("result : "+ result);
        return result;
    }

    // Lua脚本
//	String script = "local listKey = 'red_packet_list_'..KEYS[1] \n"
//	+ "local redPacket = 'red_packet_'..KEYS[1] \n"
//			+ "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n"
//			+ "if stock <= 0 then return 0 end \n"
//			+ "stock = stock -1 \n"
//			+ "redis.call('hset', redPacket, 'stock', tostring(stock)) \n"
//			+ "redis.call('rpush', listKey, ARGV[1]) \n"
//			+ "if stock == 0 then return 2 end \n"
//			+ "return 1 \n";
//
//	String sha1 = null;



//	@Override
//	public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
//		//
//		String args = userId + "-" + System.currentTimeMillis();
//		Long result = null;
//		//
//		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
//		try {
//			//
//			if (sha1 == null) {
//				sha1 = jedis.scriptLoad(script);
//			}
//			//执行lua脚本
//			Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);
//			result = (Long) res;
//			//
//			if (result == 2) {
//				//
//				String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
//				//
//				Double unitAmount = Double.parseDouble(unitAmountStr);
//				System.err.println("thread_name = " + Thread.currentThread().getName());
//				redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
//			}
//		} finally {
//			//
//			if (jedis != null && jedis.isConnected()) {
//				jedis.close();
//			}
//		}
//		return result;
//	}
}
