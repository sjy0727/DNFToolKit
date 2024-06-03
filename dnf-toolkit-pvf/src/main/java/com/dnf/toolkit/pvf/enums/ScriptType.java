package com.dnf.toolkit.pvf.enums;

import com.dnf.toolkit.pvf.parser.*;
import lombok.Getter;

/**
 * 脚本类型
 *
 * @author CN
 */

@Getter
public enum ScriptType {
    bin(new BinParser()),
    lst(new LstParser()),
    str(new StrParser()),
    ani(new AniParser()),
    ui(new UIParser()),
    DEFAULT(new DefaultParser());

    /**
     * 解析器
     */
    private final IParser parser;

    ScriptType(IParser parser) {
        this.parser = parser;
    }

    public static ScriptType of(String name) {
        for (ScriptType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return ScriptType.DEFAULT;
    }

}
