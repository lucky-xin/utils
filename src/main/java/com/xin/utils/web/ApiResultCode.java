package com.xin.utils.web;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: REST API 错误码
 * @date 2019-04-20 12:20
 */
public enum ApiResultCode {
    /**
     * 失败
     */
    FAILED(1, "操作失败"),
    /**
     * 成功
     */
    SUCCESS(0, "执行成功");

    private int status;

    private String message;

    ApiResultCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ApiResultCode fromCode(long code) {
        for (ApiResultCode ec : ApiResultCode.values()) {
            if (ec.getStatus() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
