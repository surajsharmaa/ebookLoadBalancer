package com.ebook.loadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class LoadBalancerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadBalancerApplication.class, args);
	}
	
	@Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
       // restTemplate.setRequestFactory(requestFactory());
        return restTemplate;
    }

    private HttpComponentsAsyncClientHttpRequestFactory requestFactory() {
        HttpComponentsAsyncClientHttpRequestFactory requestFactory = new HttpComponentsAsyncClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(1000);
        requestFactory.setReadTimeout(1000);
        return requestFactory;
    }
}
