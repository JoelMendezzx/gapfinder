package com.backend.gapfinder.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backend.gapfinder.entities.message.MessageBasicDTO;
import com.backend.gapfinder.entities.message.MessageEntity;

@Configuration
public class ModelMapperConfig {

@Bean
public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();

    mapper.addMappings(new PropertyMap<MessageEntity, MessageBasicDTO>() {
        @Override
        protected void configure() {
            map().setSenderName(source.getSender().getName()); // ajusta el getter real
        }
    });

    return mapper;
}
}