# DNFToolKit

## 介绍

dnf解析NPK资源文件

## 软件架构

基于 java 21

## 安装教程

1. maven配置

``` xml
    <dependency>
        <groupId>com.dnf</groupId>
        <artifactId>dnf-toolkit-core</artifactId>
        <version>1.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.dnf</groupId>
        <artifactId>dnf-toolkit-npk</artifactId>
        <version>1.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.dnf</groupId>
        <artifactId>dnf-toolkit-pvf</artifactId>
        <version>1.0</version>
    </dependency>
```

## Usage

1. 执行测试(解析NPK)

``` java
    public static void main(String[] args) {
        // NPK文件路径
        NpkCoder.initialize("D:/DOF/ImagePacks2");
        // img路径
        NpkImg npkImg = NpkCoder.loadImg("sprite/character/swordman/equipment/avatar/skin/sm_body0000.img");
        System.out.println(npkImg);
    }
```

``` java
    public static void main(String[] args) {
        // PVF文件路径
        PvfCoder.initialize("D:/DOF/Script.pvf", Charset.forName("GBK"));
        
        // .ani等其他文件脚本路径 返回对应Json(目前只支持.lst .str .ani)
        JSONObject data = PvfCoder.loadScript("character/common/animation/minimap_effect_dodge.ani");
        System.out.println(data);
        
        // 输出所有存放 <pvf子文件所在父路径名, 对应文件父路径下所有PvfFile>
        System.out.println(PvfCoder.getPvf().getTreeDict());
        
        // 输出占位符对应中文文本
        System.out.println(PvfCoder.getPvf().getPlaceHolderTable().get("condition_message_10770"));
    }
```

## BugFix

- 修改整个项目以适应60版本初期Act4版本
- (Fixed) 修改解析.ani文件的Parser
- (Todo) 补充一下 加载所有的.lst文件
- (Todo) 导出所有pvf的文本
- (Todo) 完成后续各种pvf子文件格式的Parser

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request