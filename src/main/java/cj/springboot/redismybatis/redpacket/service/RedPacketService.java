package cj.springboot.redismybatis.redpacket.service;


import cj.springboot.redismybatis.redpacket.pojo.RedPacket;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RedPacketService extends IService<RedPacket> {
	
	/**
	 * ��ȡ���
	 * @param id �������
	 * @return �����Ϣ
	 */
	public RedPacket getRedPacket(Long id);

	/**
	 * �ۼ����
	 * @param id�������
	 * @return Ӱ������.
	 */
	public int decreaseRedPacket(Long id);
	
}