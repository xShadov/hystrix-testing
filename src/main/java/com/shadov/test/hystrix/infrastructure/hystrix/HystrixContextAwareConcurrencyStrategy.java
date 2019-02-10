package com.shadov.test.hystrix.infrastructure.hystrix;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HystrixContextAwareConcurrencyStrategy extends HystrixConcurrencyStrategy {

	private final HystrixConcurrencyStrategy delegate;
	private final Collection<HystrixCallableWrapper> wrappers;

	public HystrixContextAwareConcurrencyStrategy(Collection<HystrixCallableWrapper> wrappers) {
		Assert.notNull(wrappers, "Parameter 'wrappers' can not be null");
		this.wrappers = wrappers;

		this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
		if (this.delegate instanceof HystrixContextAwareConcurrencyStrategy) {
			return;
		}

		HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
		HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
		HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
		HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();

		HystrixPlugins.reset();
		HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
		HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
		HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
		HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
		HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
	}

	@Override
	public <T> Callable<T> wrapCallable(Callable<T> callable) {
		if (callable instanceof HystrixCallableWrapper)
			return callable;

		final Callable<T> wrappedByDelegate = this.delegate != null ? this.delegate.wrapCallable(callable) : callable;

		if (wrappedByDelegate instanceof HystrixCallableWrapper)
			return wrappedByDelegate;

		return new CallableWrapperChain<>(wrappedByDelegate, wrappers.iterator())
				.wrapCallable();
	}

	private static class CallableWrapperChain<T> {
		private final Callable<T> callable;
		private final Iterator<HystrixCallableWrapper> wrappers;

		public CallableWrapperChain(Callable<T> callable, Iterator<HystrixCallableWrapper> wrappers) {
			this.callable = callable;
			this.wrappers = wrappers;
		}

		public Callable<T> wrapCallable() {
			Callable<T> result = callable;
			while (wrappers.hasNext()) {
				result = wrappers.next().wrapCallable(result);
			}
			return result;
		}
	}

	@Override
	public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
											HystrixProperty<Integer> corePoolSize,
											HystrixProperty<Integer> maximumPoolSize,
											HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
											BlockingQueue<Runnable> workQueue) {
		return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
				keepAliveTime, unit, workQueue);
	}

	@Override
	public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
		return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
	}

	@Override
	public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
		return this.delegate.getBlockingQueue(maxQueueSize);
	}

	@Override
	public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
		return this.delegate.getRequestVariable(rv);
	}
}