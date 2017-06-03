package de.hska;



import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;

import de.hska.core.security.SimpleSecurity;

@SpringBootApplication
public class TwitterKlonApplication {
	private static Logger logger = Logger.getLogger(TwitterKlonApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(TwitterKlonApplication.class, args);
		SimpleSecurity.template = (RedisTemplate<String, Object>)ctx.getBean("redisTemplate");
		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		
		logger.info("***************************************");
		
		for(int i=0; i<beanNames.length; i++) {
			logger.info(beanNames[i]);
		}
		
		logger.info("***************************************");
	}
}
