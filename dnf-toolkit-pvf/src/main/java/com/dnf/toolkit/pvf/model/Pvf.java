package com.dnf.toolkit.pvf.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.CaseInsensitiveTreeMap;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.dnf.toolkit.pvf.enums.ScriptType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static com.dnf.toolkit.pvf.util.PvfHelper.crcDecrypt;
import static com.dnf.toolkit.pvf.util.PvfHelper.getScriptType;
import static com.dnf.toolkit.helper.ByteHelper.byteToStr;
import static com.dnf.toolkit.helper.ByteHelper.bytesToInt;

/**
 * PVF
 *
 * @author CN
 */

//@Data
@Slf4j
public class Pvf {

    /**
     * PVF原始文件字节缓冲
     */
    private ByteBuffer buffer;

    /**
     * pvf文件头
     */
    private PvfHeader pvfHeader;

    /**
     * 文件树字典
     */
    private Map<String, List<PvfFile>> treeDict = new CaseInsensitiveTreeMap<>();

    /**
     * 用来存储所有.chn.str文件中的占位符对应的中文文本
     */
    private Map<String, Object> placeHolderTable;

    /**
     * stringtable.bin
     */
    private Map<Integer, String> stringTable;

    /**
     * n_string.lst
     */
    private JSONObject nString;

    /**
     * 字符编码
     */
    private Charset charset;

    public Map<String, List<PvfFile>> getTreeDict() {
        return treeDict;
    }

    public Charset getCharset() {
        return charset;
    }

