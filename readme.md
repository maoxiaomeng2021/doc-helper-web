##使用说明

- 常见问题:
word->pdf 

> 基于docx4j简单封了一下

 可以更方便的支持变量替换,包括图片,表格,动态数据,以及word转pdf

```java
package com.gzx.plug.docx4j2pdf.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gzx.plug.docx4j2pdf.base.Docx4jBaseTpl;
import com.gzx.plug.docx4j2pdf.annotation.Docx4j;
import com.gzx.plug.docx4j2pdf.annotation.Docx4jField;
import com.gzx.plug.docx4j2pdf.enums.Docx4jNodeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author : GaoZeXi
 * @date 2022/1/18 10:57
 */
@Data
@Accessors(chain = true)
@ApiModel("演示类:word变量替换")
@Docx4j
public class Docx4jSimpleStartDemo extends Docx4jBaseTpl {

    @ApiModelProperty("Id")
    private String id;

    @ApiModelProperty("姓名")
    private String name;
    
    //这里bookmarkName是为了变量替换的映射使用,先取class的属性,如果加了注解,就会使用注解的属性
    @ApiModelProperty("标题")
    @Docx4jField(bookmarkName = "tittle")
    private String title;

    @ApiModelProperty("test")
    private String test;

    @ApiModelProperty("test2")
    private String test3;

    @ApiModelProperty("模板内容")
    private String content;

    @ApiModelProperty("类型1-短信、2-邮件、3-文档")
    private Integer type;

    //这里指定了该字段的类型,不指定默认为文本,如果需要渲染图片,则指定图片
    //传入的值支持本地图片的地址和图片的url地址,如果找不到则会使用地址来填充
    @ApiModelProperty("头像")
    @Docx4jField(type = Docx4jNodeType.IMAGE)
    private String imageUrl;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("租户id")
    private Integer tenantId;
}
 
```
> 测试
```java
package com.gzx.plug.docx4j2pdf.demo;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import com.gzx.plug.docx4j2pdf.enums.Docx4jStyle;
import com.gzx.plug.docx4j2pdf.utils.WordProcessor;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.wml.HdrFtrRef;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author : GaoZeXi
 * @date 2022/1/24 18:04
 */
@Data
@Accessors(chain = true)
@ApiModel("word测试类")
@Slf4j
public class Test {
    @SneakyThrows
    public static void main(String[] args) {

        //数据准备~~~~
        Docx4jSimpleStartDemo docx4jSimpleStartDemo = new Docx4jSimpleStartDemo();
        docx4jSimpleStartDemo.setId("1").setName("高则喜").setContent("好多鱼").setCreateTime(LocalDateTime.now()).setType(3).setTest("asdwqsa")
                .setTest3("进出口有限公司")
                .setImageUrl("http://120.27.136.238:39000/flashpic/default.jpg");

        List<TableA> objects = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            TableA tableA = new TableA();
            tableA.setId(i + "").setImageUrl("http://120.27.136.238:39000/flashpic/default.jpg")
                    .setName("姓名" + i)
                    .setTitle("风景画")
                    .setContent("模板内容zzs")
                    .setType(2);
            objects.add(tableA);
        }

        List<TableB> objectBs = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            TableB tableb = new TableB();
            tableb.setId(i + "").setImageUrl("http://120.27.136.238:39000/flashpic/default.jpg")
                    .setName("姓名" + i);

            objectBs.add(tableb);
        }

        List<LinkedHashMap<String, Object>> tableCs = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("id", i + 1);
            linkedHashMap.put("imageUrl", "http://120.27.136.238:39000/flashpic/default.jpg");
            linkedHashMap.put("name", "姓名" + i);
            linkedHashMap.put("内容", "风景画");

            tableCs.add(linkedHashMap);
        }

        docx4jSimpleStartDemo.setTableDataMap(objects);
        docx4jSimpleStartDemo.setTableDataMap(objectBs);

        File targetFile = new File("C:\\Users\\maoxiaomeng\\Desktop\\表格测试.docx");
        File targetPdfFile = new File("C:\\Users\\maoxiaomeng\\Desktop\\表格测试.pdf");

    
        //创建新文档
        long now = SystemClock.now();
        WordProcessor.startDocument()
                .addBlankParagraph(2)
                .addImage("D:\\test-images\\desktop.png", "桌面")
                .title("那可是家大文化有限公司")
                .title("企业画像", 24)
                //.addNewPage()
                //页眉
                .pageHeader("XX科技有限公司", HdrFtrRef.FIRST, true)
                .pageHeader("gzx", HdrFtrRef.DEFAULT, false)
                .pageHeader("word-2pdf", HdrFtrRef.EVEN, false)
                //页脚
                .pageFooter(StrUtil.format("创建于{}", LocalDateTime.now()), HdrFtrRef.FIRST)
                .pageFooter(null, HdrFtrRef.DEFAULT)
                .pageFooter(null, HdrFtrRef.EVEN)
                .toc("目录")
                //.addNewPage()
                .addStyledParagraph(Docx4jStyle.Heading2, "二级标题1")
                .addStyledParagraph(Docx4jStyle.Heading3, "子标题1.1")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.Heading3, "二级标题1.2")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.Heading4, "三级标题1.2.1")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.Heading2, "你好低代码2")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.BodyTextFirstIndent2Char, "低代码是一种可视化应用开发方法。通过低代码开发，不同经验水平的开发人员能够通过图形用户界面，使用拖放式组件和模型驱动逻辑来创建Web 和 移动应用。低代码开发平台减轻了非技术开发人员的压力，帮其免去了代码编写工作，同时也为专业开发人员提供了支持，帮助他们提取应用开发过程中的繁琐底层架构与基础设施任务。业务和 IT 部门的开发人员可以在平台中协同，创建、迭代和发布应用，而所需时间只是传统方法的一小部分。这种低代码应用开发方法可针对不同用例开发各种类型的应用，包括将原有应用升级为支持物联网的智能应用。\n" +
                        "\n" +
                        "作者：为了你\n" +
                        "链接：https://www.zhihu.com/question/441272164/answer/1749523137\n" +
                        "来源：知乎\n" +
                        "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                .addStyledParagraph(Docx4jStyle.Heading3, "你好低代码2.1")
                .addStyledParagraph(Docx4jStyle.ColorfulGrid, "" +
                        "<html>\n" +
                        "<header>docx4j</header>\n" +
                        "<body>你好啊</body>\n" +
                        "</html>\n" +
                        "")
                .addHtml(
                        "<html>\n" +
                        "<header>docx4j</header>\n" +
                        "<body>你好啊</body>\n" +
                        "</html>\n" 
                          )
                .addParagraph("测试首行缩进")
                .addImage("D:\\test-images\\desktop.png", "桌面")
                .addStaticDataTable(objects)
                .addDynamicDataTable(tableCs)
                .addParagraph("")
                .addParagraph("表格二")
                .addStaticDataTable(objectBs)
                .addStyledParagraph(Docx4jStyle.FootnoteTextChar, "结束了呀")
                .builder(targetFile);
        log.info("word创建完成,耗时：{}ms", SystemClock.now() - now);

        //变量替换,保存为word,转为pdf
        //WordProcessor.loadFile(targetFile).replaceVar(docx4jSimpleStartDemo).saveDocx(targetFile).toPdf(targetPdfFile);
       

        //word 转 pdf
        WordProcessor.loadFile(targetFile).toPdf(targetPdfFile);
        log.info("pdf转换完成");
    }

}

```