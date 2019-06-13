package com.xin.utils.web;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author luchaoxin
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

    public R() {
        super();
    }

    public R(T data) {
        this.data = data;
    }

    public R(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public R(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = 1;
    }

    public static <T> R<T> ok(T data) {
        ApiResultCode apiResultCode = ApiResultCode.SUCCESS;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            apiResultCode = ApiResultCode.FAILED;
        }
        return restResult(data, apiResultCode);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, ApiResultCode.FAILED.getStatus(), msg);
    }

    public static <T> R<T> failed(ApiResultCode apiResultCode) {
        return restResult(null, apiResultCode);
    }

    public static <T> R<T> restResult(T data, ApiResultCode errorCode) {
        return restResult(data, errorCode.getStatus(), errorCode.getMessage());
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public boolean ok() {
        return ApiResultCode.SUCCESS.getStatus() == code;
    }
}
