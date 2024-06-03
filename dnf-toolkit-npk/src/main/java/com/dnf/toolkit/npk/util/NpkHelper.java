package com.dnf.toolkit.npk.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import java.nio.charset.StandardCharsets;

/**
 * @author CN
 */
public class NpkHelper {

    private static final byte[] DECRYPT_KEY = ("puchikon@neople dungeon and fighter " +
            "DNFDNFDNFDNFDNFDNFDNFDNFDNFDNF" +
            "DNFDNFDNFDNFDNFDNFDNFDNFDNFDNF" +
            "DNFDNFDNFDNFDNFDNFDNFDNFDNFDNF" +
            "DNFDNFDNFDNFDNFDNFDNFDNFDNFDNF" +
            "DNFDNFDNFDNFDNFDNFDNFDNFDNFDNF" +
            "DNFDNFDNFDNFDNFDNFDNFDNFDNFDNF" +
            "DNFDNFDNFDNFDNFDNFDNFDNFDNFDNF" +
            "DNFDNFDNF\0").getBytes(StandardCharsets.UTF_8);

    /**
     * 解密字符串
     *
     * @param data 待解密字节数组
     */
    public static String decryptBytesToStr(byte[] data) {
        byte[] decryptBytes = new byte[256];
        for (int i = 0; i < data.length; i++) {
            decryptBytes[i] = (byte) (data[i] ^ DECRYPT_KEY[i]);
        }
        return new String(decryptBytes).replace("\0", "");
    }

    /**
     * 计算校验码
     *
     * @param source 源数据
     */
    public static byte[] compileHash(byte[] source) {
        if (source.length < 1) {
            return new byte[0];
        }
        int count = source.length / 17 * 17;
        byte[] data = new byte[count];
        ArrayUtil.copy(source, 0, data, 0, count);
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        return digester.digest(data);
    }

}
