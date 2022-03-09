package cj.springboot.redismybatis;

import cj.springboot.redismybatis.dao.SysUserDao;
import cj.springboot.redismybatis.entity.SysUserEntity;
import cj.springboot.redismybatis.util.spring.CJApplicationContextAware;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisMybatisApplicationTests {

    @Autowired
    CJApplicationContextAware cjApplicationContextAware;

    @Test
    void contextLoads() {
        Object dataSource = CJApplicationContextAware.getBean("dataSource");
        System.out.println(dataSource.getClass().getName());
        Object transactionManager = CJApplicationContextAware.getBean("transactionManager");
        System.out.println(transactionManager.getClass().getName());
//        Object cacheManager = CJApplicationContextAware.getBean("cacheManager");
//        System.out.println(cacheManager.getClass().getName());
    }
    @Autowired
    private SysUserDao sysUserDao;

//    xml mapper cache测试
    @Test
    public void test01(){
//        List<SysUserEntity> sysUserEntities = sysUserDao.selectList(null);
//        List<SysUserEntity> sysUserEntities2 = sysUserDao.selectList(null);
//        sysUserEntities.forEach(System.out::println);
        SysUserEntity wqn = sysUserDao.findByName("wqn");
        SysUserEntity wqn02 = sysUserDao.findByName("wqn02");
        System.out.println(wqn);

    }

    @Test
    public void test02(){
        SysUserEntity sysUserEntity = sysUserDao.selectById(1);
        SysUserEntity sysUserEntity2 = sysUserDao.selectById(1);
        System.out.println(sysUserEntity);
    }

    @Test
    public void test03(){
        SysUserEntity sysUserEntity = sysUserDao.selectById(1);
        sysUserEntity.setName("wqn01");
        boolean b = sysUserDao.updateUser(sysUserEntity);

    }
}
