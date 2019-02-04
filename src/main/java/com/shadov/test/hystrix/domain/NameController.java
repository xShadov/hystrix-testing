package com.shadov.test.hystrix.domain;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.shadov.test.hystrix.model.QueryParams;
import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/custom")
public class NameController {

	@Autowired
	private NameService nameService;

	@GetMapping("/names")
	@HystrixCommand
	public List<String> names(QueryParams params) {
		return nameService.findNames(params)
				.getOrElseThrow(() -> new IllegalStateException("No names found"));
	}
}
