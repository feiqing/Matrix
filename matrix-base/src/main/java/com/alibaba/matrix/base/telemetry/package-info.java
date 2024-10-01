package com.alibaba.matrix.base.telemetry;
/**
 * 提供Tracer(Span)的极简接口, 由于该领域国内尚未有大规模推广的标准(如日志领域的slf4j), (open-telemetry)尚未在国内大规模推广
 * 因此提供一个极简的接口, 方便matrix项目开发的同时, 也方便后续接入各家不同公司的实现(如淘宝鹰眼、点评cat、open-telemetry-tracer)
 *
 *
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/5 16:30.
 */