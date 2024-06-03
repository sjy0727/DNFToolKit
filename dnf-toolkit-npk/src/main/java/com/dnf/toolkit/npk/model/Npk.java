package com.dnf.toolkit.npk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * NPK文件
 *
 * @author CN
 */
@Data
@Builder
public class Npk {

    // 魔数 16字节的固定的文件头
    private String magicNumber;

    // img数量 4字节的IMG文件的数目
    private int imgSize;

    // IMG文件索引表 每个IMG文件索引占264字节，包括IMG文件在整个NPK文件的地址偏移量和所占大小，以及经过一种特殊算法加密后的名字
    private List<NpkImgTable> npkImgTables;

    // NPK校验位 32个字节，使用SHA256算法用以对NPK文件的合法性进行校验。
    private byte[] checkBit;

    // IMG文件集合
    private List<byte[]> imgDataList;

}
