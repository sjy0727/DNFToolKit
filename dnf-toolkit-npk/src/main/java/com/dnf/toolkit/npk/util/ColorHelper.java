package com.dnf.toolkit.npk.util;

import com.dnf.toolkit.npk.enums.ColorBit;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;

import static cn.hutool.core.util.ByteUtil.bytesToShort;
import static com.dnf.toolkit.helper.BufferHelper.readBytes;

/**
 * 颜色帮助类
 *
 * @author CN
 */
public class ColorHelper {

    /**
     * 将所有ARGB类型的数据转换为ARGB_8888的字节数组
     *
     * @param buffer   缓冲流
     * @param colorBit 颜色位
     * @param target   目标数组
     * @param offset   偏移量
     */
    public static void readBgra(ByteBuffer buffer, ColorBit colorBit, byte[] target, int offset) {
        byte[] bs;
        byte a = 0, r = 0, g = 0, b = 0;

        switch (colorBit) {
            case ARGB_8888:
                bs = readBytes(buffer, 4);

                b = bs[0];
                g = bs[1];
                r = bs[2];
                a = bs[3];

                break;
            case ARGB_1555:
                bs = readBytes(buffer, 2);

                short pixel = bytesToShort(bs);
                r = (byte) ((pixel & 0x7C00) >> 7);
                g = (byte) ((pixel & 0x03E0) >> 2);
                b = (byte) ((pixel & 0x001F) << 3);
                a = (byte) (bs[1] >> 7);

                break;
            case ARGB_4444:
                bs = readBytes(buffer, 2);

                a = (byte) (bs[1] & 0xf0);
                r = (byte) ((bs[1] & 0xf) << 4);
                g = (byte) (bs[0] & 0xf0);
                b = (byte) ((bs[0] & 0xf) << 4);

                break;
        }

        if (a == 0x00) {
            b = 0;
            g = 0;
            r = 0;
        }

        target[offset] = b;
        target[offset + 1] = g;
        target[offset + 2] = r;
        target[offset + 3] = a;
    }

    /**
     * 将数组转换为bgra数组
     *
     * @param data     字节数组
     * @param width    宽
     * @param height   高
     * @param colorBit 颜色位
     */
    @SneakyThrows
    public static byte[] readBgraBytes(byte[] data, int width, int height, ColorBit colorBit) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte[] content = new byte[width * height * 4];
        for (var i = 0; i < content.length; i += 4) {
            readBgra(buffer, colorBit, content, i);
        }
        return content;
    }

}
