package com.shadov.test.hystrix.domain;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.shadov.test.hystrix.model.QueryParams;
import com.shadov.test.hystrix.utils.Sleeping;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;

@Component
public class NameService {
	@Autowired
	private Provider<NamePerRequest> requestScopedProvider;

	@HystrixCommand(threadPoolKey = "S1")
	public Try<List<String>> findNames(QueryParams params) {
		Sleeping.ms(params.getSleep());

		if (Math.random() < params.getError())
			return Try.failure(new IllegalArgumentException("Could not find names"));

		return Try.success(List.of("John", "Rebecca", requestScopedProvider.get().getName()));
	}
}
