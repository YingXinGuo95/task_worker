package com.guoyx.entity;

import com.guoyx.constants.RespCode;
import lombok.Data;

@Data
public class Response {
    private Long code;
    private String message;
    private Object data;

    public Response() {
    }

    public Response(RespCode rsp) {
        this.code = rsp.getCode();
        this.message = rsp.getMessage();
    }

    public Response(RespCode rsp, Object data) {
        this.code = rsp.getCode();
        this.message = rsp.getMessage();
        this.data = data;
    }

    public Response(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(Long code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
