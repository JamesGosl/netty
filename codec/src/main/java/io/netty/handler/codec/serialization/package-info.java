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
 * Encoder, decoder and their compatibility stream implementations which
 * transform a {@link java.io.Serializable} object into a byte buffer and
 * vice versa.
 * <p>
 * <strong>Security:</strong> serialization can be a security liability,
 * and should not be used without defining a list of classes that are
 * allowed to be desirialized. Such a list can be specified with the
 * <tt>jdk.serialFilter</tt> system property, for instance.
 * See the <a href="https://docs.oracle.com/en/java/javase/17/core/serialization-filtering1.html">
 * serialization filtering</a> article for more information.
 *
 * @deprecated This package has been deprecated with no replacement,
 * because serialization can be a security liability
 *
 * 我们在开发一些远程过程调用（RPC）的程序时，通常会涉及对象的序列化/反序列化的问题，例如一个“Person” 对象从客户端通过TCP 方式发送到服务端。由于
 * TCP 协议（或者UDP 等类似底层协议）只能发送字节流，所以需要应用层将Java POJO 对象“序列化” 成字节流，发送过去后，数据接受端再将字节流“反序列化”
 * 化成Java POJO 对象即可。
 *
 * 序列化和反序列化 一定会涉及POJO 的编码和格式化，目前可选择的编码方式有：
 * （1）使用JSON。将Java POJO 对象转换成JSON 结构化字符串。基于HTTP 协议，在Web 应用、移动开发方面等，这是常见的编码方式，因为JSON 的可读性较强。
 * 但是它的性能稍差。
 * （2）基于XML。和JSON 一样，数据在序列化成字节流之前都转换成字符串。可读性强，性能差，异构系统、Open API 类型的应用中常用。
 * （3）使用Java 内置的编码和序列化机制，可移植性强，性能稍差，无法跨平台（语言）。
 * （4）开源的二进制序列化/反序列化框架，例如Apache Avro、Apache Thrift、Protobuf 等。前面两个框架和Protobuf 相比，性能非常接近，而且设计
 * 原理如出一辙；其中Avro 在大数据存储（RPC 数据交换、本地存储）时比较常用；Thrift 的亮点在于内置了RPC 机制，所以在开发一些RPC 交互应用时，客户端
 * 和服务器端的开发与部署都非常简单。
 *
 * 如何选择序列化/反序列化框架呢？评价一个序列化框架的优缺点，大概有两个方面着手：
 * （1）结果数据大小，原则上说，序列化后的数据尺寸越小，传输效率越高。
 * （2）结构复杂度，这会影响序列化/反序列化的效率，结构越复杂，越耗时。
 * 理论上来说，对于对性能要求不是太高的服务器程序，可以选择JSON 文本格式的序列化框架；对于性能要求比较高的服务器程序，则应该选择传输效率更高的二进制
 * 序列化框架，建议是Protobuf。
 *
 *
 * 如果你的程序必须要和使用了ObjectOutputStream 和ObjectInputStream 的远程节点交互，并且兼容性也是你最关心的，那么JDK 序列化是正确的选择。
 *
 * 编码和解码是使用非常广泛的概念。放在计算机领域来说，通过Java 的内置序列化功能，把POJO 对象序列化为二进制字节码，属于编码操作。反过来，把二进制
 * 字节码恢复到POJO 对象，属于解码操作。从这个角度来说，序列化可以理解为编码操作的一种，反序列化可以理解为解码操作的一种。但是，编码的目标并不一定
 * 是二进制数据，可以是其他的POJO 对象或者中间数据。
 *
 * JSON 格式是直观的文本序列化方式，在实际的开发中，尤其是在基于RESTFul 进行远程交互的应用开发中使用得非常多。一般来说，在实际开发中使用较多的JSON
 * 开发包是阿里的FastJson、谷歌的Gson、开源社区的Jackson。
 *
 * Protobuf 格式是非直观的二进制序列化方式，效率比较高，主要用于高性能的通信开发。
 */
package io.netty.handler.codec.serialization;
