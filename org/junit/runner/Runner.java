package org.junit.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.junit.internal.runner.JUnit4RunnerStrategy;
import org.junit.internal.runner.PreJUnit4RunnerStrategy;
import org.junit.internal.runner.RunnerStrategy;
import org.junit.internal.runner.TestNotifier;


public class Runner implements TestNotifier {

	private int fCount= 0;
	private int fIgnoreCount= 0;
	private List<Failure> fFailures= new ArrayList<Failure>();
	private List<TestListener> fListeners= new ArrayList<TestListener>();
	private long fRunTime;

	public void run(Class... testClasses) {
		List<RunnerStrategy> strategies= new ArrayList<RunnerStrategy>();
		for (Class<? extends Object> each : testClasses)
			strategies.add(getStrategy(each));
		int count= 0;
		for (RunnerStrategy each : strategies)
			count+= each.testCount();
		for (TestListener each : fListeners)
			each.testRunStarted(count);
		long startTime= System.currentTimeMillis();
		for (RunnerStrategy each : strategies)
			each.run();
		long endTime= System.currentTimeMillis();
		fRunTime+= endTime - startTime;
		for (TestListener each : fListeners)
			each.testRunFinished(this);
	}
	
	@SuppressWarnings("unchecked")
	private RunnerStrategy getStrategy(Class<? extends Object> testClass) {
		if (junit.framework.TestCase.class.isAssignableFrom(testClass))
			return new PreJUnit4RunnerStrategy(this, (Class< ? extends TestCase>) testClass); 
		return new JUnit4RunnerStrategy(this, testClass);
	}

	//TODO: Get rid of this duplication
	public void run(junit.framework.Test test) { 
		PreJUnit4RunnerStrategy runnerStrategy= new PreJUnit4RunnerStrategy(this, test);
		for (TestListener each : fListeners)
			each.testRunStarted(runnerStrategy.testCount());
		long startTime= System.currentTimeMillis();
		runnerStrategy.run();
		long endTime= System.currentTimeMillis();
		fRunTime+= endTime - startTime;
		for (TestListener each : fListeners)
			each.testRunFinished(this);
	}

	public int getRunCount() {
		return fCount;
	}

	public int getFailureCount() {
		return fFailures.size();
	}

	public long getRunTime() {
		return fRunTime;
	}

	public List<Failure> getFailures() {
		return fFailures;
	}

	public int getIgnoreCount() {
		return fIgnoreCount;
	}
	
	public void addListener(TestListener listener) {
		fListeners.add(listener);
	}

	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

	private List<TestListener> getListeners() {
		return fListeners;
	}

	public void fireTestStarted(Object test, String name) {
		fCount++;
		for (TestListener each : getListeners())
			each.testStarted(test, name);
	}

	public void fireTestFailure(Failure failure) {
		fFailures.add(failure);
		for (TestListener each : fListeners)
			each.testFailure(failure);
	}

	public void fireTestIgnored(Method method) {
		fIgnoreCount++;
		for (TestListener each : fListeners)
			each.testIgnored(method);
	}

	public void fireTestFinished(Object test, String name) {
		for (TestListener each : getListeners())
			each.testFinished(test, name);
	}

}
