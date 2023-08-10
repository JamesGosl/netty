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
 * Encoder, decoder and their related message types for HTTP.
 *
 * HTTP 超文本传输协议，是一个基于请求与响应的、无状态的应用层的协议，是互联网上应用最为广泛的一种网络协议，所有的WWW 文件都必须遵守这个标准。设计
 * HTTP 的初衷是为了提供一种发布和接收HTML 页面的方法。
 *
 * 关于TCP/IP 和HTTP 协议的关系，大致可以描述为：在传输数据时，应用程序之间可以只使用TCP/IP（传输层）协议，但是那样的话，如果没有应用层，应用程序
 * 便无法识别数据内容。如果想要使传输的数据由意义，则必须使用到应用层协议。应用层协议有很多，比如HTTP、FTP、TELNET 等，也可以自己定义应用层协议。
 *
 * HTTP 是一个属于应用层的面向对象协议，由于其简捷、快速的方式，适用于分布式超媒体信息系统，是互联网上应用最为广泛的一种网络协议。所有的WWW 文件都
 * 必须遵循这个标准。1960年美国人Ted Nelson 构思了一种通过计算机处理文本信息的方法，并称之为超文本（Hyper Text），这称为了HTTP 超文本传输协议
 * 标准架构的发展根基。最终，万维网协会（World Wide Web Consortium）和互联网工程工作小组（Internet Engineering Task Force）共同合作研究
 * HTTP 协议，最终发布了一系列的RFC 文档，其中著名的RFC 2616 定义了HTTP 1.1协议。
 *
 * HTTP 协议的主要特点如下：
 * （1）支持客户端/服务器模式。
 * （2）简单快速：客户端向服务器请求服务时，只需传送请求方法和路径。请求方法常用的有GET、HEAD、POST，每种方法都规定了客户与服务器联系的类型不同；
 * 由于HTTP 协议简单，使得HTTP 服务器的程序规模小，因而通信速度很快。
 * （3）灵活：HTTP 允许传输任意类型的数据对象，数据的类型由Content-Type 加以标记。
 * （4）无连接：每次连接只处理一个请求，服务器处理完客户的请求，并收到客户的应答后，即断开连接。
 * （5）无状态：无状态是指协议对于事务处理没有记忆能力。如果后续处理需要前面的信息，则它必须重传，这样可能导致每次连接传送的数据量增大。另一方面，在
 * 服务器不需要先前信息时它的应答就较快。
 * 总之，HTTP 协议是请求-响应模式的协议，客户端发送一个HTTP 请求，服务就响应此请求。
 *
 * 众所周知HTTP 是超文本传输协议，在HTTP 协议中信息是明文传输的，因此为了通信安全就有了HTTPS 协议。HTTPS 也是一种超文本传输协议。HTTPS 在HTTP
 * 的基础上加入了SSL/TLS 协议，SSL/TLS 依靠证书来验证服务端的身份，并为浏览器和服务端之间的通信加密。
 *
 * HTTPS 是一种通过计算机网络进行安全通信的传输协议，该协议使用HTTP 协议进行通信，借助SSL/TLS 建立安全通道和加密数据包。使用HTTPS 的主要目的：
 * 提供对网站服务端的身份认证，同时保护交换数据的隐私和安全性。
 *
 * TLS 是传输层加密协议，前身是SSL 协议，由网景公司1995年发布，有时候TLS 和SSL 两者不做太多区分。
 *
 * SSL 是“Secure Sockets Layer” 的缩写，中文可以译为“安全套接层”。它是1994年由网景公司为Netscape Navigator 浏览器设计和研发的安全传输技术。
 * Netscape Navigator 浏览器是著名浏览器Firefox 的前身，Firefox 是继Chrome 和Safari 之后最受欢迎的浏览器，其前身Netscape Navigator 浏览器，
 * 在90年代它是互联网yoghurt中最受欢迎的浏览器。
 */
package io.netty.handler.codec.http;
