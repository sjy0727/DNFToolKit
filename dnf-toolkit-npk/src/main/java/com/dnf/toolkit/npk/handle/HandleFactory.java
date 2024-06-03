package com.dnf.toolkit.npk.handle;

import com.dnf.toolkit.npk.enums.ImgVersion;
import lombok.Getter;

/**
 * handle 工厂
 *
 * @author CN
 */

public class HandleFactory {

    public static IHandle of(ImgVersion version) {
        for (HandleType handleType : HandleType.values()) {
            if (handleType.getVersion() == version) {
                return handleType.getHandle();
            }
        }
        return HandleType.Other.getHandle();
    }

    @Getter
    private enum HandleType {
        V1(ImgVersion.Ver1, new V1Handle()),
        V2(ImgVersion.Ver2, new V2Handle()),
        Other(ImgVersion.Other, new OtherHandle());

        private final ImgVersion version;

        private final IHandle handle;

        HandleType(ImgVersion version, IHandle handle) {
            this.version = version;
            this.handle = handle;
        }

    }

}
