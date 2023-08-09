/*
 * Copyright 2013 The Netty Project
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
package io.netty.util;

/**
 * A reference-counted object that requires explicit deallocation.
 * <p>
 * When a new {@link ReferenceCounted} is instantiated, it starts with the reference count of {@code 1}.
 * {@link #retain()} increases the reference count, and {@link #release()} decreases the reference count.
 * If the reference count is decreased to {@code 0}, the object will be deallocated explicitly, and accessing
 * the deallocated object will usually result in an access violation.
 * </p>
 * <p>
 * If an object that implements {@link ReferenceCounted} is a container of other objects that implement
 * {@link ReferenceCounted}, the contained objects will also be released via {@link #release()} when the container's
 * reference count becomes 0.
 * </p>
 *
 * JVM 中使用”计数器“（一种GC 算法）来标记对象是否”不可达“进而回收（注：GC 是Garbage Collection 的缩写，即Java 中的垃圾回收机制），Netty
 * 也使用了这种手段对ByteBuf 的引用进行计数，Netty 的ByteBuf 的内存回收工作是通过引用计数的方式管理的。
 *
 * Netty 之所以采用”计数器“来追踪ByteBuf 的生命周期，一是能对Pooled ByteBuf 的支持，二是能够尽快地”发现“那些可以回收的ByteBuf(非Pooled)，
 * 以便提升ByteBuf 的分配和销毁的效率。
 *
 * 什么是Pooled(池化) 的ByteBuf 缓冲区呢？从Netty 4版本开始，新增了ByteBuf 的池化机制。即创建一个缓冲区对象池，将没有被引用的ByteBuf 对象，
 * 放入对象缓冲池中；当需要时，则重新从对象缓冲池中取出，而不需要重新创建。
 *
 * ByteBuf 引用计数的大致规则如下：在默认情况下，当创建完一个ByteBuf 时，它的引用为1；每次调用retain() 方法，它的引用就加1；每次调用release()
 * 方法，就是将引用计数减一；如果引用为0，再次访问这个ByteBuf 对象，就会抛出异常；如果引用为0，表示这个ByteBuf 没有哪个进程引用它，它占用的内存需要回收。
 * 也就是说：在Netty 中，引用计数为0的缓冲区不能在继续使用。
 *
 * Netty 在缓冲区使用完成后，会调用一次release，就是释放一次。例如在Netty 流水线上，中间所有的Handler 业务处理器处理完ByteBuf 之后直接传递给下一个，
 * 由最后一个Handler 负责调用其release 方法来释放缓冲区的内存空间。
 *
 * 当ByteBuf 的引用计数已经为0，Netty 会进行ByteBuf 的回收。分为两种情况：
 * （1）如果属于Pooled 池化的ByteBuf 内存，回收方法是：放入可以重新分配的ByteBuf 池子，等待下一次分配；
 * （2）Unpooled 未池化的ByteBuf 缓冲区，需要细分为两种情况：如果是堆（heap）结构缓冲，会被JVM 的垃圾回收机制回收；如果是Direct 直接内存的类型，
 *      则会调用本地方法释放外部内存（unsafe.freeMemeory）。
 *
 * 除了通过ByteBuf 成员方法retain 和release 管理引用计数之外，Netty 还提供了一组件用于增加和减少引用计数的通用静态方法：
 * （1）ReferenceCountUtil.retain(object)：增加一次缓冲区引用计数的静态方法，从而防止该缓冲区被释放；
 * （2）ReferenceCountUtil.release(object)：减少一次缓冲区引用计数的静态方法，如果引用计数为0，缓冲区将被释放；
 *
 * Netty 这里采用了引用计数法来控制回收内存，大致规则如下：
 * （1）每个ByteBUf 对象的初始计数为1；
 * （2）调用release 方法计数减以，如果计数为0，ByteBuf 内存被回收；
 * （3）retain和release 这两个方法配套使用，如果retain 方法计数加1后不使用release，即使它handler 调用了release 也不会造成回收；
 * （4）当计数为0时，底层内存会被回收，这时即使ByteBuf 对象还在，其各个方法均无法正常使用；
 */
public interface ReferenceCounted {
    /**
     * Returns the reference count of this object.  If {@code 0}, it means this object has been deallocated.
     */
    int refCnt();

    /**
     * Increases the reference count by {@code 1}.
     *
     * 增加一次缓冲区引用计数的方法，从而防止该缓冲区被释放；
     */
    ReferenceCounted retain();

    /**
     * Increases the reference count by the specified {@code increment}.
     */
    ReferenceCounted retain(int increment);

    /**
     * Records the current access location of this object for debugging purposes.
     * If this object is determined to be leaked, the information recorded by this operation will be provided to you
     * via {@link ResourceLeakDetector}.  This method is a shortcut to {@link #touch(Object) touch(null)}.
     *
     * ResourceLeakDetector 用于调试是否泄露
     */
    ReferenceCounted touch();

    /**
     * Records the current access location of this object with an additional arbitrary information for debugging
     * purposes.  If this object is determined to be leaked, the information recorded by this operation will be
     * provided to you via {@link ResourceLeakDetector}.
     */
    ReferenceCounted touch(Object hint);

    /**
     * Decreases the reference count by {@code 1} and deallocates this object if the reference count reaches at
     * {@code 0}.
     *
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated
     *
     * 减少一次缓冲区引用计数的方法，如果引用计数为0，缓冲区将被释放；
     */
    boolean release();

    /**
     * Decreases the reference count by the specified {@code decrement} and deallocates this object if the reference
     * count reaches at {@code 0}.
     *
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated
     */
    boolean release(int decrement);
}
