package cj.springboot.redismybatis.redpacket.service;


import cj.springboot.redismybatis.redpacket.pojo.RedPacket;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RedPacketService extends IService<RedPacket> {
	
	/**
	 * 获取红包
	 * @param id ——编号
	 * @return 红包信息
	 */
	public RedPacket getRedPacket(Long id);

	/**
	 * 扣减红包
	 * @param id——编号
	 * @return 影响条数.
	 */
	public int decreaseRedPacket(Long id);
	
}