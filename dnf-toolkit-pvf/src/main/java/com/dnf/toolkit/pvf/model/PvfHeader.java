package com.dnf.toolkit.pvf.model;

import lombok.Data;

import java.nio.ByteBuffer;

import static com.dnf.toolkit.pvf.util.PvfHelper.crcDecrypt;
import static com.dnf.toolkit.helper.BufferHelper.*;

/**
 * PVF 文件头
 *
 * @author CN
 */
@Data
public class PvfHeader {

//    // GUID长度
//    private int guidLength;
//    // GUID
//    private String guid;
//    // 文件版本
//    private int version;

    // 魔数
    private String magicNumber;
    // 目录树长度
    private int treeLength;
    // 目录树crc
    private int treeCrc32;
    // 目录树文件数量
    private int treeCount;
    // 目录树字节缓冲
    private ByteBuffer treeBuffer;

    public PvfHeader(ByteBuffer buffer) {
//        this.guidLength = readInt(buffer);
//        this.guid = readStr(buffer, guidLength);
//        this.version = readInt(buffer);
        this.magicNumber = readStr(buffer, 15);
        this.treeLength = readInt(buffer);
        this.treeCrc32 = readInt(buffer);
        this.treeCount = readInt(buffer);
        // 目录树字节缓冲
        this.treeBuffer = ByteBuffer.wrap(readBytes(buffer, treeLength));
        // 解密目录树字节缓冲
        crcDecrypt(this.treeBuffer, this.treeCrc32);
        // 标记位置
        buffer.mark();
    }

    /**
     * 获取目录树字节数组
     *
     * @param length 长度
     */
    protected byte[] getTreeBytes(int length) {
        return readBytes(treeBuffer, length);
    }

}
