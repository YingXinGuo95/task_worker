package com.guoyx.constants;

/**
 * 返回码枚举
 */
public enum RespCode {
    SUCCESS(0L, "成功"),
    FAIL(-1L, "失败");

    private String message;
    private Long code;

    RespCode(Long code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Long getCode() {
        return code;
    }
}
