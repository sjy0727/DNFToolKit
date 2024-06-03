package com.dnf.toolkit.npk.enums;

import lombok.Getter;

/**
 * 压缩模式
 *
 * @author CN
 */
@Getter
public enum CompressMode {
    // 图像数据未压缩
    NONE(0x05),
    //图像数据经ZLIB压缩
    ZLIB(0x06),
    DDS_ZLIB(0x07),
    UNKNOWN(0x01);

    private final int value;

    CompressMode(int value) {
        this.value = value;
    }

    public static CompressMode of(int value) {
        for (CompressMode compressMode : values()) {
            if (compressMode.getValue() == value) {
                return compressMode;
            }
        }
        return UNKNOWN;
    }

}
