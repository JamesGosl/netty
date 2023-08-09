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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;

/**
 * Enables a {@link ChannelHandler} to interact with its {@link ChannelPipeline}
 * and other handlers. Among other things a handler can notify the next {@link ChannelHandler} in the
 * {@link ChannelPipeline} as well as modify the {@link ChannelPipeline} it belongs to dynamically.
 *
 * <h3>Notify</h3>
 *
 * You can notify the closest handler in the same {@link ChannelPipeline} by calling one of the various methods
 * provided here.
 *
 * Please refer to {@link ChannelPipeline} to understand how an event flows.
 *
 * <h3>Modifying a pipeline</h3>
 *
 * You can get the {@link ChannelPipeline} your handler belongs to by calling
 * {@link #pipeline()}.  A non-trivial application could insert, remove, or
 * replace handlers in the pipeline dynamically at runtime.
 *
 * <h3>Retrieving for later use</h3>
 *
 * You can keep the {@link ChannelHandlerContext} for later use, such as
 * triggering an event outside the handler methods, even from a different thread.
 * <pre>
 * public class MyHandler extends {@link ChannelDuplexHandler} {
 *
 *     <b>private {@link ChannelHandlerContext} ctx;</b>
 *
 *     public void beforeAdd({@link ChannelHandlerContext} ctx) {
 *         <b>this.ctx = ctx;</b>
 *     }
 *
 *     public void login(String username, password) {
 *         ctx.write(new LoginMessage(username, password));
 *     }
 *     ...
 * }
 * </pre>
 *
 * <h3>Storing stateful information</h3>
 *
 * {@link #attr(AttributeKey)} allow you to
 * store and access stateful information that is related with a {@link ChannelHandler} / {@link Channel} and its
 * context. Please refer to {@link ChannelHandler} to learn various recommended
 * ways to manage stateful information.
 *
 * <h3>A handler can have more than one {@link ChannelHandlerContext}</h3>
 *
 * Please note that a {@link ChannelHandler} instance can be added to more than
 * one {@link ChannelPipeline}.  It means a single {@link ChannelHandler}
 * instance can have more than one {@link ChannelHandlerContext} and therefore
 * the single instance can be invoked with different
 * {@link ChannelHandlerContext}s if it is added to one or more {@link ChannelPipeline}s more than once.
 * Also note that a {@link ChannelHandler} that is supposed to be added to multiple {@link ChannelPipeline}s should
 * be marked as {@link io.netty.channel.ChannelHandler.Sharable}.
 *
 * <h3>Additional resources worth reading</h3>
 * <p>
 * Please refer to the {@link ChannelHandler}, and
 * {@link ChannelPipeline} to find out more about inbound and outbound operations,
 * what fundamental differences they have, how they flow in a  pipeline,  and how to handle
 * the operation in your application.
 *
 * ChannelHandlerContext 的作用是就是存储ChannelHandler 为双向链表结构 (责任链模式)
 * 同时负责ChannelPipeline 对ChannelHandler 的传播过程
 *
 * 在Netty 的设计中Handler 是无状态的，不能保存和Channel 有关的信息。Handler 的目标，是将自己的处理逻辑做得很通用，可以给不同的Channel 使用。
 * 与Handler 不同的是，Pipeline 是有状态的，它保存了Channel 的关系。于是乎，Handler 和Pipeline 之间，需要有一个中间角色，把它们联系起来。
 * 这就是——ChannelHandlerContext
 *
 * 不管我们定义的是哪种类型的Handler 业务处理器，最终它们都是以双向链表的方式保存在流水线中。这里流水线的节点类型，并不是前面的Handler 业务处理器基类，
 * 而是其包装类型：ChannelHandlerContext 通道处理器上下文。当Handler 业务处理器被添加到流水线中时，会为其专门创建一个通道处理器上下文
 * ChannelHandlerContext 实例，主要封装了ChannelHandler 通道处理器和ChannelPipeline 通道流水线之间的关联关系。
 *
 * 所以流水线ChannelPipeline 中的双向链表，实质是一个由ChannelHandlerContext 组成的双向链表。而无状态的Handler，作为Context 的成员，关联在
 * ChannelHandlerContext 中。
 *
 * ChannelHandlerContext 中包含了有许多方法，主要可以分为两类：第一类是获取上下文所关联的Netty 组件实例，如所关联的通道、所关联的流水线、
 * 上下文内部Handler 业务处理器实例等；第二类是入站和出站处理方法。
 *
 * 如果通道Channel 或ChannelPipeline 的实例来调用这些出站和入站处理方法，它们就会在整条流水线中传播。然而，如果是通过ChannelHandlerContext
 * 上下文调用出站和入站处理方法，就只会从当前的节点开始，往同类型的下一站处理器传播，而不是在整条流水线从头至尾进行完整的传播。
 *
 * 总结一下Channel、Handler、ChannelHandlerContext 三者的关系：Channel 通道拥有一条ChannelPipeline 通道流水线，每一个流水线节点为一个
 * ChannelHandlerContext 上下文对象，每一个上下文中包裹了一个ChannelHandler 通道处理器。在ChannelHandler 通道处理器的入站/出站处理方法中，
 * Netty 都会传递一个Context 上下文实例作为实际参数。处理器中的回调代码，可以通过Context 实参，在业务处理过程中去获取ChannelPipeline 实例或者
 * Channel 实例。
 *
 * 实际上，通道流水线在没有加入任何处理器之前，装配了两个默认的处理器山下文：一个头部上下文叫做HeadContext、一个尾部上下文叫做TailContext。
 * pipeline 的创建、初始化除了保存一些必要的属性外，核心就在于创建了HeadContext 头节点和TailContext 尾节点。
 *
 * 每个pipeline 中双向链表结构，从一开始就存在HeadContext 和TailContext 两个节点，后面添加的处理器上下文节点，都添加在HeadContext 实例和
 * TailContext 实例之间。
 */
