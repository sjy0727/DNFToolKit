package com.dnf.toolkit.pvf.parser;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.dnf.toolkit.pvf.model.Pvf;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dnf.toolkit.helper.BufferHelper.readStr;

/**
 * ani
 *
 * @author CN
 */
public class AniParser implements IParser {

    /**
     * [LOOP]=0
     * [SHADOW]=1
     * [COORD]=3
     * [IMAGE RATE]=7
     * [IMAGE ROTATE]=8
     * [RGBA]=9
     * [INTERPOLATION]=0xA
     * [GRAPHIC EFFECT]=0xB
     * [DELAY]=0xC
     * [DAMAGE TYPE]=0xD
     * [DAMAGE BOX]=0xE
     * [ATTACK BOX]=0xF
     * [PLAY SOUND]=0x10
     * [SPECTRUM]=0x12
     * [SET FLAG]=0x17
     * [FLIP TYPE]=0x18
     * [LOOP START]=0x19
     * [LOOP END]=0x1A
     * ----
     * ani顺序
     * [LOOP]
     * [SHADOW]
     * [FRAME MAX]
     * [FRAME000]
     * [IMAGE] `Character/Fighter/Equipment/Avatar/skin/ft_body%04d.img` 110
     * [IMAGE POS] -248 -380
     * [IMAGE RATE] 1.000000 1.000000
     * [IMAGE ROTATE] 0.000000
     * [RGBA] 255 255 255 255
     * [INTERPOLATION] 0
     * [GRAPHIC EFFECT] `NONE`
     * [DELAY] 100
     * [DAMAGE TYPE] `NORMAL`
     * [DAMAGE BOX] -21 -5 16 37 10 70 （技能动画才有hitbox）
     */
    @Override
    public JSONObject convert(Pvf pvf, byte[] data) {
        String content = new String(data);
        String[] lines = content.split("\r\n");

        JSONObject result = new JSONObject();
        JSONArray frames = new JSONArray();
        JSONObject currentFrame = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("[") && line.endsWith("]")) {
                // 取[]内的内容
                String key = line.substring(1, line.length() - 1);
                if (key.startsWith("FRAME") && !key.contains("FRAME MAX")) {
                    if (currentFrame != null) {
                        frames.put(currentFrame);
                    }
                    currentFrame = new JSONObject();
                } else {
                    result.put(key, new JSONObject());
                }
            } else {
                // 使用正则表达式匹配 [xxxx xxxx] 和剩余部分
                Pattern pattern = Pattern.compile("(\\[.*?\\])\\s*(.*)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    // 获取[]内的key
                    String key = matcher.group(1);
                    // 获取对应的value
                    String value = matcher.group(2).trim().replace("`", "");
                    // 获取value字符串中多个值
                    String[] values = value.split(" ");

                    if (currentFrame != null) {
                        if (!currentFrame.containsKey(key)) {
                            currentFrame.put(key, values.length == 1 ? value : values);
                        }
                        currentFrame.put(key, values.length == 1 ? value : values);
                    } else {
                        if (!result.containsKey(key)) {
                            result.put(key, value);
                        }
                        result.put(key, value);
                    }
                }
            }
        }

        if (currentFrame != null) {
            frames.put(currentFrame);
        }

        result.put("FRAMES", frames);
        return result;
    }

