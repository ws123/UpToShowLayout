package com.carlos.uptoshow.mylibrary;

/**
 * Created by carlos on 2016/6/2.
 * 自定义库的异常类
 */
public class CarlosException extends RuntimeException {
    public CarlosException(String detailMessage) {
        super(detailMessage);
    }
}
