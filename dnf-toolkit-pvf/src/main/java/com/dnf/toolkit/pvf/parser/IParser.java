package com.dnf.toolkit.pvf.parser;

import cn.hutool.json.JSONObject;
import com.dnf.toolkit.pvf.model.Pvf;

/**
 * PVF 脚本解析器
 *
 * @author CN
 */
public interface IParser {

    /**
     * 转换
     *
     * @param pvf  PVF
     * @param data 字节数组
     */
    JSONObject convert(Pvf pvf, byte[] data);

}
