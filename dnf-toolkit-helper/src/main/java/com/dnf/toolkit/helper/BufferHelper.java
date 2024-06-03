package com.dnf.toolkit.helper;

import lombok.SneakyThrows;

import java.nio.ByteBuffer;

import static com.dnf.toolkit.helper.ByteHelper.byteToStr;


/**
 * buffer 帮助类
 *
 * @author CN
 */
public class BufferHelper {

    /**
     * 读取int
     *
     * @param buffer 字节缓冲区
     */
    public static int readInt(ByteBuffer buffer) {
        return buffer.getInt();
    }

    /**
     * 读取int
     *
     * @param buffer 字节缓冲区
     * @param length 字节长度
     */
    public static int readInt(ByteBuffer buffer, int length) {
        return ByteHelper.bytesToInt(readBytes(buffer, length));
    }

    /**
     * 读取short
     *
     * @param buffer 字节缓冲区
     */
    public static int readShort(ByteBuffer buffer) {
        return buffer.getShort();
    }

    /**
     * 读取字符串
     *
     * @param buffer 字节缓冲区
     * @param length 字节长度
     */
    public static String readStr(ByteBuffer buffer, int length) {
        return ByteHelper.byteToStr(readBytes(buffer, length));
    }

    /**
     * 读取字节数组
     *
     * @param buffer 字节缓冲区
     * @param length 字节长度
     */
    @SneakyThrows
    public static byte[] readBytes(ByteBuffer buffer, int length) {
        byte[] dst = new byte[length];
        buffer.get(dst, 0, length);
        return dst;
    }

}
