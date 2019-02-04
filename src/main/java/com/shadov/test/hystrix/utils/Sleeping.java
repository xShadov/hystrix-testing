package com.shadov.test.hystrix.utils;

public class Sleeping {
	public static void ms(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ignored) {
		}
	}
}
