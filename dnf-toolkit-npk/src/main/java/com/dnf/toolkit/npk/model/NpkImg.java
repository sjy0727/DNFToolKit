package com.dnf.toolkit.npk.model;

import lombok.Builder;

/**
 * NPK IMG 文件
 *
 * @author CN
 */
//@Data
@Builder
public class NpkImg {

    // 魔数 16字节的固定的文件头
    private String magicNumber;

    // 索引表大小 4字节，索引表所占空间
    private int indexSize;

    public int getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(int indexCount) {
        this.indexCount = indexCount;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "NpkImg{" +
                "magicNumber='" + magicNumber + '\'' +
                ", indexSize=" + indexSize +
                ", reserve=" + reserve +
                ", version=" + version +
                ", indexCount=" + indexCount +
                '}';
    }

    public NpkTexture[] getTextures() {
        return textures;
    }

    public void setTextures(NpkTexture[] textures) {
        this.textures = textures;
    }

    // 保留 4字节，为0
    private int reserve;

    // 版本号 4字节，IMGV2文件结构中的版本号为2
    private int version;

    // 索引表数目 4字节，索引表的表项（包括指向型和图片型）
    private int indexCount;

    // 贴图集合
    private NpkTexture[] textures;

}
