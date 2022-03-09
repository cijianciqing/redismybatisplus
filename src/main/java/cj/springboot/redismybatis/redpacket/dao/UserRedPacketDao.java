package cj.springboot.redismybatis.redpacket.dao;

import cj.springboot.redismybatis.entity.SysRoleEntity;
import cj.springboot.redismybatis.redpacket.pojo.UserRedPacket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

//@Repository
public interface UserRedPacketDao extends BaseMapper<UserRedPacket> {

	/**
	 * 插入抢红包信息.
	 * @param userRedPacket ——抢红包信息
	 * @return 影响记录数.
	 */
	public int grapRedPacket(UserRedPacket userRedPacket);
}
