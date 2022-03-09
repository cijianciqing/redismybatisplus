package cj.springboot.redismybatis.redis;

import cj.springboot.redismybatis.util.spring.CJApplicationContextAware;
import org.apache.ibatis.cache.Cache;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

public class CJRedisCache implements Cache {

    // cache的标识符
    private String id;

    public CJRedisCache(String id){
        this.id=id;
    }

    private RedisTemplate<String, Object> getRedisTempplate() {
        return (RedisTemplate<String, Object>) CJApplicationContextAware.getBean("redisTemplate");
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        getRedisTempplate().opsForHash().put(id, key, value);
    }

    @Override
    public Object getObject(Object key) {

        return getRedisTempplate().opsForHash().get(id, key);
    }

    @Override
    public Object removeObject(Object key) {
        return null;
    }

    @Override
    public void clear() {
        getRedisTempplate().delete(id);
    }

    @Override
    public int getSize() {
        return getRedisTempplate().opsForHash().size(id).intValue();
    }
}
