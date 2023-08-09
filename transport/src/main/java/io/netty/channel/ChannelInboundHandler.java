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

/**
 * {@link ChannelHandler} which adds callbacks for state changes. This allows the user
 * to hook in to state changes easily.
 *
 * ChannelInboundHandler in-operations
 *
 * 当对端数据入站到Netty 通道时，Netty 将触发入站处理器ChannelInboundHandler 所对应的入站API，进行入站操作处理。
 */
public interface ChannelInboundHandler extends ChannelHandler {

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered with its {@link EventLoop}
     *
     * 当通道注册完成后，Netty 会调用fireChannelRegistered 方法，触发通道注册事件。而在通道流水线注册过的入站处理器的channelRegistered
     * 回调方法，将会被调用到。
     *
     * 当通道成功绑定一个Reactor(EventLoop) 反应器后，此方法将被回调。
     */
    void channelRegistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was unregistered from its {@link EventLoop}
     */
    void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} is now active
     *
     * 当通道激活完成后，Netty 会调用fireChannelActive 方法，触发通道激活事件。而在通道流水线注册过的入站处理器的channelActive 回调方法，
     * 会被调用到。
     *
     * 当通道激活成功后，此方法将被回调。通道激活成功指的是，所有的业务处理器添加、注册的异步任务完成，并且与Reactor(EventLoop) 反应器绑定的
     * 异步任务完成。
     */
    void channelActive(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered is now inactive and reached its
     * end of lifetime.
     *
     * 当连接被断开或者不可用时，Netty 会调用fireChannelInactive，触发连接不可用事件。而在通道流水线注册过的入站处理器的channelInactive
     * 回调方法，会被调用到。
     *
     * 当通道的底层连接已经不是ESTABLISH 状态，或者底层连接已经关闭时，会首选回调所有业务处理器的channelInactive 方法。
     */
    void channelInactive(ChannelHandlerContext ctx) throws Exception;

    /**
     * Invoked when the current {@link Channel} has read a message from the peer.
     *
     * 当通道缓冲区可读，Netty 的反应器完成数据读取后，会调用fireChannelRead 发射读取到的二进制数据。而在通道流水线注册过的入站处理器的channelRead
     * 回调方法，会被调用到，以便完成入站数据的读取和处理。
     *
     * 有数据包入站，通道可读。流水线会启动入站处理流程，从前向后，入站处理器的channelRead 方法会被依次回调到（因为是pipeline 责任链）。
     */
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    /**
     * Invoked when the last message read by the current read operation has been consumed by
     * {@link #channelRead(ChannelHandlerContext, Object)}.  If {@link ChannelOption#AUTO_READ} is off, no further
     * attempt to read an inbound data from the current {@link Channel} will be made until
     * {@link ChannelHandlerContext#read()} is called.
     *
     * 当通道缓冲区读完，Netty 会调用fireChannelReadComplete，触发通道缓冲区读完事件。而在通道流水线注册过的入站处理器的channelReadComplete
     * 回调方法，会被调用到。
     *
     * 流水线完成入站处理后，会从前往后，依次回调每个入站处理器的channelReadComplete 方法，表示数据读取完毕。
     */
    void channelReadComplete(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if an user event was triggered.
     */
    void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    /**
     * Gets called once the writable state of a {@link Channel} changed. You can check the state with
     * {@link Channel#isWritable()}.
     */
    void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if a {@link Throwable} was thrown.
     *
     * 当通道处理过程发生异常时，Netty 会调用fireExceptionCaught，触发异常捕获事件。而在通道流水线注册过的入站处理器的exceptionCaught 方法，
     * 会被调用到。注意，这个方法是在通道处理器ChannelHandler 定义的方法，入站处理器、出站处理器都继承到了该方法。
     */
    @Override
    @SuppressWarnings("deprecation")
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
