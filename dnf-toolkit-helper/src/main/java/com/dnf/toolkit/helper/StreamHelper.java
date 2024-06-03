package com.dnf.toolkit.helper;

import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamHelper {

    @SneakyThrows
    public static String readStr(InputStream in) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            int j = 0;
            while ((j = in.read()) != 0 && j != -1) {
                os.write(j);
            }
            return ByteHelper.byteToStr(os.toByteArray());
        }
    }

    public static String readStr(InputStream in, int length) {
        return ByteHelper.byteToStr(readBytes(in, length));
    }

    public static int readInt(InputStream in) {
        return ByteHelper.bytesToInt(readBytes(in, 4));
    }

    public static long readLong(InputStream in) {
        return ByteHelper.bytesToLong(readBytes(in, 8));
    }

    @SneakyThrows
    public static byte[] readBytes(InputStream in, int length) {
        return IoUtil.readBytes(in, length);
    }

    @SneakyThrows
    public static void seekBytes(InputStream in, int pos) {
        in.skipNBytes(pos);
    }

}
