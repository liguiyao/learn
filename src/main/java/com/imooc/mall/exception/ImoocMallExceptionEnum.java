package com.imooc.mall.exception;

public enum ImoocMallExceptionEnum {
    NEED_USER_NAME(10001, "用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TOO_SHORT(10003, "密码长度不能小于八位"),
    NAME_EXISTED(10004, "不允许重名"),
    INSERT_FAILED(10005, "插入失败，请重试"),
    WRONG_PASSWORD(10006, "用户名或密码错误"),
    NEED_LOGIN(10007, "用户未登录"),
    UPDATE_FAILED(10008, "更新失败"),
    NEED_ADMIN(10009, "不是管理员"),
    REQUEST_PARAM_ERROR(10010, "参数错误"),
    DELETE_FAILED(10011, "删除失败"),
    NOT_CATEGORY(10012, "未找到分类"),
    MKDIR_FAILED(10013, "创建文件夹失败"),
    UPLOAD_FAILED(10014, "图片上传失败"),
    NOT_PRODUCT(10015, "未找到商品"),
    NOT_SALE(10016, "商品状态不可售"),
    NOT_STOCK(10017, "库存不足"),
    EMPTY_CART(10018,"未选中商品"),
    ORDER_STATUS_ERROR(10019,"订单状态异常"),
    NO_ORDER(10020,"订单不存在"),
    NOT_U_ORDER(10021,"订单不属于你"),
    SYSTEM_ERROR(20000, "系统异常");


    Integer code;
    String msg;

    ImoocMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
