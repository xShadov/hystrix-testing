package com.shadov.test.hystrix.infrastructure.hystrix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty(value = "hystrix.wrappers.enabled", matchIfMissing = true)
public class HystrixContextAutoConfiguration {
	@Autowired(required = false)
	private List<HystrixCallableWrapper> wrappers = new ArrayList<>();

	@PostConstruct
	public void configureHystrixConcurencyStrategy() {
		if (!wrappers.isEmpty()) {
			new HystrixContextAwareConcurrencyStrategy(wrappers);
		}
	}
}