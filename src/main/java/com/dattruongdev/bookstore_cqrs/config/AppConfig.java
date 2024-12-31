package com.dattruongdev.bookstore_cqrs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.catalina.core.ApplicationContext;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AppConfig {

//    @Bean
//    MongoTemplate mongoTemplate() {
//        return new MongoTemplate();
//    }
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(ObjectId.class, new ToStringSerializer());
    objectMapper.registerModule(module);
    return objectMapper;
}
}
