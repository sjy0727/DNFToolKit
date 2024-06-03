package com.dnf.toolkit.npk.handle;

import com.dnf.toolkit.npk.model.NpkTexture;
import com.dnf.toolkit.npk.model.NpkImg;

import java.io.InputStream;

/**
 * img版本处理器
 *
 * @author CN
 */
public interface IHandle {

    /**
     * 读取流
     *
     * @param stream 数据流
     * @param img    npk img
     */
    void readStream(InputStream stream, NpkImg img);

    /**
     * 转换数据
     *
     * @param texture 贴图
     */
    byte[] convertData(NpkTexture texture);

}
