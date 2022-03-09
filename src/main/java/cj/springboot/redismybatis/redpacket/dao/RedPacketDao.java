package cj.springboot.redismybatis.redpacket.dao;

import cj.springboot.redismybatis.redpacket.pojo.RedPacket;
import cj.springboot.redismybatis.redpacket.pojo.UserRedPacket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

//@Repository
public interface RedPacketDao extends BaseMapper<RedPacket> {
	
	/**
	 * ��ȡ�����Ϣ.
	 * @param id --���id
	 * @return ���������Ϣ
	 */
	public RedPacket getRedPacket(Long id);
	/**
	 * �ۼ��������.
	 * @param id -- ���id
	 * @return ���¼�¼����
	 */
	public int decreaseRedPacket(Long id);
	
	/***
	 * ʹ��for update 悲观锁
	 * @param id �������id
	 * @return �����Ϣ
	 */
	public RedPacket getRedPacketForUpdate(Long id);
//
//
	public int decreaseRedPacketForVersion(@Param("id") Long id, @Param("version") Integer version);

}