package com.baidu.entity;

import java.io.Serializable;

//封装用户信息到对象中
public class Result implements Serializable {
    private boolean flag;  //操作成功与失败
    private String message;

    public Result() {
    }

    public Result(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
