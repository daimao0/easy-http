package com.cy.easyhttp.util;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.IOException;

/**
 * 自定义请求体，兼容 OkHttp 3.x 版本
 *
 * @author cy
 * @since v1.5.0
 */
public class JsonRequestBody extends RequestBody {
    private final String content;
    private final MediaType mediaType;
    // 缓存内容长度
    private final long contentLength;

    public JsonRequestBody(String content, MediaType mediaType) {
        this.content = content;
        this.mediaType = mediaType;
        // 计算长度
        this.contentLength = content.getBytes().length;
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() { // 新增此方法
        return contentLength;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.writeUtf8(content);
    }
}