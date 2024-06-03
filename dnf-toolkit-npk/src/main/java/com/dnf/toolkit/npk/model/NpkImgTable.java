package com.dnf.toolkit.npk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * IMG文件索引表
 * <p>
 * 每个IMG文件索引占264字节，包括IMG文件在整个NPK文件的地址偏移量和所占大小，以及经过一种特殊算法加密后的名字。
 * </p>
 *
 * @author CN
 */
@Getter
@Builder
@AllArgsConstructor
public class NpkImgTable {

    // 地址偏移量
    // 4字节，地址偏移量
    private int offset;

    // IMG文件大小
    // 4字节，表示对应IMG文件的大小
    private int length;

    // IMG文件名称
    // 256字节，IMG文件加密后的名称
    private String name;
}
