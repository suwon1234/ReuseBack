package com.example.msasbproducts.kafka;

import com.example.msasbproducts.dto.ProductDetailDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * 트리거에 의해서 이벤트 발생(메세지 전송) -> 브로커(kafka)
 */
@Service
public class KafkaProducer {
    // 브로커에서 메세지를 전송하는 객체 필요 -> DI
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    // 문자열 직렬, 역직렬 처리용도
    @Autowired
    private ObjectMapper objectMapper;

    public void sendMsg(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
    // 재료는 OrderDto객체 -> 문자열직열화 처리 -> 메세지 형태는 문자열
    // 재료는 OrderDto객체 <- 문자열역직열화 처리 <- 메세지 형태는 문자열
    public void sendMsg(String topic, ProductDetailDto productDetailDto) throws JsonProcessingException {
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(productDetailDto));
    }
}
