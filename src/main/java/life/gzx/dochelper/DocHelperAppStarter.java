package life.gzx.dochelper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import life.gzx.docx4jhelper.enums.Docx4jStyle;
import life.gzx.docx4jhelper.utils.WordProcessor;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.healthylife.gzx.dochelper.DocHelper;
import top.healthylife.gzx.dochelper.config.DocBaseConfig;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 毛小蒙
 */
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class})
@RestController
public class DocHelperAppStarter {
    public static void main(String[] args) {
        SpringApplication.run(DocHelperAppStarter.class, args);
    }

    @SneakyThrows
    @GetMapping("/template")
    public void template(HttpServletResponse response) {
        response.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String("配置模板.xlsx".getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        Resource resource = new ClassPathResource("DocHelpeConfig.xlsx");
        IoUtil.write(response.getOutputStream(), true, IoUtil.readBytes(resource.getInputStream()));
    }

    @PostMapping("/upload/config")
    public R<String> uploadConfig(MultipartFile file) {
        try {
            DocBaseConfig.initRemoteConfig(file);
        } catch (Exception e) {
            return R.failed(e.getMessage());
        }
        return R.ok("配置文件上传成功");
    }

    @GetMapping("/doc/build")
    public void generatorWord(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();

        response.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(DocBaseConfig.getConfigByKey("docName").getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        try {
            DocHelper.start(null, outputStream);
        } catch (Exception ex) {
            List<DocBaseConfig> globalCustomConfig = DocBaseConfig.globalCustomConfig;
            List<String> collect = globalCustomConfig.stream().map(e -> StrUtil.format(tableRowHtml, e.getKey(), e.getValue(), e.getDesc())).collect(Collectors.toList());
            String format = StrUtil.format(tableTpl, collect);
            WordProcessor.startDocument()
                    .addStyledParagraph(Docx4jStyle.Heading1, "错误信息:")
                    .title(ex.getMessage())
                    .addStyledParagraph(Docx4jStyle.Heading1, "错误说明:")
                    .title("大多数的错误,都是因为配置问题,请参考下文日志详细检查,也可以参考项目首页配置说明: https://gitee.com/gaozexi/doc-helper ,仍旧解决不了可以作者(vx: himaoxiaomeng 添加注明来意)")
                    .addStyledParagraph(Docx4jStyle.Heading1, "全局配置信息:")
                    .addHtml(format)
                    .addStyledParagraph(Docx4jStyle.Heading1, "通用叶子节点配置:")
                    .addParagraph(JSONUtil.toJsonStr(DocBaseConfig.leafCommonConfig))
                    .addStyledParagraph(Docx4jStyle.Heading1, "上下文信息:")
                    .addParagraph(JSONUtil.toJsonStr(DocBaseConfig.docTitleTrees))
                    .builder(outputStream);
        }

    }

    static String tableRowHtml = " <tr>\n" +
            "      <td>{}</td>\n" +
            "      <td>{}</td>\n" +
            "      <td>{}</td>\n" +
            "    </tr>";

    static String tableTpl = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <title>Title</title>\n" +
            "</head>\n" +
            "<style>\n" +
            "  table {\n" +
            "    width: 100%;\n" +
            "    border: black 1px solid;\n" +
            "  }\n" +
            "\n" +
            "  tr:nth-child(odd) {\n" +
            "    background-color: pink;\n" +
            "  }\n" +
            "\n" +
            "  tr:nth-child(even) {\n" +
            "    background-color: lightblue;\n" +
            "  }\n" +
            "\n" +
            "  td {\n" +
            "    width: 33%;\n" +
            "  }\n" +
            "</style>\n" +
            "<body>\n" +
            "<table>\n" +
            "  <tr style=\"background-color: lightgrey\">\n" +
            "    <td>键</td>\n" +
            "    <td>值</td>\n" +
            "    <td>说明</td>\n" +
            "  </tr>\n" +
            "  {}\n" +
            "</table>\n" +
            "<h3>上下文信息:</h3>\n" +
            "<code>\n" +
            "  {}\n" +
            "</code>\n" +
            "</body>\n" +
            "</html>\n";
}
