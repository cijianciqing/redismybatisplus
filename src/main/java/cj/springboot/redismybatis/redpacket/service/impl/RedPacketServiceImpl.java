package cj.springboot.redismybatis.redpacket.service.impl;



import cj.springboot.redismybatis.redpacket.dao.RedPacketDao;
import cj.springboot.redismybatis.redpacket.pojo.RedPacket;
import cj.springboot.redismybatis.redpacket.service.RedPacketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedPacketServiceImpl extends ServiceImpl<RedPacketDao, RedPacket> implements RedPacketService {
	@Autowired
	private RedPacketDao redPacketDao = null;

	@Override
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RedPacket getRedPacket(Long id) {
		return redPacketDao.getRedPacket(id);
	}

	@Override
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int decreaseRedPacket(Long id) {
		return redPacketDao.decreaseRedPacket(id);
	}

}