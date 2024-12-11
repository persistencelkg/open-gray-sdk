package com.gray.lkg.core.flow;

import com.gray.lkg.core.AbstractGrayInterceptor;
import com.gray.lkg.core.GrayExecutor;
import com.gray.lkg.core.GraySwitchService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Description: 基于rest template 拦截器链结构 ClientHttpRequestInterceptor
 * Author: 李开广
 * Date: 2024/11/6 10:18 AM
 */
public class RestTemplateGrayInterceptor extends AbstractGrayInterceptor<HttpRequest, ClientHttpResponse> implements ClientHttpRequestInterceptor {

    public RestTemplateGrayInterceptor(GraySwitchService graySwitchService) {
        super(graySwitchService);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        RestTemplateExecution restTemplateExecution = new RestTemplateExecution(request, body, execution);
        return super.flowIntercept(restTemplateExecution, request.getURI().getPath(), request.getURI().toString());
    }


    @AllArgsConstructor
    private static class RestTemplateExecution implements GrayExecutor<HttpRequest, ClientHttpResponse> {
        private final HttpRequest httpRequest;
        private final byte[] body;
        private ClientHttpRequestExecution execution;

        @Override
        public ClientHttpResponse execute() throws IOException {
            return execution.execute(httpRequest, body);
        }

        @Override
        public ClientHttpResponse execute(HttpRequest httpRequest) throws IOException {
            return execution.execute(httpRequest, body);
        }

        @Override
        public ClientHttpResponse execute(String url) throws IOException {
            return execution.execute(new HttpRequest() {
                @Override
                public String getMethodValue() {
                    return httpRequest.getMethodValue();
                }

                @Override
                public URI getURI() {
                    try {
                        return new URI(url);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public HttpHeaders getHeaders() {
                    return httpRequest.getHeaders();
                }
            }, body);
        }
    }


}
