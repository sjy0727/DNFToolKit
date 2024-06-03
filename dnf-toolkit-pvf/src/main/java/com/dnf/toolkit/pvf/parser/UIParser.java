package com.dnf.toolkit.pvf.parser;

import cn.hutool.json.JSONObject;
import com.dnf.toolkit.pvf.model.Pvf;
import com.dnf.toolkit.pvf.model.PvfData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static com.dnf.toolkit.helper.BufferHelper.readBytes;
import static com.dnf.toolkit.helper.ByteHelper.bytesToFloat;
import static com.dnf.toolkit.helper.ByteHelper.bytesToInt;

/**
 * ui
 *
 * @author CN
 */
public class UIParser implements IParser {

    @Override
    public JSONObject convert(Pvf pvf, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(2);

        PvfData pvfData = new PvfData();
        List<Integer> unitTypes = pvfData.getUnitTypes();
        List<Object> values = pvfData.getValues();

        while (buffer.hasRemaining()) {
            if (buffer.remaining() >= 5) {

                // 单位
                int unitType = buffer.get();

                // 值
                byte[] value = readBytes(buffer, 4);

                switch (unitType) {
                    case 1:
                        unitTypes.add(unitType);
                        values.add(bytesToInt(value));
                        break;
                    // 数字
                    case 2:
                        unitTypes.add(unitType);
                        values.add(bytesToInt(value));
                        break;
                    case 3:
                        unitTypes.add(unitType);
                        values.add(bytesToInt(value));
                        break;
                    // float
                    case 4:
                        unitTypes.add(unitType);
                        values.add(bytesToFloat(value));
                        break;
                    // 标签
                    case 5:
                        String string = pvf.getStringTable(bytesToInt(value));
                        // FIXME 临时处理 [common action]
                        if ("[common action]".equals(string)
                                || "[parent tab]".equals(string)
                                || "[parent radio]".equals(string)
                                || "[parent]".equals(string)
                                || string.contains("int option]")) {
                            unitTypes.add(7);
                        } else {
                            unitTypes.add(unitType);
                        }
                        values.add(string);
                        break;
                    case 6:
                        unitTypes.add(unitType);
                        values.add(pvf.getStringTable(bytesToInt(value)));
                        break;
                    // 字符串
                    case 7:
                        unitTypes.add(unitType);
                        values.add(pvf.getStringTable(bytesToInt(value)));
                        break;
                    case 8:
                        unitTypes.add(unitType);
                        values.add(pvf.getStringTable(bytesToInt(value)));
                        break;
                    // 字符串
                    case 10:
                        unitTypes.add(unitType);
                        values.add(pvf.getNString(pvf.getStringTable(bytesToInt(value))));
                        break;
                }
            } else {
                break;
            }
        }
        return pvfData.getDict();
    }

}
