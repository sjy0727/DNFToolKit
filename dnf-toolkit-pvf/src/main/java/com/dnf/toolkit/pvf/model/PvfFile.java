package com.dnf.toolkit.pvf.model;

import lombok.Builder;
import lombok.Data;

/**
 * pvf 文件
 *
 * @author CN
 */
@Data
@Builder
public class PvfFile {

    /**
     * 文件编号
     */
    private int number;

    /**
     * 文件完整路径长度
     */
    private int pathLength;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件字节数大小
     */
    private int length;

    /**
     * 文件解密CRC32
     */
    private int crc32;

    /**
     * 文件的偏移
     */
    private int offset;
}
