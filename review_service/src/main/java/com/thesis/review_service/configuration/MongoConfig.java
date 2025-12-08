package com.thesis.review_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConfig {
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory,
                                       MappingMongoConverter converter) {

        //Remove _class when saving into database
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(factory, converter);
    }
}
