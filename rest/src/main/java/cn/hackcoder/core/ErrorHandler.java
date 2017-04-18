package cn.hackcoder.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(StatusCode.class);

    public static Result error(int errorCode) {
        return Result.fail(errorCode, StatusCode.codeMap.get(errorCode));
    }

    public static Result error(int errorCode, String parameter) {
        return Result.fail(errorCode, String.format(StatusCode.codeMap.get(errorCode), parameter));
    }
}
