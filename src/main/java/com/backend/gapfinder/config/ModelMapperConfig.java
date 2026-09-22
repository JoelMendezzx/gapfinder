package com.backend.gapfinder.config;

import com.backend.gapfinder.dto.response.MessageBasicDTO;
import com.backend.gapfinder.model.MessageModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

@Bean
public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();

    mapper.addMappings(new PropertyMap<MessageModel, MessageBasicDTO>() {
        @Override
        protected void configure() {
            map().setSenderName(source.getSender().getName()); // ajusta el getter real
        }
    });

    return mapper;
}
}