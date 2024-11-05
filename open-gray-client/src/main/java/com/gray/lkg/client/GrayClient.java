package com.gray.lkg.client;

import java.io.Closeable;
import java.io.IOException;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/17 9:08 PM
 */
public class GrayClient implements Closeable {




    public static GrayClient getInstance(String switchName) {
        return new GrayClient();
    }

    @Override
    public void close() throws IOException {

    }
}
