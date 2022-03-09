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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

@Service
public class UserRedPacketServiceImpl extends ServiceImpl<UserRedPacketDao, UserRedPacket> implements UserRedPacketService {

	@Autowired
	private UserRedPacketDao userRedPacketDao = null;

	@Autowired
	private RedPacketDao redPacketDao = null;

	// ʧ��
	private static final int FAILED = 0;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int grapRedPacket(Long redPacketId, Long userId) {
		// 不使用锁
//		 RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
		// 使用悲观锁
		RedPacket redPacket = redPacketDao.getRedPacketForUpdate(redPacketId);
		// ��ǰС���������0
		if (redPacket.getStock() > 0) {
			redPacketDao.decreaseRedPacket(redPacketId);
			// �����������Ϣ
			UserRedPacket userRedPacket = new UserRedPacket();
			userRedPacket.setRedPacketId(redPacketId);
			userRedPacket.setUserId(userId);
			userRedPacket.setAmount(redPacket.getUnitAmount());
			userRedPacket.setNote("抢红包" + redPacketId);
			// �����������Ϣ
			int result = userRedPacketDao.grapRedPacket(userRedPacket);
			return result;
		}
		// ʧ�ܷ���
		return FAILED;
	}

	// �ֹ�����������
//	@Override
//	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
//	public int grapRedPacketForVersion(Long redPacketId, Long userId) {
//		// ��ȡ�����Ϣ,ע��versionֵ
//		RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
//		// ��ǰС���������0
//		if (redPacket.getStock() > 0) {
//			// �ٴδ����̱߳����version��ֵ��SQL�жϣ��Ƿ��������߳��޸Ĺ�����
//			int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
//			// ���û�����ݸ��£���˵�������߳��Ѿ��޸Ĺ����ݣ����������ʧ��
//			if (update == 0) {
//				return FAILED;
//			}
//			// �����������Ϣ
//			UserRedPacket userRedPacket = new UserRedPacket();
//			userRedPacket.setRedPacketId(redPacketId);
//			userRedPacket.setUserId(userId);
//			userRedPacket.setAmount(redPacket.getUnitAmount());
//			userRedPacket.setNote("����� " + redPacketId);
//			// �����������Ϣ
//			int result = userRedPacketDao.grapRedPacket(userRedPacket);
//			return result;
//		}
//		// ʧ�ܷ���
//		return FAILED;
//	}

	// �ֹ�������ʱ�������
	// @Override
	// @Transactional(isolation = Isolation.READ_COMMITTED, propagation =
	// Propagation.REQUIRED)
	// public int grapRedPacketForVersion(Long redPacketId, Long userId) {
	// // ��¼��ʼʱ��
	// long start = System.currentTimeMillis();
	// // ����ѭ�����ȴ��ɹ�����ʱ����100�����˳�
	// while (true) {
	// // ��ȡѭ����ǰʱ��
	// long end = System.currentTimeMillis();
	// // ��ǰʱ���Ѿ�����100���룬����ʧ��
	// if (end - start > 100) {
	// return FAILED;
	// }
	// // ��ȡ�����Ϣ,ע��versionֵ
	// RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
	// // ��ǰС���������0
	// if (redPacket.getStock() > 0) {
	// // �ٴδ����̱߳����version��ֵ��SQL�жϣ��Ƿ��������߳��޸Ĺ�����
	// int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
	// redPacket.getVersion());
	// // ���û�����ݸ��£���˵�������߳��Ѿ��޸Ĺ����ݣ�����������
	// if (update == 0) {
	// continue;
	// }
	// // �����������Ϣ
	// UserRedPacket userRedPacket = new UserRedPacket();
	// userRedPacket.setRedPacketId(redPacketId);
	// userRedPacket.setUserId(userId);
	// userRedPacket.setAmount(redPacket.getUnitAmount());
	// userRedPacket.setNote("����� " + redPacketId);
	// // �����������Ϣ
	// int result = userRedPacketDao.grapRedPacket(userRedPacket);
	// return result;
	// } else {
	// // һ��û�п�棬�����Ϸ���
	// return FAILED;
	// }
	// }
	// }

	// �ֹ���������������
	// @Override
	// @Transactional(isolation = Isolation.READ_COMMITTED, propagation =
	// Propagation.REQUIRED)
	// public int grapRedPacketForVersion(Long redPacketId, Long userId) {
	// for (int i = 0; i < 3; i++) {
	// // ��ȡ�����Ϣ��ע��versionֵ
	// RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
	// // ��ǰС���������0
	// if (redPacket.getStock() > 0) {
	// // �ٴδ����̱߳����version��ֵ��SQL�жϣ��Ƿ��������߳��޸Ĺ�����
	// int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
	// redPacket.getVersion());
	// // ���û�����ݸ��£���˵�������߳��Ѿ��޸Ĺ����ݣ�����������
	// if (update == 0) {
	// continue;
	// }
	// // �����������Ϣ
	// UserRedPacket userRedPacket = new UserRedPacket();
	// userRedPacket.setRedPacketId(redPacketId);
	// userRedPacket.setUserId(userId);
	// userRedPacket.setAmount(redPacket.getUnitAmount());
	// userRedPacket.setNote("����� " + redPacketId);
	// // �����������Ϣ
	// int result = userRedPacketDao.grapRedPacket(userRedPacket);
	// return result;
	// } else {
	// // һ��û�п�棬�����Ϸ���
	// return FAILED;
	// }
	// }
	// return FAILED;
	// }

	/*@Autowired
	private RedisTemplate redisTemplate = null;

	@Autowired
	private RedisRedPacketService redisRedPacketService = null;

	// Lua�ű�
	String script = "local listKey = 'red_packet_list_'..KEYS[1] \n" 
	+ "local redPacket = 'red_packet_'..KEYS[1] \n"
			+ "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n" 
			+ "if stock <= 0 then return 0 end \n"
			+ "stock = stock -1 \n" 
			+ "redis.call('hset', redPacket, 'stock', tostring(stock)) \n"
			+ "redis.call('rpush', listKey, ARGV[1]) \n" 
			+ "if stock == 0 then return 2 end \n" 
			+ "return 1 \n";

	// �ڻ���LUA�ű���ʹ�øñ�������Redis���ص�32λ��SHA1���룬ʹ����ȥִ�л����LUA�ű�[������仰]
	String sha1 = null;

	@Override
	public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
		// ��ǰ������û���������Ϣ
		String args = userId + "-" + System.currentTimeMillis();
		Long result = null;
		// ��ȡ�ײ�Redis��������
		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
		try {
			// ����ű�û�м��ع�����ô���м��أ������ͻ᷵��һ��sha1����
			if (sha1 == null) {
				sha1 = jedis.scriptLoad(script);
			}
			// ִ�нű������ؽ��
			Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);
			result = (Long) res;
			// ����2ʱΪ���һ���������ʱ���������Ϣͨ���첽���浽���ݿ���
			if (result == 2) {
				// ��ȡ����С������
				String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
				// �����������ݿ����
				Double unitAmount = Double.parseDouble(unitAmountStr);
				System.err.println("thread_name = " + Thread.currentThread().getName());
				redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
			}
		} finally {
			// ȷ��jedis˳���ر�
			if (jedis != null && jedis.isConnected()) {
				jedis.close();
			}
		}
		return result;
	}*/
}
