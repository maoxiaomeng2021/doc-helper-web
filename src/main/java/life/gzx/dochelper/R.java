package life.gzx.dochelper;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标准的结果返回对象
 *
 * @author maoxiaomeng
 */
@Builder
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int code = 0;

    @Getter
    @Setter
    private String msg = "success";

    @Getter
    @Setter
    private T data;

    @Getter
    @Setter
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss SSS")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss SSS")
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> R<T> ok() {
        return restResult(null, 0, "success");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 0, "success");
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 0, msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, 1, "error");
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, 1, msg);
    }

    public static <T> R<T> failed(T data) {
        return restResult(data, 1, "error");
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, 1, msg);
    }

    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> R<T> failed(T data, int code) {
        return restResult(data, code, null);
    }

    public static <T> R<T> failed(T data, int code, String msg) {
        return restResult(data, code, msg);
    }

    public R() {
        super();
    }

    public R(T data) {
        super();
        this.data = data;
    }

    public R(T data, String msg) {
        super();
        this.data = data;
        this.msg = msg;
    }

    public R(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = 1;
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    /**
     * 判断是否成功
     *
     * @return
     */
    public boolean is() {
        return this.code == 0;
    }
}
