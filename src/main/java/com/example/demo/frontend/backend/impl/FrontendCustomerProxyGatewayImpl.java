package com.example.demo.frontend.backend.impl;

import com.example.demo.common.GatewayImpl;
import com.example.demo.frontend.backend.domain.FrontendCustomerGateway;
import com.example.demo.frontend.backend.domain.FrontendInterface;
import com.example.demo.frontend.backend.event.CreateCustomerCommand;
import com.example.demo.frontend.backend.event.CreateCustomerError;
import com.example.demo.frontend.backend.event.CustomerCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.frontend.backend.impl.FrontendChannels.FRONTEND_IN;

@SuppressWarnings("ALL")
@Service
@Slf4j
@RequiredArgsConstructor
@Profile({"sync"})
class FrontendCustomerProxyGatewayImpl extends GatewayImpl<FrontendInterface> implements FrontendCustomerGateway {

	private final RestTemplate restTemplate;

	public int sendCreateCustomerCommand(CreateCustomerCommand command) {
		log.info("sendCreateCustomerCommand HTTP {}", command);

		int customerId = restTemplate.postForObject("http://localhost:8081/customers", command, Integer.class);
		return customerId;
	}

	@StreamListener(target = FRONTEND_IN, condition = "headers['type']=='CustomerCreatedEvent'")
	public void onCreateCustomerSuccess(Message<CustomerCreatedEvent> success) {
		log.info("onCreateCustomerSuccess: {}", success);

		businessInterface.onCreateCustomerReply(success.getPayload(), null);
	}

	@StreamListener(target = FRONTEND_IN, condition = "headers['type']=='CreateCustomerError'")
	public void onCreateCustomerError(Message<CreateCustomerError> error) {
		log.info("onCreateCustomerError: {}", error);

		businessInterface.onCreateCustomerReply(null, error.getPayload());
	}
}