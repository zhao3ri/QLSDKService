package com.qinglan.sdk.server.application.basic.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public class KafkaProducerClient {

	private final static Logger logger = LoggerFactory.getLogger(KafkaProducerClient.class);
	
	private Producer<String, String> inner;
	
	private String brokerList;
	
	private String defaultTopic;
	
	private KeyedMessage<String, String> km;

	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}

	public void setDefaultTopic(String defaultTopic) {
		this.defaultTopic = defaultTopic;
	}

	public KafkaProducerClient(){}
	
	public void init() throws Exception {
//		Properties properties = new Properties();
//		if(brokerList != null) {
//			properties.put("metadata.broker.list", brokerList);
//			properties.put("serializer.class","kafka.serializer.StringEncoder");
//		}
//		ProducerConfig config = new ProducerConfig(properties);
//		inner = new Producer<String, String>(config);
	}

	public void send(String message){
//		try{
//			if (message == null) {
//				return;
//			}
//			km = new KeyedMessage<String, String>(defaultTopic,message);
//			inner.send(km);
//		}catch(Exception e){
//			logger.error("kafka error",e);
//		}
		
	}

}
