package com.dnf.toolkit.npk.enums;

import lombok.Getter;

/**
 * 颜色系统
 *
 * @author CN
 */
@Getter
public enum ColorBit {
    ARGB_1555(0x0e),
    ARGB_4444(0x0f),
    ARGB_8888(0x10),
    DXT_1(0x12),
    DXT_3(0x13),
    DXT_5(0x14),
    UNKNOWN(0x00);

    private final int value;

    ColorBit(int value) {
        this.value = value;
    }

    public static ColorBit of(int value) {
        for (ColorBit colorBit : values()) {
            if (colorBit.getValue() == value) {
                return colorBit;
            }
        }
        return UNKNOWN;
    }

}
