//package com.dnf.toolkit.npk.model;
//
//import enums.com.dnf.toolkit.npk.ColorBit;
//import enums.com.dnf.toolkit.npk.CompressMode;
//import enums.com.dnf.toolkit.npk.ImgVersion;
//import handle.com.dnf.toolkit.npk.HandleFactory;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * npk 贴图
// *
// * @author CN
// */
//@Slf4j
//@Data
//public class NpkTexture {
//
//    // img
//    private NpkImg img;
//
//    // 贴图在img中的下标
//    private int index;
//
//    // 是否指向帧
//    private boolean isLink;
//
//    // 当贴图为链接贴图时所指向的贴图
//    public NpkTexture linkTarget;
//
//    // 颜色系统
//    private ColorBit colorBit;
//
//    // 压缩状态
//    private CompressMode compressMode;
//
//    // 图像宽
//    private int width;
//
//    public int getWidth() {
//        return isLink ? linkTarget.getWidth() : width;
//    }
//
//    // 图像高
//    private int height;
//
//    public int getHeight() {
//        return isLink ? linkTarget.getHeight() : height;
//    }
//
//    // 图像大小
//    private int length;
//
//    // 图像X坐标
//    private int x;
//
//    public int getX() {
//        return isLink ? linkTarget.getX() : x;
//    }
//
//    // 图像Y坐标
//    private int y;
//
//    public int getY() {
//        return isLink ? linkTarget.getY() : y;
//    }
//
//    // 帧域宽
//    private int frameWidth;
//
//    // 帧域高
//    private int frameHeight;
//
//    // 数据偏移量
//    private int dataOffset;
//
//    // 数据
//    private byte[] data;
//
//    public NpkTexture(NpkImg img) {
//        this.img = img;
//    }
//
//    /**
//     * 获取bgra 数据
//     */
//    public byte[] getBgraData() {
//        if (isLink) {
//            return linkTarget.getBgraData();
//        } else {
//            return HandleFactory.of(ImgVersion.of(img.getVersion())).convertData(this);
//        }
//    }
//
//}
package com.dnf.toolkit.npk.model;

import com.dnf.toolkit.npk.enums.ColorBit;
import com.dnf.toolkit.npk.enums.CompressMode;
import com.dnf.toolkit.npk.enums.ImgVersion;
import com.dnf.toolkit.npk.handle.HandleFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * npk 贴图
 *
 * @author CN
 */
@Slf4j
@Data
public class NpkTexture {

    // img
    private NpkImg img;

    // 贴图在img中的下标
    private int index;

    // 是否指向帧
    private boolean isLink;

    // 当贴图为链接贴图时所指向的贴图
    public NpkTexture linkTarget;

    // 颜色系统
    private ColorBit colorBit;

    // 压缩状态
    private CompressMode compressMode;

    // 图像宽
    private int width;

    public int getWidth() {
        return isLink && linkTarget != null ? linkTarget.getWidth() : width;
    }

    // 图像高
    private int height;

    public int getHeight() {
        return isLink && linkTarget != null ? linkTarget.getHeight() : height;
    }

    // 图像大小
    private int length;

    // 图像X坐标
    private int x;

    public int getX() {
        return isLink && linkTarget != null ? linkTarget.getX() : x;
    }

    // 图像Y坐标
    private int y;

    public int getY() {
        return isLink && linkTarget != null ? linkTarget.getY() : y;
    }

    // 帧域宽
    private int frameWidth;

    // 帧域高
    private int frameHeight;

    // 数据偏移量
    private int dataOffset;

    // 数据
    private byte[] data;

    public NpkTexture(NpkImg img) {
        this.img = img;
    }

    /**
     * 获取bgra 数据
     */
    public byte[] getBgraData() {
        if (isLink && linkTarget != null) {
            return linkTarget.getBgraData();
        } else {
            return HandleFactory.of(ImgVersion.of(img.getVersion())).convertData(this);
        }
    }

    public void setLinkTarget(NpkTexture linkTarget) {
        if (this == linkTarget || isCyclicDependency(linkTarget)) {
            throw new IllegalArgumentException("Cyclic dependency detected in NpkTexture links");
        }
        this.linkTarget = linkTarget;
    }

    private boolean isCyclicDependency(NpkTexture target) {
        NpkTexture current = target;
        while (current != null) {
            if (current == this) {
                return true;
            }
            current = current.linkTarget;
        }
        return false;
    }
}
