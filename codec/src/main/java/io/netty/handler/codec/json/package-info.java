/*
 * Copyright 2014 The Netty Project
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
 * <a href="https://en.wikipedia.org/wiki/JSON">JSON</a> specific codecs.
 *
 * JSON（JavaScript Object Notation，JS 对象简谱）是一种轻量级的数据交换格式。它基于ECMAScript（欧洲计算机协会指定的JS 规范）的一个子集，
 * 采用完全独立于编程语言的文本格式来存储和表示数据。简洁和清晰的层次结构使得JSON 称为理想的数据交换语言。
 *
 * JSON 协议是一种文本协议，易于人阅读和编写，同时也易于机器解析和生成，并能有效地提升网络传输效率。
 *
 * XML 也是一种常用的文本协议，XML 和JSOn 都使用结构化方法来标记数据。和XML 相比，JSON 作为数据包格式传输的时候具有更高的效率，这是因为JSON 不像
 * XML 那样需要有严格的闭合标签，这就让有效数据量与总数据包比大大提升，从而同等数据流量的情况下，JSON 减少网络的传输压力。
 *
 * JSON 的语法格式和清晰的层次结构非常简单，明显要比XML 容易阅读，并且在数据交换方面，由于JSON 所使用的字符要比XML 少得多，可以大大得节约传输数据
 * 所占用的宽带。
 *
 * Java 处理JSON 数据有三个比较流行的开源库有：阿里的FastJson、谷歌的Gson 和开源社区的Jackson
 *
 * Jackson 是一个简单的、基于Java的JSON 开源库。使用Jackson 开源库，可以轻松地将Java POJO 对象转换为JSON、XML 格式字符串；同样也可以方便地
 * 将JSON、XML 字符串转换成Java POJO 对象。Jackson 开源库的优点是：依赖的Jar 包较少、简单易于、性能也还不错，另外Jackson 社区相对比较活跃。
 * Jackson 开源库的缺点是：对于复杂POJO 类型、复杂的集合Map、List 的转换结果，不是标准的JSON 格式，或者会出现一些问题。
 *
 * Google 的Gson 开源库是一个功能齐全的JSON 解析库，起源于Google 公司内部需求而由Google 自行研发而来，在2008年5月公开发布第一版之后已经被许多
 * 公司或用户应用。Gson 可以完成复杂类型的POJO 和JSOn 字符串的相互转换，转换的能力非常强。
 *
 * 阿里巴巴的FastJson 是一个高性能的JSON 库。顾名思义，FastJson 库采用独创的快速算法，将JSON 转换成POJO 的速度提升到极致，从性能上说，其反序列化
 * 的速度超过其他JSON 开源库。传闻FastJson 在复杂类型的POJO 转换JSON（序列化）时，可能会出现一些引用类型问题导致JSON 转换出错，需要进行引用的定制。
 *
 *
 * 目前来说，以不变应万变，最佳策略是：业务应用应该兼容主要的Json 组件，根据具体的场景和各种突发事件，能够进行灵活、快速的组件切换。工具类 + 策略模式
 *
 *
 */
package io.netty.handler.codec.json;
