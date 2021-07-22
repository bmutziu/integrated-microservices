package com.example.demo.frontend.backend.event;

import com.example.demo.common.CloudEvent;
import com.example.demo.common.Data;
import lombok.Value;

public class CreateCustomerError extends CloudEvent<CreateCustomerError.Payload> {

    public CreateCustomerError(String error, int code) {
        this.setData(new Payload(error, code));
        setEntity("Customer");
    }

    @Value
    public static class Payload extends Data {
        String error;
        int code;
    }
}