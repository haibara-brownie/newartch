package com.cloudwise.archetype.api.conf;

import com.cloudwise.archetype.api.service.AirOrdersService;
import com.cloudwise.archetype.dao.activerecord.AirOrders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class kafkaConfig {
	
	@Autowired
	private AirOrdersService airOrdersService;
	
	@KafkaListener(topics = "air" ,groupId = "air-group")
	public void listen (String airOrders, Acknowledgment acknowledgment) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			AirOrders airOrder = objectMapper.readValue(airOrders, AirOrders.class);
			airOrdersService.save(airOrder);
			acknowledgment.acknowledge();
		}catch (Exception e){
			System.err.println("Error processing message: " + e.getMessage());
		}
	}
}
