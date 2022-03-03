package com.nurkiewicz.reactor;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static java.time.Duration.ofSeconds;
import static reactor.test.StepVerifier.withVirtualTime;

public class R070_VirtualClock {

	private static final Logger log = LoggerFactory.getLogger(R070_VirtualClock.class);

	@Test
	public void virtualTime() throws Exception {
		withVirtualTime(this::longRunning)
				.expectSubscription()
				.expectNoEvent(ofSeconds(2))
				.expectNext("OK")
				.expectComplete()
				.verify(ofSeconds(5));
	}

	/**
	 * TODO Apply {@link Mono#timeout(Duration)} of 1 second to a return value from {@link #longRunning()} method and verify it works.
	 * Warning: {@link reactor.test.StepVerifier.LastStep#verifyTimeout(java.time.Duration)} doesn't verify {@link java.util.concurrent.TimeoutException}
	 */
	@Test
	public void timeout() throws Exception {
		//TODO Write whole test :-)
		withVirtualTime(this::longRunning)
				.expectSubscription()
				.expectNoEvent(Duration.ofMillis(1000))
				.expectError(TimeoutException.class)
				.verify(Duration.ofMillis(1000));
	}

	Mono<String> longRunning() {
		return Mono
				.delay(Duration.ofMillis(2000))
				.timeout(Duration.ofMillis(1000))
				.map(x -> "OK");
	}

}