public interface ChannelHandlerContext extends AttributeMap, ChannelInboundInvoker, ChannelOutboundInvoker {

    /**
     * Return the {@link Channel} which is bound to the {@link ChannelHandlerContext}.
     */
    Channel channel();

    /**
     * Returns the {@link EventExecutor} which is used to execute an arbitrary task.
     */
    EventExecutor executor();

    /**
     * The unique name of the {@link ChannelHandlerContext}.The name was used when then {@link ChannelHandler}
     * was added to the {@link ChannelPipeline}. This name can also be used to access the registered
     * {@link ChannelHandler} from the {@link ChannelPipeline}.
     */
    String name();

    /**
     * The {@link ChannelHandler} that is bound this {@link ChannelHandlerContext}.
     */
    ChannelHandler handler();

    /**
     * Return {@code true} if the {@link ChannelHandler} which belongs to this context was removed
     * from the {@link ChannelPipeline}. Note that this method is only meant to be called from with in the
     * {@link EventLoop}.
     */
    boolean isRemoved();

    @Override
    ChannelHandlerContext fireChannelRegistered();

    @Override
    ChannelHandlerContext fireChannelUnregistered();

    @Override
    ChannelHandlerContext fireChannelActive();

    @Override
    ChannelHandlerContext fireChannelInactive();

    @Override
    ChannelHandlerContext fireExceptionCaught(Throwable cause);

    @Override
    ChannelHandlerContext fireUserEventTriggered(Object evt);

    @Override
    ChannelHandlerContext fireChannelRead(Object msg);

    @Override
    ChannelHandlerContext fireChannelReadComplete();

    @Override
    ChannelHandlerContext fireChannelWritabilityChanged();

    @Override
    ChannelHandlerContext read();

    @Override
    ChannelHandlerContext flush();

    /**
     * Return the assigned {@link ChannelPipeline}
     */
    ChannelPipeline pipeline();

    /**
     * Return the assigned {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     *
     * 获取通道缓冲区分配器
     */
    ByteBufAllocator alloc();

    /**
     * @deprecated Use {@link Channel#attr(AttributeKey)}
     */
    @Deprecated
    @Override
    <T> Attribute<T> attr(AttributeKey<T> key);

    /**
     * @deprecated Use {@link Channel#hasAttr(AttributeKey)}
     */
    @Deprecated
    @Override
    <T> boolean hasAttr(AttributeKey<T> key);
}
