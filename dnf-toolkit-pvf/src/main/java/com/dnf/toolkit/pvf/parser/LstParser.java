package com.dnf.toolkit.pvf.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.dnf.toolkit.pvf.model.Pvf;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * lst
 *
 * @author CN
 */
public class LstParser implements IParser {

    @Override
    public JSONObject convert(Pvf pvf, byte[] data) {
//        ByteBuffer buffer = ByteBuffer.wrap(data);
//        buffer.order(ByteOrder.LITTLE_ENDIAN);
//
//        buffer.position(2);
//
//        JSONObject dataDict = new JSONObject();
//
//        int index = -1;
//
//        while (buffer.hasRemaining()) {
//            if (buffer.remaining() - 5 >= 0) {
//
//                byte type = buffer.get();
//
//                switch (type) {
//                    case 0x02:
//                        index = buffer.getInt();
//                        break;
//                    case 0x07:
//                        String strValue = pvf.getStringTable(buffer.getInt()).toLowerCase();
//                        dataDict.set(String.valueOf(index), strValue);
//                        break;
//                }
//            } else {
//                break;
//            }
//        }
//        return dataDict;
        String content = new String(data, pvf.getCharset());

        Map<String, String> dataDict = Arrays.stream(content.split("\r\n"))
                .filter(line -> !line.startsWith("//"))
                .filter(StrUtil::isNotBlank)
                .map(line -> line.split("\t"))
                .filter(split -> split.length >= 2) // 确保分割后的数组至少有两个元素
                .collect(Collectors.toMap(split -> split[0],
                        split -> split[1].replace("`", "")));

        return new JSONObject(dataDict);
    }
}
