package com.github.fdkvandr.springwebfluxexample.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static org.springframework.boot.web.error.ErrorAttributeOptions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Order(-2)
@Component
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties webProperties,
            ApplicationContext applicationContext,
            ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(codecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::formatErrorResponse);
    }

    private Mono<ServerResponse> formatErrorResponse(ServerRequest serverRequest) {
        boolean traceParam = serverRequest.queryParam("trace")
                .map(Boolean::parseBoolean)
                .orElse(false);
        ErrorAttributeOptions errorAttributeOptions = traceParam? of(Include.STACK_TRACE) : defaults();

        Map<String, Object> errorAttributes = getErrorAttributes(serverRequest, errorAttributeOptions);
        int status = (int) Optional.ofNullable(errorAttributes.get("status")).orElse(500);
        return ServerResponse.status(status)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorAttributes));
    }
}
