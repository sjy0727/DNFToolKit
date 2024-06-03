package com.dnf.toolkit.pvf.util;

import com.dnf.toolkit.pvf.enums.ScriptType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static cn.hutool.core.io.FileUtil.getSuffix;

/**
 * PVF 帮助类
 *
 * @author CN
 */
public class PvfHelper {

    /**
     * crc解密
     *
     * @param data  字节数组
     * @param crc32 crc32
     */
    public static void crcDecrypt(byte[] data, int crc32) {
        crcDecrypt(ByteBuffer.wrap(data), crc32);
    }

    /**
     * crc解密
     *
     * @param buffer 字节缓冲
     * @param crc32  crc32
     */
    public static void crcDecrypt(ByteBuffer buffer, int crc32) {
//        long key = 0x81A79011L;
//        long xor = key ^ crc32;
        long xor = crc32;
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int bufferSize = buffer.remaining();
        for (int i = 0; i < bufferSize; i += 4) {
            buffer.mark();
            int anInt = buffer.getInt();
            int val = (int) (anInt ^ xor);
            buffer.reset();
            int decrypt = val >>> 6 | (int) ((long) val << (32 - 6));
            buffer.putInt(decrypt);
        }
        buffer.rewind();
    }

    /**
     * 通过文件后缀名获取脚本类型
     *
     * @param path 脚本路径
     */
    public static ScriptType getScriptType(String path) {
        return ScriptType.of(getSuffix(path));
    }

}