    public Pvf(String path, Charset charset) throws IOException {
        try (InputStream stream = FileUtil.getInputStream(FileUtil.file(path))) {
            this.buffer = ByteBuffer.allocateDirect(stream.available());
            this.buffer.put(IoUtil.readBytes(stream));
            this.buffer.position(0);
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);

            this.pvfHeader = new PvfHeader(this.buffer);

            this.charset = charset;

            // 加载树
            this.loadTree();

            // 加载 string.lst 并返回对应的hashmap, key为lst中序号, value为对应的待解析的pvf子文件路径名
            this.loadStringTable();
//            System.out.println(stringTable);

            // 加载string.lst文件中所有的*.chn.str文件 (汉化替换符)，返回所有替换符对应文本的JSON
            this.loadNString();
//            System.out.println(nString);

            placeHolderTable = parseLeafNodes(nString);

            // 加载其他的lst文件
//            this.loadLst("aicharacter/aicharacter.lst");
//            this.loadLst("character/character.lst");
//            this.loadLst("creature/creature.lst");
//            this.loadLst("dungeon/dungeon.lst");
//            this.loadLst("equipment/equipment.lst");
//            this.loadLst("itemshop/itemshop.lst");
//            this.loadLst("map/map.lst");
//            this.loadLst("monster/monster.lst");
//            this.loadLst("npc/npc.lst");
//            this.loadLst("passiveobject/passiveobject.lst");
//            this.loadLst("pet/pet.lst");
//            this.loadLst("quest/quest.lst");
//            this.loadLst("skill/skilllist.lst");
//            this.loadLst("skill/fighterskill.lst");
//            this.loadLst("skill/gunnerskill.lst");
//            this.loadLst("skill/mageskill.lst");
//            this.loadLst("skill/priestskill.lst");
//            this.loadLst("skill/swordmanskill.lst");
//            this.loadLst("stackable/stackable.lst");
//            this.loadLst("town/town.lst");
//            this.loadLst("worldmap/worldmap.lst");
        }
    }

    /**
     * 加载文件树
     */
    private void loadTree() {
        for (int i = 0; i < pvfHeader.getTreeCount(); i++) {
//            int fileNumber = bytesToInt(pvfHeader.getTreeBytes(4));
//            int filePathLength = bytesToInt(pvfHeader.getTreeBytes(4));
//            String filePath = byteToStr(pvfHeader.getTreeBytes(filePathLength));
//            int fileLength = (bytesToInt(pvfHeader.getTreeBytes(4)) + 3) & 0xFFFFFFFC;
//            int fileCrc32 = bytesToInt(pvfHeader.getTreeBytes(4));
//            int relativeOffset = bytesToInt(pvfHeader.getTreeBytes(4));

            int fileNumber = bytesToInt(pvfHeader.getTreeBytes(4));
            int relativeOffset = bytesToInt(pvfHeader.getTreeBytes(4));
            int fileLength = (bytesToInt(pvfHeader.getTreeBytes(4)) + 3) & 0xFFFFFFFC;
            int fileCrc32 = bytesToInt(pvfHeader.getTreeBytes(4));
            int filePathLength = bytesToInt(pvfHeader.getTreeBytes(4));
            String filePath = byteToStr(pvfHeader.getTreeBytes(filePathLength));

            PvfFile pvfFile = PvfFile.builder()
                    .number(fileNumber)
                    .pathLength(filePathLength)
                    .path(filePath)
                    .length(fileLength)
                    .crc32(fileCrc32)
                    .offset(relativeOffset)
                    .build();

            // 添加文件树
            putTreeFile(pvfFile);
        }
    }

    /**
     * 添加文件树
     */
    private void putTreeFile(PvfFile pvfFile) {
        List<PvfFile> pvfFiles;
        String path = pvfFile.getPath();
        // 根据文件路径 path 确定根文件路径 rootFilePath。
        // 如果路径中包含 /，则取最后一个 / 之前的部分作为根路径，否则将根路径设为 /。
        // 这一步是为了将文件按路径层级组织起来。
        String rootFilePath = path.indexOf("/") > 0 ? path.substring(0, path.lastIndexOf("/")) : "/";

        // 这里检查 treeDict 字典中是否已经包含了 rootFilePath 作为键。
        // 如果包含，获取对应的文件列表 pvfFiles；
        // 如果不包含，创建一个新的文件列表 pvfFiles 并将其放入字典中。
        if (treeDict.containsKey(rootFilePath)) {
            pvfFiles = treeDict.get(rootFilePath);
        } else {
            pvfFiles = new ArrayList<>();
            treeDict.put(rootFilePath, pvfFiles);
        }
        pvfFiles.add(pvfFile);
    }

    /**
     * 获取文件树
     */
    private PvfFile getTreeFile(String path) {
        String rootFilePath = path.indexOf("/") > 0 ? path.substring(0, path.lastIndexOf("/")) : "/";
        List<PvfFile> pvfFiles = treeDict.get(rootFilePath);

        if (pvfFiles == null || pvfFiles.isEmpty()) {
            throw new RuntimeException("未找到pvf文件:" + path);
        }

        // 存储pvf子文件
        PvfFile pvfFile = null;

        for (PvfFile file : pvfFiles) {
            // 和treeDict中所有的pvf子文件夹判断路径名是否相等
            if (file.getPath().equalsIgnoreCase(path)) {
                pvfFile = file;
                break;
            }
        }

        if (pvfFile == null) {
            for (PvfFile file : pvfFiles) {
                String treeFileName = FileNameUtil.getName(file.getPath());
                String fileName = FileNameUtil.getName(path);
                if (treeFileName.equalsIgnoreCase("(r)" + fileName) || treeFileName.equalsIgnoreCase("(f)" + fileName)) {
                    pvfFile = file;
                    break;
                }
            }
        }

        return pvfFile;
    }

    /**
     * 是否存在
     *
     * @param path 路径
     */
    public boolean isExist(String path) {
        String rootFilePath = path.indexOf("/") > 0 ? path.substring(0, path.lastIndexOf("/")) : "/";
        List<PvfFile> pvfFiles = treeDict.get(rootFilePath);

        if (pvfFiles == null || pvfFiles.isEmpty()) {
            return false;
        }

        for (PvfFile file : pvfFiles) {
            if (file.getPath().equalsIgnoreCase(path)) {
                return true;
            }
        }

//        if (pvfFile == null) {
//            for (PvfFile file : pvfFiles) {
//                String treeFileName = FileNameUtil.getName(file.getPath());
//                String fileName = FileNameUtil.getName(path);
//                if (treeFileName.equalsIgnoreCase("(r)" + fileName) || treeFileName.equalsIgnoreCase("(f)" + fileName)) {
//                    pvfFile = file;
//                    break;
//                }
//            }
//        }

        return false;
    }

    /**
     * 加载stringtable.bin
     */
    private void loadStringTable() {
        // stringtable.bin 修改为 string.lst
        // 解析string.lst中的文本并返回json
        stringTable = getScript("string.lst").toBean(new TypeReference<>() {
        });
    }

    /**
     * 加载指定.lst文件中所有的pvf子文件脚本并返回对应的JSON(解析器种类未完全写完)
     *
     * @param path pvf子文件路径
     * @return 返回对应的Json
     */
    private JSONObject loadLst(String path) {
        return new JSONObject(getScript(path)
                .values()
                .stream()
                .collect(Collectors.toMap(str -> str, str -> getScript((String) str)))
        );
    }

    /**
     * 加载n_string.lst
     */
    private void loadNString() {
        // n_string.lst 改为 string.lst
        nString = new JSONObject(
                getScript("string.lst")
                        .values()
                        .stream()
                        .collect(Collectors.toMap(str -> str, str -> getScript((String) str)))
        );
    }

    /**
     * 获取树内容
     *
     * @param path 路径
     */
    private byte[] getTreeContent(String path) {
        // 获取文件树
        PvfFile file = getTreeFile(path);
        if (file == null) {
            log.warn("load script {} content fail,can't found the script!", path);
            return null;
        }
        // 读取content
        byte[] content = new byte[file.getLength()];
        // reset 回到 PvfHeader中读取完索引区的位置
        buffer.reset();
        buffer.get(buffer.position() + file.getOffset(), content, 0, file.getLength());
        // crc解密content
        crcDecrypt(content, file.getCrc32());
        return content;
    }

    /**
     * 获取脚本内容
     *
     * @param path 路径
     */
    public JSONObject getScript(String path) {
        path = path.toLowerCase();
        ScriptType scriptType = getScriptType(path);
        return scriptType.getParser().convert(this, getTreeContent(path));
    }

    public JSONObject getScript(String path, ScriptType scriptType) {
        path = path.toLowerCase();
        return scriptType.getParser().convert(this, getTreeContent(path));
    }

    /**
     * 获取string table
     *
     * @param key 值
     */
    public String getStringTable(Integer key) {
        return stringTable.get(key);
    }

    /**
     * 获取 n_string
     *
     * @param key 值
     */
    public String getNString(Object key) {
        for (Object o : nString.values()) {
            JSONObject object = (JSONObject) o;
            if (object.containsKey((String) key)) {
                return object.getStr((String) key);
            }
        }
        return null;
    }

    public void extractPvfToDirectory(String scriptPvfPath, boolean isVerbose) {
        List<String> directories = getTreeDict().keySet().stream().toList();
        directories.forEach(dir -> {
            File dirPath = new File(scriptPvfPath + "/Script/" + dir);
            boolean success = dirPath.mkdirs();
            if (!success && !dirPath.exists()) {
                System.out.println(dirPath.getName() + "创建失败");
            }
            // 获取文件夹下对应的子文件列表
            List<PvfFile> pvfFileList = getTreeDict().get(dir);
            pvfFileList.forEach(
                    f -> {
                        try (FileOutputStream fos = new FileOutputStream(scriptPvfPath + "/Script/" + f.getPath())) {
                            // 获取path对应的pvf子文件数据区,返回对应的字节数组
                            fos.write(getTreeContent(f.getPath()));

                            if (isVerbose) {
                                System.out.println("Written file : "
                                        + f.getPath()
                                        + " / Size : "
                                        + f.getLength() + " <"
                                        + String.format("%08X", f.getLength())
                                        + "> / OS : "
                                        + f.getOffset() + " <"
                                        + String.format("%08X", f.getOffset()) + ">"
                                );
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );

        });

    }

    public Map<String, Object> getPlaceHolderTable() {
        return placeHolderTable;
    }

    public static Map<String, Object> parseLeafNodes(JSONObject jsonObject) {
        Map<String, Object> leafNodes = new HashMap<>();
        parseLeafNodesHelper(jsonObject, leafNodes);
        return leafNodes;
    }

    private static void parseLeafNodesHelper(JSONObject jsonObject, Map<String, Object> leafNodes) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                parseLeafNodesHelper((JSONObject) value, leafNodes);
            } else if (value instanceof JSONArray) {
                parseLeafNodesFromArray((JSONArray) value, leafNodes);
            } else {
                leafNodes.put(key, value);
            }
        }
    }

    private static void parseLeafNodesFromArray(JSONArray jsonArray, Map<String, Object> leafNodes) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object value = jsonArray.get(i);

            if (value instanceof JSONObject) {
                parseLeafNodesHelper((JSONObject) value, leafNodes);
            } else if (value instanceof JSONArray) {
                parseLeafNodesFromArray((JSONArray) value, leafNodes);
            } else {
                leafNodes.put(String.valueOf(i), value);
            }
        }
    }
}
