package com.soquickproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages={"com.soquickproject"})
@MapperScan("com.soquickproject.dao")
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        //启动整个项目
        SpringApplication.run(App.class,args);
    }
}