//    @Override
//    public JSONObject convert(Pvf pvf, byte[] data) {
//        ByteBuffer buffer = ByteBuffer.wrap(data);
//        buffer.order(ByteOrder.LITTLE_ENDIAN);
//
//        JSONObject dataDict = new JSONObject(true);
//
//        // 1.FRAME COUNT
//        int frameCount = buffer.getShort();
//        dataDict.set("[FRAME MAX]", frameCount);
//
//        // 2.IMG PATH DICT
//        short imgPathSize = buffer.getShort();
//        List<String> imgPathList = IntStream.range(0, imgPathSize)
//                .boxed()
//                .map(i -> readStr(buffer, buffer.getInt()))
//                .toList();
//
//        // 3.GLOBAL PARAM DICT
//        int globalParamCount = buffer.getShort();
//        for (int i = 0; i < globalParamCount; i++) {
//            Param param = Param.of(buffer.getShort());
//            dataDict.set(param.getValue(), param.apply(pvf, buffer));
//        }
//
//        // 4.FRAME
//        List<Object> frameList = IntStream.range(0, frameCount)
//                .boxed()
//                .map(i -> {
//                    JSONObject frameDict = new JSONObject(true);
//
//                    // extend frame param
//                    int extendParamCount = buffer.getShort();
//                    for (int j = 0; j < extendParamCount; j++) {
//                        Param param = Param.of(buffer.getShort());
//                        frameDict.set(param.getValue(), param.apply(pvf, buffer));
//                    }
//
//                    // [IMAGE]
//                    String img = "";
//                    buffer.mark();
//                    short imgPathIndex = buffer.getShort();
//                    if (imgPathIndex == -1) {
//                        buffer.reset();
//                    } else {
//                        img = imgPathList.get(imgPathIndex);
//                    }
//                    frameDict.set("[IMAGE]", new Object[]{img, (int) buffer.getShort()});
//
//                    // [IMAGE POS]
//                    frameDict.set("[IMAGE POS]", new int[]{buffer.getInt(), buffer.getInt()});
//
//                    // frame param
//                    int paramCount = buffer.getShort();
//                    for (int j = 0; j < paramCount; j++) {
//                        Param param = Param.of(buffer.getShort());
//                        frameDict.set(param.getValue(), param.apply(pvf, buffer));
//                    }
//
//                    return frameDict;
//                })
//                .collect(Collectors.toList());
//        dataDict.set("[FRAME]", frameList);
//
//        return dataDict;
//    }

    /**
     * 参数
     */
    @Getter
    public enum Param {
        LOOP(0x00, "[LOOP]", (pvf, buffer) -> (int) buffer.get()),
        SHADOW(0x01, "[SHADOW]", (pvf, buffer) -> (int) buffer.get()),
        COORD(0x03, "[COORD]", (pvf, buffer) -> (int) buffer.get()),
        IMAGE_RATE(0x07, "[IMAGE RATE]", (pvf, buffer) -> new int[]{buffer.getInt(), buffer.getInt()}),
        IMAGE_ROTATE(0x08, "[IMAGE ROTATE]", (pvf, buffer) -> buffer.getFloat()),
        RGBA(0x09, "[RGBA]", (pvf, buffer) -> new byte[]{buffer.get(), buffer.get(), buffer.get(), buffer.get()}),
        INTERPOLATION(0x0A, "[INTERPOLATION]", (pvf, buffer) -> (int) buffer.get()),
        GRAPHIC_EFFECT(0x0B, "[GRAPHIC EFFECT]", (pvf, buffer) -> {
            short effect = buffer.getShort();
            if (effect == 5) {
                return new int[]{(int) effect, (int) buffer.getShort(), (int) buffer.getShort(), (int) buffer.getShort()};
            }
            return effect;
        }),
        DELAY(0x0C, "[DELAY]", (pvf, buffer) -> buffer.getInt()),
        DAMAGE_TYPE(0x0D, "[DAMAGE TYPE]", (pvf, buffer) -> buffer.getShort()),
        DAMAGE_BOX(0x0E, "[DAMAGE BOX]", (pvf, buffer) -> new int[]{buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt()}),
        ATTACK_BOX(0x0F, "[ATTACK BOX]", (pvf, buffer) -> new int[]{buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt()}),
        PLAY_SOUND(0x10, "[PLAY SOUND]", (pvf, buffer) -> readStr(buffer, buffer.getInt())),
        SPECTRUM(0x12, "[SPECTRUM]", (pvf, buffer) -> {
            System.out.println("[SPECTRUM] TODO...");
            return null;
        }),
        SET_FLAG(0x17, "[SET FLAG]", (pvf, buffer) -> buffer.getInt()),
        FLIP_TYPE(0x18, "[FLIP TYPE]", (pvf, buffer) -> buffer.getShort()),
        LOOP_START(0x19, "[LOOP START]", (pvf, buffer) -> 1),
        LOOP_END(0x1A, "[LOOP END]", (pvf, buffer) -> buffer.getShort()),
        CLIP(0x1B, "[CLIP]", (pvf, buffer) -> new int[]{buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort()}),
        UNDEFINED(0xFF, "[UNDEFINED]", (pvf, buffer) -> {
            System.out.println("[UNDEFINED] TODO...");
            return buffer.getShort();
        });

        /**
         * 参数索引
         */
        private final int index;

        /**
         * 参数值
         */
        private final String value;

        /**
         * 参数处理器
         */
        private final BiFunction<Pvf, ByteBuffer, Object> function;

        Param(int index, String value, BiFunction<Pvf, ByteBuffer, Object> function) {
            this.index = index;
            this.value = value;
            this.function = function;
        }

        public static Param of(int index) {
            for (Param param : values()) {
                if (param.getIndex() == index) {
                    return param;
                }
            }
            return UNDEFINED;
        }

        private Object apply(Pvf pvf, ByteBuffer buffer) {
            return this.getFunction().apply(pvf, buffer);
        }

    }

}
