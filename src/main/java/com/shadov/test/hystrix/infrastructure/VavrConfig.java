package com.shadov.test.hystrix.infrastructure;

import io.vavr.jackson.datatype.VavrModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VavrConfig {
	@Bean
	public VavrModule vavrModule() {
		return new VavrModule();
	}
}
