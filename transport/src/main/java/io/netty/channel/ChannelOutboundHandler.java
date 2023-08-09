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
package io.netty.channel;

import java.net.SocketAddress;

/**
 * {@link ChannelHandler} which will get notified for IO-outbound-operations.
 *
 * ChannelOutboundHandler out-operations
 *
 * 当业务处理完成后，需要操作Java NIO 底层通道时，通过系列的ChannelOutboundHandler 出站处理器，完成Netty 通道到底层通道的操作。
 * 比方说建立底层连接、断开底层连接、写入底层Java NIO通道等。
 *
 * Netty 出站处理的方向：是通过上层Netty 通道，去操作底层Java IO 通道。
 */
public interface ChannelOutboundHandler extends ChannelHandler {
    /**
     * Called once a bind operation is made.
     *
     * @param ctx           the {@link ChannelHandlerContext} for which the bind operation is made
     * @param localAddress  the {@link SocketAddress} to which it should bound
     * @param promise       the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception    thrown if an error occurs
     *
     * 监听地址（IP + 端口）绑定：完成底层Java IO 通道的IP 地址绑定。如果使用TCP 传输协议，这个方法用于服务器端。
     */
    void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception;

    /**
     * Called once a connect operation is made.
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the connect operation is made
     * @param remoteAddress     the {@link SocketAddress} to which it should connect
     * @param localAddress      the {@link SocketAddress} which is used as source on connect
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     *
     * 连接服务端：完成底层Java IO 通道的服务器端的连接操作。如果使用TCP 传输协议，这个方法用于客户端。
     */
    void connect(
            ChannelHandlerContext ctx, SocketAddress remoteAddress,
            SocketAddress localAddress, ChannelPromise promise) throws Exception;

    /**
     * Called once a disconnect operation is made.
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the disconnect operation is made
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     *
     * 断开服务器连接：断开底层Java IO 通道的Socket 连接。如果使用TCP 传输协议，此方法主要用于客户端。
     */
    void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

    /**
     * Called once a close operation is made.
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the close operation is made
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     *
     * 主动关闭通道：关闭底层的通道，例如服务器端的新连接监听通道。
     */
    void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

    /**
     * Called once a deregister operation is made from the current registered {@link EventLoop}.
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the close operation is made
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     */
    void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

    /**
     * Intercepts {@link ChannelHandlerContext#read()}.
     *
     * 出站处理的read 操作，是启动数据读取，或者说开始数据的读取操作，不是实际的数据读取。只有入站处理的read 操作，才真正执行底层读数据。入站read
     * 处理在完成Netty 通道从Java IO 通道的数据读取后，再把数据发射到通道的pipeline，最后数据会依次进入pipeline 的各个入站处理器，最终被入站
     * 处理器的channelRead 方法处理。
     */
    void read(ChannelHandlerContext ctx) throws Exception;

    /**
    * Called once a write operation is made. The write operation will write the messages through the
     * {@link ChannelPipeline}. Those are then ready to be flushed to the actual {@link Channel} once
     * {@link Channel#flush()} is called
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the write operation is made
     * @param msg               the message to write
     * @param promise           the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception        thrown if an error occurs
     *
     * 写数据到底层：完成Netty 通道向底层Java IO 通道的数据写入操作。此方法仅仅是触发一下操作而已，并不是完成实际的数据写入操作。
     */
    void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception;

    /**
     * Called once a flush operation is made. The flush operation will try to flush out all previous written messages
     * that are pending.
     *
     * @param ctx               the {@link ChannelHandlerContext} for which the flush operation is made
     * @throws Exception        thrown if an error occurs
     *
     * 将底层缓冲区的数据腾空，立即写出到对端。
     */
    void flush(ChannelHandlerContext ctx) throws Exception;
}
