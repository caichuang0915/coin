package com.deal.coin;

import com.deal.coin.conf.MyApplicationStartup;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.deal.coin.mapper")
public class CoinApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(CoinApplication.class);
		springApplication.addListeners(new MyApplicationStartup());
		springApplication.run(args);
	}

}
