package com.dnf.toolkit.pvf.model;

import com.dnf.toolkit.pvf.coder.PvfCoder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * pvf 数据
 *
 * @author CN
 */
@Getter
@NoArgsConstructor
public abstract class PvfScript {

    /**
     * 脚本路径
     */
    protected String path;

    /**
     * 脚本数据
     */
    protected PvfScriptData data;

    public PvfScript(String path) {
        this.path = path;
        this.data = PvfCoder.loadScriptData(path);
        // 初始化参数
        initParam();
    }

    public PvfScript(PvfScriptData data) {
        this.path = data.getPath();
        this.data = data;

        initParam();
    }

    /**
     * 初始化参数
     */
    protected abstract void initParam();

}
