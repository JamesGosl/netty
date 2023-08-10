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
 * Encoder and decoder which transform a
 * <a href="https://github.com/google/protobuf">Google Protocol Buffers</a>
 * {@link com.google.protobuf.Message} and {@link com.google.protobuf.nano.MessageNano} into a
 * {@link io.netty.buffer.ByteBuf} and vice versa.
 *
 * Protobuf 是一个高性能、易扩展的序列化框架，性能比较高，其性能的有关的数据可以参考官方文档。Protobuf 本身非常简单，易于开发，而且结合Netty 框架，
 * 可以非常便捷地实现一个通信应用程序。反过来，Netty 也提供了相关的编解码器，为Protobuf 解决了有关Socket 通信中“半包、粘包”等问题。
 *
 * Protobuf 以一种紧凑而高效的方式对结构化的数据进行编码以及解码。它具有许多的编程语言绑定，使得它很适合跨语言的项目。
 *
 * Protobuf 全称Google Protocol Buffer，Google 提出的一种数据交换格式，是一套类似JSON 或者XML 的数据传输格式和规范，用于不同应用或进程之间
 * 进行通讯。Protobuf 具有以下特点：
 * （1）语言无关，平台无关：Protobuf 支持Java、C++、Python、JavaScript 等多种语言，支持跨多个平台。
 * （2）高效：比XML 更小（3~10倍），更快（20~100倍），更为简单。
 * （3）扩展性，兼容性好：可以更新数据结构，而不影响和破坏原有的旧程序。
 *
 * Protobuf 即独立于语言，有独立于平台。Google 官方提供了多种语言的实现：Java、C#、C++、Go、JavaScript 和Python。Protobuf 的编码过程为：
 * 使用预先定义的Message 数据结构将事件的传输数据进行打包，然后编码成二进制的码流进行传输或者存储。Protobuf 的解码过程则刚好与编码过程相反：将二
 * 进制码流解码成Protobuf 自己定义的Message 结构的POJO 实例。
 *
 * 与JSON、XML 相比，Protobuf 算是后起之秀，只是Protobuf 更加适合高性能、快速响应的数据传输应用场景。Protobuf 数据包是一种二进制的格式，相对于
 * 文本格式的数据交换（JSON、XML）来说，速度要快很多。由于Protobuf 优秀的性能，使得它更加适合用于分布式应用场景下的数据通信或者异构环境下的数据交换。
 *
 * 另外，JSON、XML 是文本格式，数据具有可读性；而Protobuf 是二进制数据格式，数据本身不具有可读性，只是反序列化之后才能得到真正可读的数据。正因为
 * Protobuf 是二进制数据格式，数据序列化之后，体积相比JSOn 和XML 要小，更加适合网络传输。
 *
 * 总体来说，在一个需要大量数据传输的应用场景中，因为数据量很大，那么选择Protobuf 可以明显地减少传输的数据量和提升网络I/O 的速度。对于打造一款高性能
 * 的通信服务器来说，Protobuf 传输协议是最高性能的传输协议之一。微信的消息传输就采用了Protobuf 协议。
 */
package io.netty.handler.codec.protobuf;
