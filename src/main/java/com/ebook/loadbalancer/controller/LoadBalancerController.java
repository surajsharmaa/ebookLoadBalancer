package com.ebook.loadbalancer.controller;

import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.ebook.orderprocessorservice.request.OrderRequest;
import com.ebook.orderprocessorservice.response.OrderResponse;

@RestController
public class LoadBalancerController {
	@Autowired
    private RestTemplate restTemplate;
	
	private String dellBaseUrl ="http://localhost:9400";
	private String hpBaseUrl ="http://192.168.0.30:9400";
     

	@RequestMapping(method = RequestMethod.POST, value = "/order")
	public ResponseEntity<OrderResponse> createEventException(@Valid @RequestBody OrderRequest message,
			@RequestHeader("token") String tokenFromRequest, BindingResult bindingResult) throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("token", tokenFromRequest);
		
		HttpEntity<OrderRequest> request = new HttpEntity<OrderRequest>(message, httpHeaders);
		
		ResponseEntity<OrderResponse> response = null;
		try {
			//check if the PRIMARY(HP) is up
			Object getHpHealth = restTemplate.getForObject(hpBaseUrl + "/actuator/health", Object.class); 
			if(getHpHealth.toString().equals("{status=UP}")) {
				response = restTemplate.postForEntity(hpBaseUrl + "/order", request, OrderResponse.class);
			}
			System.out.println(getHpHealth.toString());
		} catch (Exception e) {
			try {
				//Check if FALL BACK (Dell) is up
				Object getDellHealth = restTemplate.getForObject(dellBaseUrl + "/actuator/health", Object.class); 
				if(getDellHealth.toString().equals("{status=UP}")) {
					response = restTemplate.postForEntity(dellBaseUrl + "/order", request, OrderResponse.class);
				}
			} catch (Exception e1) {
				throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Sorry we are not available at this time. Please try at later time!");
			}			
		}
		return response;
	}
}