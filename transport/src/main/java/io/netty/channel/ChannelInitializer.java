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

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A special {@link ChannelInboundHandler} which offers an easy way to initialize a {@link Channel} once it was
 * registered to its {@link EventLoop}.
 *
 * Implementations are most often used in the context of {@link Bootstrap#handler(ChannelHandler)} ,
 * {@link ServerBootstrap#handler(ChannelHandler)} and {@link ServerBootstrap#childHandler(ChannelHandler)} to
 * setup the {@link ChannelPipeline} of a {@link Channel}.
 *
 * <pre>
 *
 * public class MyChannelInitializer extends {@link ChannelInitializer} {
 *     public void initChannel({@link Channel} channel) {
 *         channel.pipeline().addLast("myHandler", new MyHandler());
 *     }
 * }
 *
 * {@link ServerBootstrap} bootstrap = ...;
 * ...
 * bootstrap.childHandler(new MyChannelInitializer());
 * ...
 * </pre>
 * Be aware that this class is marked as {@link Sharable} and so the implementation must be safe to be re-used.
 *
 * @param <C>   A sub-type of {@link Channel}
 *
 * Chanel 通道和Handler 业务处理器的关系是：一条Netty 的通道拥有一条Handler 业务处理器流水线，负责装配自己的Handler 业务处理器。装配Handler
 * 的工作，发生在开始工作之前。这就得借助通道的初始化处理器——ChannelInitializer
 */
@Sharable
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
    // We use a Set as a ChannelInitializer is usually shared between all Channels in a Bootstrap /
    // ServerBootstrap. This way we can reduce the memory usage compared to use Attributes.
    private final Set<ChannelHandlerContext> initMap = Collections.newSetFromMap(
            new ConcurrentHashMap<ChannelHandlerContext, Boolean>());

    /**
     * This method will be called once the {@link Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch            the {@link Channel} which was registered.
     * @throws Exception    is thrown if an error occurs. In that case it will be handled by
     *                      {@link #exceptionCaught(ChannelHandlerContext, Throwable)} which will by default close
     *                      the {@link Channel}.
     *
     * initChannel 方法是ChannelInitializer 定义的一个抽象方法，这个抽象方法需要开发人员自己实现。
     *
     * 在通道初始化时，会调用提前注册的初始化处理器的initChannel 方法。比如，在父通道接收到新连接并且要初始化其子通道时，会调用初始化器的initChannel
     * 方法，并且会将新接受的通道作为参数，传递给此方法。
     *
     * 一般来说，initChannel 方法的大致业务代码是：拿到新连接通道作为实际参数，往它的流水线中装配Handler 业务处理器。
     */
    protected abstract void initChannel(C ch) throws Exception;

    @Override
    @SuppressWarnings("unchecked")
    public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // Normally this method will never be called as handlerAdded(...) should call initChannel(...) and remove
        // the handler.
        if (initChannel(ctx)) {
            // we called initChannel(...) so we need to call now pipeline.fireChannelRegistered() to ensure we not
            // miss an event.
            ctx.pipeline().fireChannelRegistered();

            // We are done with init the Channel, removing all the state for the Channel now.
            removeState(ctx);
        } else {
            // Called initChannel(...) before which is the expected behavior, so just forward the event.
            ctx.fireChannelRegistered();
        }
    }

    /**
     * Handle the {@link Throwable} by logging and closing the {@link Channel}. Sub-classes may override this.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (logger.isWarnEnabled()) {
            logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), cause);
        }
        ctx.close();
    }

    /**
     * {@inheritDoc} If override this method ensure you call super!
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isRegistered()) {
            // This should always be true with our current DefaultChannelPipeline implementation.
            // The good thing about calling initChannel(...) in handlerAdded(...) is that there will be no ordering
            // surprises if a ChannelInitializer will add another ChannelInitializer. This is as all handlers
            // will be added in the expected order.
            if (initChannel(ctx)) {

                // We are done with init the Channel, removing the initializer now.
                removeState(ctx);
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        initMap.remove(ctx);
    }

    @SuppressWarnings("unchecked")
    private boolean initChannel(ChannelHandlerContext ctx) throws Exception {
        if (initMap.add(ctx)) { // Guard against re-entrance.
            try {
                initChannel((C) ctx.channel());
            } catch (Throwable cause) {
                // Explicitly call exceptionCaught(...) as we removed the handler before calling initChannel(...).
                // We do so to prevent multiple calls to initChannel(...).
                exceptionCaught(ctx, cause);
            } finally {
                if (!ctx.isRemoved()) {
                    ctx.pipeline().remove(this);
                }
            }
            return true;
        }
        return false;
    }

    private void removeState(final ChannelHandlerContext ctx) {
        // The removal may happen in an async fashion if the EventExecutor we use does something funky.
        if (ctx.isRemoved()) {
            initMap.remove(ctx);
        } else {
            // The context is not removed yet which is most likely the case because a custom EventExecutor is used.
            // Let's schedule it on the EventExecutor to give it some more time to be completed in case it is offloaded.
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    initMap.remove(ctx);
                }
            });
        }
    }
}
