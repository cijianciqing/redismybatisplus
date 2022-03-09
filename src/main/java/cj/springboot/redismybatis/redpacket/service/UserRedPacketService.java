package cj.springboot.redismybatis.redpacket.service;

import cj.springboot.redismybatis.entity.SysRoleEntity;
import cj.springboot.redismybatis.redpacket.pojo.UserRedPacket;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserRedPacketService extends IService<UserRedPacket> {
	
	/**
	 * �����������Ϣ.
	 * @param redPacketId ������
	 * @param userId ������û����
	 * @return Ӱ���¼��.
	 */
	public int grapRedPacket(Long redPacketId, Long userId);
	
	
	public int grapRedPacketForVersion(Long redPacketId, Long userId);
	
	/**
	 * ͨ��Redisʵ�������
	 * @param redPacketId --������
	 * @param userId -- �û����
	 * @return  
	 * 0-û�п�棬ʧ�� 
	 * 1--�ɹ����Ҳ������һ�����
	 * 2--�ɹ����������һ�����
	 */
//	public Long grapRedPacketByRedis(Long redPacketId, Long userId);
	
}
