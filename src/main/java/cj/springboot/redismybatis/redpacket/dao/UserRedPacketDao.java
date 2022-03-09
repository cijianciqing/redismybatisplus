package cj.springboot.redismybatis.redpacket.dao;

import cj.springboot.redismybatis.entity.SysRoleEntity;
import cj.springboot.redismybatis.redpacket.pojo.UserRedPacket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

//@Repository
public interface UserRedPacketDao extends BaseMapper<UserRedPacket> {

	/**
	 * �����������Ϣ.
	 * @param userRedPacket �����������Ϣ
	 * @return Ӱ���¼��.
	 */
	public int grapRedPacket(UserRedPacket userRedPacket);
}
