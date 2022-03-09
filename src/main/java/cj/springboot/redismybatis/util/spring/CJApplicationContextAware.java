package cj.springboot.redismybatis.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CJApplicationContextAware implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CJApplicationContextAware.applicationContext = applicationContext;
    }

//    从applicationContext中获取所需的bean
    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }

}
