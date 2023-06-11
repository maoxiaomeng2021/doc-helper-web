package life.gzx.dochelper;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.healthylife.gzx.dochelper.BusinessException;

/**
 * 统一异常处理
 *
 * @author maoxiaomeng
 */
@Slf4j
@ControllerAdvice({"*.*"})
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public R<?> handleEagleException(BusinessException exception) {
        log.error("业务异常:{}", exception.getMessage());
        return R.failed(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R<?> handleEagleException(Exception e) {
        log.error("通用异常捕获", e);
        return R.failed(e.getMessage());
    }
}
