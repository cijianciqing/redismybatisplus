package cj.springboot.redismybatis.service.impl;

import cj.springboot.redismybatis.entity.SysUserEntity;
import cj.springboot.redismybatis.dao.SysUserDao;
import cj.springboot.redismybatis.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cj
 * @since 2021-12-07
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {

}
