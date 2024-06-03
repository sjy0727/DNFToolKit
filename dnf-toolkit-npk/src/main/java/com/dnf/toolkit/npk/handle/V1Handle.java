package com.dnf.toolkit.npk.handle;

import com.dnf.toolkit.npk.enums.ColorBit;
import com.dnf.toolkit.npk.enums.CompressMode;
import com.dnf.toolkit.npk.model.NpkImg;
import com.dnf.toolkit.npk.model.NpkTexture;
import com.dnf.toolkit.npk.util.ColorHelper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.util.ZipUtil.unZlib;
import static com.dnf.toolkit.helper.StreamHelper.readBytes;
import static com.dnf.toolkit.helper.StreamHelper.readInt;

/**
 * V1
 *
 * @author CN
 */
public class V1Handle implements IHandle {

    @Override
    public void readStream(InputStream stream, NpkImg img) {
        Map<Integer, Integer> targetMap = new HashMap<>(8);

        // 贴图数量
        int indexCount = img.getIndexCount();

        NpkTexture[] textures = new NpkTexture[indexCount];

        // 设置贴图
        img.setTextures(textures);

        // 创建贴图集合
        for (int i = 0; i < indexCount; i++) {
            NpkTexture texture = new NpkTexture(img);
            texture.setIndex(i);

            textures[i] = texture;

            // 索引项
            int indexType = readInt(stream);

            // 指向型
            if (indexType == 0x11) {
                texture.setLink(true);
                // targetMap 放入img子图的下标索引 以及指向的下一帧的索引
                targetMap.put(i, readInt(stream));
            } else {
                // 如果不是指向型，则确定图像格式
                ColorBit colorBit = ColorBit.of(indexType);
                texture.setColorBit(colorBit);
                texture.setCompressMode(CompressMode.of(readInt(stream)));
                texture.setWidth(readInt(stream));
                texture.setHeight(readInt(stream));
                texture.setLength(readInt(stream));
                texture.setX(readInt(stream));
                texture.setY(readInt(stream));
                texture.setFrameWidth(readInt(stream));
                texture.setFrameHeight(readInt(stream));

                if (texture.getCompressMode() == CompressMode.NONE) {
                    texture.setLength(texture.getWidth() * texture.getHeight() * (texture.getColorBit() == ColorBit.ARGB_8888 ? 4 : 2));
                }

                // 设置数据
                texture.setData(readBytes(stream, texture.getLength()));
            }
        }

        // 指向型索引
        for (NpkTexture texture : textures) {
            if (texture.isLink()) {
                int index = texture.getIndex();
                if (targetMap.containsKey(index)) {
                    Integer targetIndex = targetMap.get(index);
                    if (targetIndex != index) {
                        texture.setLinkTarget(textures[targetIndex]);
                    }
                }
            }
        }
    }

    @Override
    public byte[] convertData(NpkTexture texture) {
        byte[] data = texture.getData();

        // 压缩方式
        if (texture.getCompressMode() == CompressMode.ZLIB) {
            data = unZlib(data);
        }

        // 颜色转换
        return ColorHelper.readBgraBytes(data, texture.getWidth(), texture.getHeight(), texture.getColorBit());
    }

}
