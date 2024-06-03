package com.dnf.toolkit.pvf.model;

import cn.hutool.json.JSONArray;

/**
 * PVF 脚本标签
 *
 * @author CN
 */
public interface PvfScriptLabel {

    /**
     * 解析
     *
     * @param array 数据数组
     */
    void parse(JSONArray array);

}