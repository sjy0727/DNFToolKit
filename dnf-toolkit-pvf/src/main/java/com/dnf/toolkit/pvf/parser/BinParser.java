package com.dnf.toolkit.pvf.parser;

import cn.hutool.json.JSONObject;
import com.dnf.toolkit.pvf.model.Pvf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.dnf.toolkit.helper.ByteHelper.byteToStr;

/**
 * bin
 *
 * @author CN
 */
public class BinParser implements IParser {

    @Override
    public JSONObject convert(Pvf pvf, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        JSONObject dataDict = new JSONObject(true);

        int tableSize = buffer.getInt();

        int start = buffer.getInt();
        for (int i = 0; i < tableSize; i++) {
            int end = buffer.getInt();

            byte[] contextByte = new byte[end - start];
            buffer.mark();
            buffer.position(start + 4);
            buffer.get(contextByte);
            buffer.reset();

            String context = byteToStr(contextByte, pvf.getCharset());
            dataDict.set(String.valueOf(i), context);

            start = end;
        }

        return dataDict;
    }

}
