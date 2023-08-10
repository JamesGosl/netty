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

/**
 * Adds support for read and write timeout and idle connection notification
 * using a {@link io.netty.util.Timer}.
 *
 * 通信过程中的心跳发送与心跳检测对于任何长连接的应用来说，都是一个非常基础的功能。如果要理解心跳的重要性，首先需要从网络连接假死的现象开始。
 *
 * 网络连接假死：如果底层的TCP（Socket 连接）已经断开，但是服务端斌没有正常地关闭Socket 套接字，服务端认为这条TCP 连接仍然是存在的，则该连接处于
 * “假死”状态。连接假死的具体表现如下：
 * （1）在服务器端，会有一些处于TCP_ESTABLISHED 状态的“正常”连接。
 * （2）但在客户端，TCP 客户端已经显示连接已经断开。
 * （3）客户端此时虽然可以进行断线重连操作，但是上一次的连接状态依然被服务器端认为有效，并且服务器端的资源得不到正确的释放，包括套接字上下文以及接收/发送缓冲区。
 *
 * 连接假死的情况虽然不多见，但是确实存在。服务器端长时间运行后，会面临大量假死连接得不到正确释放的情况。由于每个连接都会消耗CPU 和内存资源，因此大量
 * 假死的连接会耗光服务器的资源，使得服务器越来越慢，IO 处理效率越来越低，最终导致服务器崩溃。
 *
 * 连接假死通常是由以下多个原因造成的，例如：
 * （1）应用程序出现线程堵塞，无法进行数据的读写；
 * （2）网络相关的设备出现故障，例如网卡、机房故障；
 * （3）网络丢包。公网环境非常容易出现丢包和网络抖动等现象；
 * 解决假死的有效手段是：客户端定时进行心跳检测，服务器端定时进行空闲检测。
 *
 * 空闲检测，就是每隔一段时间，检查子通道是否有数据读写，如果有，则子通道是正常的；如果没有，则I/O 通道被判定为假死，关掉子通道，如有必要再进行重连。
 * 服务器端实现空闲检测，可以使用Netty 自带的IdleStateHandler 空闲状态处理器就可以实现这个功能。
 */
package io.netty.handler.timeout;
