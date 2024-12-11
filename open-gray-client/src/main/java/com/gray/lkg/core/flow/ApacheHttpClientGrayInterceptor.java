package com.gray.lkg.core.flow;

import com.gray.lkg.core.AbstractGrayInterceptor;
import com.gray.lkg.core.GrayExecutor;
import com.gray.lkg.core.GraySwitchService;
import lombok.AllArgsConstructor;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 10:38 AM
 */

public class ApacheHttpClientGrayInterceptor extends AbstractGrayInterceptor<HttpEntityEnclosingRequestBase, CloseableHttpResponse> implements HttpRequestInterceptor, HttpResponseInterceptor {

    private HttpClient client;

    public ApacheHttpClientGrayInterceptor(GraySwitchService graySwitchService) {
        super(graySwitchService);
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        // TOOD 应该模仿RestTemplate # ClientHttpRequestInterceptor去定制化，同时需要改底层组件
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {

    }

    @AllArgsConstructor
    private class ApacheHttpClientGrayExecutor implements GrayExecutor<HttpEntityEnclosingRequestBase, CloseableHttpResponse> {
        private final HttpEntityEnclosingRequestBase request;
        private final HttpContext context;

        @Override
        public CloseableHttpResponse execute() throws IOException {
            return (CloseableHttpResponse) ApacheHttpClientGrayInterceptor.this.client.execute(request, context);
        }

        @Override
        public CloseableHttpResponse execute(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase) throws IOException {
            return (CloseableHttpResponse) ApacheHttpClientGrayInterceptor.this.client.execute(request, context);

        }

        @Override
        public CloseableHttpResponse execute(String url) throws IOException {
            try {
                request.setURI(new URI(url));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            return (CloseableHttpResponse) ApacheHttpClientGrayInterceptor.this.client.execute(request, context);
        }
    }
}
