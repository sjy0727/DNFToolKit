package com.dnf.toolkit.npk.coder;

import com.dnf.toolkit.npk.enums.ImgVersion;
import com.dnf.toolkit.npk.handle.HandleFactory;
import com.dnf.toolkit.npk.handle.IHandle;
import com.dnf.toolkit.npk.model.NpkImg;
import com.dnf.toolkit.npk.model.NpkImgTable;
import com.dnf.toolkit.npk.model.NpkTexture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.hutool.core.io.FileUtil.*;
import static com.dnf.toolkit.helper.ByteHelper.byteToStr;
import static com.dnf.toolkit.helper.StreamHelper.readBytes;
import static com.dnf.toolkit.helper.StreamHelper.*;

/**
 * NPK 解析器
 *
 * @author CN
 */
@Slf4j
public class NpkCoder {

    /**
     * NPK魔数
     */
    private static final String NPK_MAGIC_NUMBER = "NeoplePack_Bill";

    /**
     * NPK魔数长度
     */
    private static final int NPK_MAGIC_NUMBER_LENGTH = 16;

    /**
     * IMG魔数1
     */
    private static final String IMAGE_MAGIC_NUMBER = "Neople Image File";

    /**
     * IMG魔数2
     */
    private static final String IMG_MAGIC_NUMBER = "Neople Img File";

    /**
     * NPK IMG 名称长度
     */
    private static final int NPK_IMG_NAME_LENGTH = 256;

    private static final int NPK_CHECK_BIT_LENGTH = 4;

    /**
     * NPK IMG 文件名称表
     */
    private static final Map<String, String> NPK_IMG_NAME_TABLE = new ConcurrentHashMap<>();

    /**
     * 存储NPK文件名和对应img文件名列表
     */
    private static final Map<String, List<String>> NPK_TABLE = new ConcurrentHashMap<>();

    /**
     * NPK IMG 索引表
     */
    private static final Map<String, NpkImgTable> NPK_IMG_INDEX_TABLE = new ConcurrentHashMap<>();

    /**
     * ImagePacks2根目录
     */
    private static String rootPath;

    /**
     * 初始化
     *
     * @param path 路径
     */
    public static void initialize(String path) {
        log.info("NPK 开始初始化，{}", path);
        rootPath = path;
        loopFiles(path).parallelStream().forEach(NpkCoder::readNpkCache);
        reverseTable();
        log.info("NPK 初始化完成，共加载{}个img", NPK_IMG_NAME_TABLE.size());
    }

    /**
     * 读取NPK索引表
     *
     * @param file 文件
     */
    private static void readNpkCache(File file) {
        // 读NPK文件流到输入流
        try (InputStream stream = getInputStream(file)) {
            // readImgTables(stream)返回NPK中包含每个img元数据的索引表集合
            readImgTables(stream).forEach(
                    // 遍历每个索引表
                    (npkImgTable) -> {
                        // 获取每个img文件的文件路径名
                        String name = npkImgTable.getName();
                        // 存储索引表中每个img的文件路径名，以及对应NPK文件的文件名（不含路径）
                        NPK_IMG_NAME_TABLE.putIfAbsent(name, file.getName());
                        // 存储索引表中每个img的文件路径名，以及该img对应的索引表
                        NPK_IMG_INDEX_TABLE.putIfAbsent(name, npkImgTable);
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载 img
     *
     * @param name IMG 文件名
     */
    public static NpkImg loadImg(String name) {
        return readImg(
                getInputStream(file(rootPath, NPK_IMG_NAME_TABLE.get(name))),
                NPK_IMG_INDEX_TABLE.get(name)
        );
    }

    /**
     * 读取 IMG 索引表集合（未校验）
     *
     * @param stream 文件流
     */
    @SneakyThrows
    private static List<NpkImgTable> readImgTables(InputStream stream) {

        // NPK文件头
        String magicNumber = byteToStr(readBytes(stream, NPK_MAGIC_NUMBER_LENGTH));
        if (NPK_MAGIC_NUMBER.equals(magicNumber)) {
            log.error("NPK解析失败：非法魔数！");
        }

        // IMG 数量
        int imgSize = readInt(stream);

        // IMG 索引表
        return IntStream.range(0, imgSize)
                .mapToObj(i -> readImgTable(stream))
                .collect(Collectors.toList());
    }

    /**
     * 读取 IMG 索引表
     *
     * @param stream 文件流
     */
    private static NpkImgTable readImgTable(InputStream stream) {
        return NpkImgTable.builder()
                .offset(readInt(stream))
                .length(readInt(stream))
                .name(readStr(stream, NPK_IMG_NAME_LENGTH).trim())//去除尾部的空字符
                .build();
    }

    /**
     * 读取img
     *
     * @param stream      文件流
     * @param npkImgTable img索引表
     */
    @SneakyThrows
    private static NpkImg readImg(InputStream stream, NpkImgTable npkImgTable) {
        seekBytes(stream, npkImgTable.getOffset());
        String magicNumber = readStr(stream);

        int indexSize = 0, reserve = 0, version, indexCount = 0;

        if (magicNumber.startsWith(IMAGE_MAGIC_NUMBER)) {
            // 索引表大小，以字节为单位
            indexSize = readInt(stream);
            // keep，保留位
            readBytes(stream, 2);
            // 版本
            version = readInt(stream);
            // img文件中贴图数目
            indexCount = readInt(stream);
        } else if (magicNumber.startsWith(IMG_MAGIC_NUMBER)) {
            indexSize = readInt(stream);
            reserve = readInt(stream);
            version = readInt(stream);
            indexCount = readInt(stream);
        } else {
            System.out.println(magicNumber);
            System.out.println("音频文件 待解析...");
            version = ImgVersion.Other.getValue();
        }

        NpkImg npkImg = NpkImg.builder()
                .magicNumber(magicNumber)
                .indexSize(indexSize)
                .reserve(reserve)
                .version(version)
                .indexCount(indexCount)
                .build();

        IHandle handle = HandleFactory.of(ImgVersion.of(version));
        if (handle == null) {
            log.warn("load img fail,can't found img version {} implement!", version);
            return npkImg;
        }

        // 读取数据
        handle.readStream(stream, npkImg);

        return npkImg;
    }

    public static Map<String, String> getNpkImgNameTable() {
        return NPK_IMG_NAME_TABLE;
    }

    public static Map<String, NpkImgTable> getNpkIndexNameTable() {
        return NPK_IMG_INDEX_TABLE;
    }

    public static Map<String, List<String>> getNpkTable() {
        return NPK_TABLE;
    }

    // 倒排索引
    private static void reverseTable() {
        List<String> imgsList = NPK_IMG_NAME_TABLE.keySet().stream().toList();
        for (String k : imgsList) {
            String v = NPK_IMG_NAME_TABLE.get(k);

            if (!NPK_TABLE.containsKey(v)) {
                NPK_TABLE.put(v, new ArrayList<>());
                NPK_TABLE.get(v).add(k);
            } else {
                NPK_TABLE.get(v).add(k);
            }
        }
    }

    public static NpkTexture[] getNpkTexturesByImgName(String name) {
        return loadImg(name).getTextures();
    }

    public static List<String> getNpkNames() {
        return NPK_TABLE.keySet().stream().sorted().toList();
    }

    public static List<String> getImgNamesByNpkName(String name) {
        return NPK_TABLE.get(name);
    }


}
