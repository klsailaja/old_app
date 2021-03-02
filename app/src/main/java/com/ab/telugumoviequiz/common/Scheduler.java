package com.ab.telugumoviequiz.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	private final ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
	private static Scheduler instance = null;
	
	private Scheduler() {
	}
	
	public static Scheduler getInstance() {
		if (instance == null) {
			instance = new Scheduler();
			instance.scheduler.setRemoveOnCancelPolicy(true);
		}
		return instance;
	}
	
	public void submit(Runnable run) {
		scheduler.schedule(run, 10, TimeUnit.MILLISECONDS);
	}
	
	public void submit(Runnable run, long delay, TimeUnit timeUnit) {
		scheduler.schedule(run, delay, timeUnit);
	}
	
	public ScheduledFuture<?> submitRepeatedTask(Runnable run, long initialDelay, long delay, TimeUnit unit) {
		return scheduler.scheduleWithFixedDelay(run, initialDelay, delay, unit);
	}
	
	public void shutDown() {
		scheduler.shutdown();
		try {
			scheduler.awaitTermination(60 * 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ignored) {
		}
	}
}
