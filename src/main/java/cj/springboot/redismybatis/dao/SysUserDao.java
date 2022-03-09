package cj.springboot.redismybatis.dao;
import java.util.List;

import cj.springboot.redismybatis.redis.CJRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Param;

import cj.springboot.redismybatis.entity.SysUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cj
 * @since 2021-12-07
 */
//@CacheNamespace(implementation = CJRedisCache.class,eviction = CJRedisCache.class)
@CacheNamespaceRef(name = "cj.springboot.redismybatis.dao.SysUserDao")
public interface SysUserDao extends BaseMapper<SysUserEntity> {
    SysUserEntity findByName(@Param("name") String name);

    boolean updateUser(SysUserEntity user);
}
