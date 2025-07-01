package com.comerzzia.pos.core.services.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ModelMapperConfiguration {
//	@Bean
//	@Primary
//	public ModelMapper modelMapper() {		
//		ModelMapper modelMapper = new ModelMapper();		
//		modelMapper.getConfiguration().setSkipNullEnabled(false).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true)
//				.setFullTypeMatchingRequired(true).setFieldMatchingEnabled(true).setDeepCopyEnabled(true).setCollectionsMergeEnabled(true)
//				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
//		return modelMapper;
//	}
	
	@Bean
	@Primary
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();		
		modelMapper.getConfiguration().setSkipNullEnabled(false).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true)
				.setFullTypeMatchingRequired(true).setFieldMatchingEnabled(true).setDeepCopyEnabled(true).setCollectionsMergeEnabled(false)
				.setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.getConfiguration().getConverters().add(0, new MapConverter());
		modelMapper.getConfiguration().getConverters().add(1, new NonMergingArrayConverter());
//		modelMapper.getConfiguration().getConverters().add(2, new NonMergingCollectionConverter());
		return modelMapper;
	}
}
