package com.example.msasbproducts.kafka;

import com.example.msasbproducts.dto.ProductDetailDto;
import com.example.msasbproducts.dto.ProductReqDto;
import com.example.msasbproducts.dto.WishlistDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestKafProducer {
    private static final Logger logger = LoggerFactory.getLogger(TestKafProducer.class);

    @Autowired
    private static KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private static ObjectMapper objectMapper;

    public void createPdt(String topic, ProductDetailDto productDetailDto) throws JsonProcessingException {
        logger.info("상품 등록 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(SendPdtDto.builder()
                        .email(productDetailDto.getEmail())
                        .ptId(productDetailDto.getPdtId())
                        .pdtName(productDetailDto.getPdtName())
                        .price(Float.valueOf(productDetailDto.getPdtPrice()))
//                        .imageUrl(productDetailDto.getImageUrls())
                        .build()));
        logger.info("상품 등록 메세지 전송 완료");
    }

    public void deletePdt(String topic, ProductReqDto productReqDto) throws JsonProcessingException {
        logger.info("상품 삭제 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(productReqDto));
        logger.info("상품 삭제 메세지 전송 완료");
    }

    public void wishPdt(String topic, WishlistDto wishlistDto) throws JsonProcessingException {
        logger.info("찜 등록 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(wishlistDto));
        logger.info("찜 등록 메세지 전송 완료");
    }

    public void wishPdtDelete(String topic, Integer productReqDto) throws JsonProcessingException {
        logger.info("찜 삭제 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(productReqDto));
        logger.info("찜 삭제 메세지 전송 완료");
    }

}
