package com.dnf.toolkit.pvf.model;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * PVF 数据
 *
 * @author CN
 */
@Data
public class PvfData {

    private List<Integer> unitTypes = new ArrayList<>();

    private List<Object> values = new ArrayList<>();

    /**
     * 获取json
     */
    public JSONObject getDict() {
        return loadDict(this.unitTypes, this.values);
    }

    /**
     * 加载json
     */
    private JSONObject loadDict(List<Integer> unitTypes, List<Object> values) {
        // 存放带结束符的段落
        List<String> segmentKeysWithEndMark = getSegmentKeysWithEndMark();

        JSONObject res = new JSONObject(true);
        List<Object> segment = new ArrayList<>();
        List<Integer> segTypes = new ArrayList<>();
        String segmentKey = null;

        for (int i = 0; i < values.size(); i++) {
            int unitType = unitTypes.get(i);
            Object value = values.get(i);

            if (unitType == 5) {

                String strValue = (String) value;

                // 判断是否为新的段
                if (segmentKey == null) {
                    segmentKey = strValue.contains("/") ? null : strValue;
                    continue;
                } else {
                    if (!segmentKeysWithEndMark.contains(segmentKey) || strValue.replace("/", "").equals(segmentKey)) {
                        addSegment(res, segmentKeysWithEndMark, segTypes, segmentKey, segment);
                        segmentKey = strValue.contains("/") ? null : strValue;
                        segTypes.clear();
                        segment.clear();
                        continue;
                    }
                }
            }

            segment.add(value);
            segTypes.add(unitType);
        }

        if (segmentKey != null) {
            addSegment(res, segmentKeysWithEndMark, segTypes, segmentKey, segment);
        }

        return res;
    }

    /**
     * 添加片段
     */
    private void addSegment(JSONObject res, List<String> segmentKeysWithEndMark, List<Integer> segTypes, String segmentKey, List<Object> segment) {
        String oldSegmentKey = segmentKey;
        if (res.containsKey(segmentKey)) {
            int suffix = 1;
            while (res.containsKey(segmentKey + "-" + suffix)) {
                suffix += 1;
            }
            segmentKey = segmentKey + "-" + suffix;
        }
        if ((segmentKeysWithEndMark.contains(segmentKey) || segmentKeysWithEndMark.contains(oldSegmentKey)) && segTypes.contains(5)) {
            res.set(segmentKey, loadDict(segTypes, segment));
        } else {
            res.set(segmentKey, segment);
        }
    }

    /**
     * 存放带结束符的段落
     */
    private List<String> getSegmentKeysWithEndMark() {
        List<String> segmentKeysWithEndMark = new ArrayList<>();
        for (Object value : this.values) {
            if (value instanceof String strValue) {
                if (strValue.startsWith("[/") && strValue.endsWith("]")) {
                    segmentKeysWithEndMark.add(strValue.replace("/", ""));
                }
            }
        }
        return segmentKeysWithEndMark;
    }

}
