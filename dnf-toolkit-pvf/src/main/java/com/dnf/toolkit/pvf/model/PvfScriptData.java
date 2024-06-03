package com.dnf.toolkit.pvf.model;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PVF 脚本数据
 *
 * @author CN
 */
public class PvfScriptData extends JSONObject {

    /**
     * 脚本路径
     */
    @Getter
    private final String path;

    /**
     * 脚本键集合
     */
    private final List<String> keys;

    /**
     * 脚本值集合
     */
    private final List<Object> values;

    public PvfScriptData(JSONObject data, String path) {
        super(data);
        this.path = path;
        this.keys = this.keySet().stream().toList();
        this.values = this.values().stream().toList();
    }

    public PvfScriptData getScriptData(String key) {
        return new PvfScriptData(getJSONObject(key), null);
    }

    /**
     * 获取string值
     *
     * @param key 键
     */
    public String strVal(String key) {
        if (!containsKey(key)) {
            return null;
        }
        return getJSONArray(key).getStr(0);
    }

    /**
     * 获取int值
     *
     * @param key 键
     */
    public Integer intVal(String key) {
        if (!containsKey(key)) {
            return 0;
        }
        return getJSONArray(key).getInt(0);
    }

    /**
     * 获取float值
     *
     * @param key 键
     */
    public Float floatVal(String key) {
        if (!containsKey(key)) {
            return 0f;
        }
        return getJSONArray(key).getFloat(0);
    }

    /**
     * 获取bool值
     *
     * @param key 键
     */
    public Boolean boolVal(String key) {
        return floatVal(key) == 1;
    }

    /**
     * 获取int数组值
     *
     * @param key 键
     */
    public Integer[] intVals(String key) {
        if (!containsKey(key)) {
            return new Integer[0];
        }
        return getJSONArray(key).stream().map(d -> (int) d).toArray(Integer[]::new);
    }

    /**
     * 获取string数组值
     *
     * @param key 键
     */
    public String[] strVals(String key) {
        if (!containsKey(key)) {
            return new String[0];
        }
        return getJSONArray(key).stream().map(d -> (String) d).toArray(String[]::new);
    }

    /**
     * 获取对象值
     *
     * @param key 键
     * @param clz 类
     */
    public <T extends PvfScriptLabel> T objVal(String key, Class<T> clz) {
        T instance = ReflectUtil.newInstanceIfPossible(clz);
        if (!containsKey(key)) {
            return instance;
        }
        instance.parse(getJSONArray(key));
        return instance;
    }

    /**
     * 模糊查找
     *
     * @param pattern 匹配规则
     */
    public Map<Integer, String> getIndexKey(String pattern) {
        Map<Integer, String> indexKey = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (key.contains(pattern)) {
                indexKey.put(i, key);
            }
        }
        return indexKey;
    }

    /**
     * 获取索引
     *
     * @param key 键
     */
    public int getIndex(String key) {
        return keys.indexOf(key);
    }

    /**
     * 获取键
     *
     * @param index 索引
     */
    public String getKey(int index) {
        return keys.get(index);
    }

    /**
     * 获取下一个键
     *
     * @param key 键
     */
    public String getNextKey(String key) {
        return getNextKey(key, 1);
    }

    /**
     * 获取下一个键
     *
     * @param key  键
     * @param next 下几个
     */
    public String getNextKey(String key, int next) {
        int index = getIndex(key);
        if (index < 0) {
            return null;
        }
        return keys.get(index + next);
    }

    /**
     * 获取值
     *
     * @param index 索引
     */
    public Object getByIndex(int index) {
        return values.get(index);
    }

}
