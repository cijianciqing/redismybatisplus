package cj.springboot.redismybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"cj.springboot.redismybatis.redpacket.dao","cj.springboot.redismybatis.dao"})
@SpringBootApplication
public class RedisMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisMybatisApplication.class, args);
    }

}
