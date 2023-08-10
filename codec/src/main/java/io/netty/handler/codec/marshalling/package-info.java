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
 * Decoder and Encoder which uses <a href="https://www.jboss.org/jbossmarshalling">JBoss Marshalling</a>.
 *
 * 如果可以自由的使用外部依赖，那么JBoss Marshalling 将是个理想的选择：它比JDK 序列化最多快3倍，而且也更加紧凑。
 *
 * JBoss Marshalling 官网是这么定义的：JBoss Marshalling 是一种可选的序列化API，它修复了在JDK 序列化API 中所发现的许多问题，同时保留了与
 * java.io.Serializable 及其相关类的兼容性，并添加了几个新的可调优参数以及额外的特性，所有的这些都是可以通过工厂配置（如外部序列化、类/实例查找
 * 表、类解析以及对象替换等）实现可插拔的。
 *
 */
package io.netty.handler.codec.marshalling;
