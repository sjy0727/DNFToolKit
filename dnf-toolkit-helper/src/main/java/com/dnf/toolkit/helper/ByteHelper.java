package com.dnf.toolkit.helper;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * byte 帮助类
 *
 * @author CN
 */
public class ByteHelper {

    public static String byteToStr(byte[] data) {
        return new String(data);
    }

    public static String byteToStr(byte[] data, Charset charset) {
        return new String(data, charset);
    }

    public static int bytesToInt(byte[] data) {
        return ByteUtil.bytesToInt(data);
    }

    public static long bytesToLong(byte[] data) {
        return ByteUtil.bytesToLong(data);
    }

    public static float bytesToFloat(byte[] data) {
        return ByteUtil.bytesToFloat(data);
    }

    public static byte[] subBytes(byte[] data, int start, int end) {
        return ArrayUtil.sub(data, start, end);
    }

    public static byte[] subBytes(byte[] data, int start) {
        return ArrayUtil.sub(data, start, data.length);
    }

    public static byte[] bytesJoin(byte[]... datas) {
        return ArrayUtil.addAll(datas);
    }

    public static void bytesCopy(byte[] src, byte[] dest, int offset) {
        ArrayUtil.copy(src, 0, dest, offset, src.length);
    }

}
