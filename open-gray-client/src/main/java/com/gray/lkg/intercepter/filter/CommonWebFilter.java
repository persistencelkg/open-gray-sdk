package com.gray.lkg.intercepter.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * @description: 通用web filter：保存请求的快照
 * @author: 李开广
 * @date: 2023/7/14 5:04 PM
 */
public class CommonWebFilter implements Filter {

    private final List<CommonFilterInterceptor> list = new LinkedList<>();

    public CommonWebFilter(List<CommonFilterInterceptor> list) {
        if (Objects.nonNull(list)) {
            list.stream().sorted(Comparator.comparing(CommonFilterInterceptor::order)).forEach(this.list::add);
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        DefaultFilterChain defaultChain = new DefaultFilterChain((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
        defaultChain.process();
    }


    private class DefaultFilterChain implements CommonFilterInterceptor.CommonFilterChain {

        private final HttpServletRequest httpServletRequest;

        private final HttpServletResponse httpServletResponse;

        private final FilterChain filterChain;

        private final Iterator<CommonFilterInterceptor> commonFilterInterceptorIterator = CommonWebFilter.this.list.iterator();

        private final long startTime = System.nanoTime();

        public DefaultFilterChain(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filter) {
            this.httpServletRequest = httpServletRequest;
            this.httpServletResponse = httpServletResponse;
            this.filterChain = filter;
        }

        @Override
        public HttpServletRequest request() {
            return httpServletRequest;
        }

        @Override
        public HttpServletResponse response() {
            return httpServletResponse;
        }

        @Override
        public void process() throws ServletException, IOException {
            if (commonFilterInterceptorIterator.hasNext()) {
                commonFilterInterceptorIterator.next().intercept(this);
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        }

        @Override
        public Duration duration() {
            return Duration.ofNanos(System.nanoTime()  - startTime);
        }
    }

}
