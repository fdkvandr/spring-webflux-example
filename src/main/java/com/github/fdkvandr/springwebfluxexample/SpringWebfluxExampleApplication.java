package com.github.fdkvandr.springwebfluxexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringWebfluxExampleApplication {

	static {
		BlockHound.install(
				builder -> builder
						.allowBlockingCallsInside("java.io.InputStream", "readNBytes")
						.allowBlockingCallsInside("java.io.FilterInputStream", "read")
						.allowBlockingCallsInside("java.lang.reflect.Method", "invoke")
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxExampleApplication.class, args);
	}
}
