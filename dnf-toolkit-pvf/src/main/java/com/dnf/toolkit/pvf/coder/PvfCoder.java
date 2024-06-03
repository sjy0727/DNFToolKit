package com.dnf.toolkit.pvf.coder;

import cn.hutool.core.lang.Singleton;
import cn.hutool.json.JSONObject;
import com.dnf.toolkit.pvf.model.Pvf;
import com.dnf.toolkit.pvf.model.PvfScriptData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * PVF 解析器
 *
 * @author CN
 */
@Slf4j
public class PvfCoder {

    @Getter
    private static Pvf pvf;

    /**
     * 初始化
     *
     * @param path    路径
     * @param charset 文件编码
     */
    public static void initialize(String path, Charset charset) {
        log.info("PVF 开始初始化，{}", path);
        pvf = Singleton.get(Pvf.class, path, charset);
        log.info("PVF 初始化完成，共加载{}个文件", pvf.getTreeDict().size());
    }

    /**
     * 加载脚本
     *
     * @param path 脚本路径
     */
    public static JSONObject loadScript(String path) {
        return pvf.getScript(path);
    }

    /**
     * 加载脚本数据
     *
     * @param path 脚本路径
     */
    public static PvfScriptData loadScriptData(String path) {
        return new PvfScriptData(loadScript(path), path);
    }

    /**
     * 是否存在
     *
     * @param path 路径
     */
    public static boolean isExist(String path) {
        return pvf.isExist(path);
    }

    /**
     * 卸载PVF
     */
    public static void unload() {
        pvf = null;
        System.gc();
    }

}
