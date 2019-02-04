package com.shadov.test.hystrix.domain;

import com.shadov.test.hystrix.model.QueryParams;
import com.shadov.test.hystrix.utils.Sleeping;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;

@Component
public class NameService {
	public Try<List<String>> findNames(QueryParams params) {
		Sleeping.ms(params.getSleep());

		if (Math.random() < params.getError())
			return Try.failure(new IllegalArgumentException("Could not find names"));

		return Try.success(List.of("John", "Rebecca"));
	}
}
