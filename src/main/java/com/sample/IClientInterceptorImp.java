package com.sample;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class IClientInterceptorImp implements IClientInterceptor {
	Instant start;
	Instant stop;

	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		 start = Instant.now();
		
		
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) throws IOException {
		 stop = Instant.now();
		
		
	}
	public Duration getDuration() {
		Duration duration = Duration.between(start, stop);
		return duration;
	}

}
