package com.shadov.test.hystrix.model;

public class QueryParams {
	private double error;
	private int sleep;

	public QueryParams(double error, int sleep) {
		this.error = error;
		this.sleep = sleep;
	}

	public double getError() {
		return error;
	}

	public int getSleep() {
		return sleep;
	}
}
