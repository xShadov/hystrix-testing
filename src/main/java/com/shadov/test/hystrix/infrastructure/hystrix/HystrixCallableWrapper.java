package com.shadov.test.hystrix.infrastructure.hystrix;

import java.util.concurrent.Callable;

public interface HystrixCallableWrapper {
	<T> Callable<T> wrapCallable(Callable<T> callable);
}
