package com.leyou.common.exception;

import lombok.Getter;

/**
 * @author Leslie Arnoald
 */
@Getter
public class LyException extends RuntimeException {
    /**
     * 异常状态码信息
     */
    private int status;

    public LyException(int status) {
        this.status = status;
    }

    public LyException(int status, String message) {
        super(message);
        this.status = status;
    }

    //Throwable是所有异常的顶级父类
    public LyException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public LyException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }
}