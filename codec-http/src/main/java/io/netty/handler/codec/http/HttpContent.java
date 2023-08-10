/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelPipeline;

/**
 * An HTTP chunk which is used for HTTP chunked transfer-encoding.
 * {@link HttpObjectDecoder} generates {@link HttpContent} after
 * {@link HttpMessage} when the content is large or the encoding of the content
 * is 'chunked.  If you prefer not to receive {@link HttpContent} in your handler,
 * place {@link HttpObjectAggregator} after {@link HttpObjectDecoder} in the
 * {@link ChannelPipeline}.
 *
 * 是对HTTP 请求体Body 进行封装，本质上就是一个ByteBuf 缓冲区实例。如果ByteBuf 的长度是固定的，则请求的Body 过大，可能包含多个HttpContent。
 * 解码的时候，最后一个解码返回对象为lastHttpContent(空的HttpContent)，表示对Body 的解码已经结束。
 */
public interface HttpContent extends HttpObject, ByteBufHolder {
    @Override
    HttpContent copy();

    @Override
    HttpContent duplicate();

    @Override
    HttpContent retainedDuplicate();

    @Override
    HttpContent replace(ByteBuf content);

    @Override
    HttpContent retain();

    @Override
    HttpContent retain(int increment);

    @Override
    HttpContent touch();

    @Override
    HttpContent touch(Object hint);
}
