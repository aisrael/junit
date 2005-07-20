package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.Runner;
import org.junit.runner.TestListener;

public class RunnerTest {

	private boolean wasRun;

	public class MyListener implements TestListener {

		int testCount;

		public void testRunStarted(int testCount) {
			this.testCount= testCount;
		}

		public void testRunFinished(Runner runner) {
		}

		public void testStarted(Object test, String name) {
		}

		public void testFailure(Failure failure) {
		}

		public void testIgnored(Object method) {
		}

		public void testFinished(Object test, String name) {
		}
		
	}
	
	public static class Example {
		@Test public void empty() {
		}
	}
	
	@Test public void newTestCount() {
		Runner runner= new Runner();
		MyListener listener= new MyListener();
		runner.addListener(listener);
		runner.run(Example.class);
		assertEquals(1, listener.testCount);
	}
	
	public static class ExampleTest extends TestCase {
		public void testEmpty() {
		}
	}
	
	@Test public void oldTestCount() {
		Runner runner= new Runner();
		MyListener listener= new MyListener();
		runner.addListener(listener);
		runner.run(ExampleTest.class);
		assertEquals(1, listener.testCount);
	}
	
	public static class NewExample {
		@Test public void empty() {
		}
	}
	
	@Test public void testFinished() {
		Runner runner= new Runner();
		wasRun= false;
		TestListener listener= new MyListener() {
			@Override
			public void testFinished(Object test, String name) {
				wasRun= true;
			}
		};
		runner.addListener(listener);
		runner.run(NewExample.class);
		assertTrue(wasRun);
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RunnerTest.class);
	}
}
