package com.dnf.toolkit.npk.enums;

import lombok.Getter;

/**
 * IMG 版本
 *
 * @author CN
 */

@Getter
public enum ImgVersion {
    Other(0x00),
    Ver1(0x01),
    Ver2(0x02),
    Ver4(0x04),
    Ver5(0x05),
    Ver6(0x06),
    Ver7(0x07),
    Ver8(0x08),
    Ver9(0x09);

    private final int value;

    ImgVersion(int value) {
        this.value = value;
    }

    public static ImgVersion of(int value) {
        for (ImgVersion version : values()) {
            if (version.getValue() == value) {
                return version;
            }
        }
        return Other;
    }

}
