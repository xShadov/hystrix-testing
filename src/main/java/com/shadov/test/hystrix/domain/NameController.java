package com.shadov.test.hystrix.domain;

import com.shadov.test.hystrix.model.QueryParams;
import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;

@RestController
@RequestMapping("/custom")
public class NameController {
	@Autowired
	private Provider<NamePerRequest> requestScopedProvider;

	@Autowired
	private NameService nameService;

	@GetMapping("/names")
	public List<String> names(QueryParams params) {
		requestScopedProvider.get().setName("Controllinia");
		return nameService.findNames(params)
				.getOrElseThrow(() -> new IllegalStateException("No names found"));
	}
}
