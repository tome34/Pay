package com.replay.limty.model.common;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class OrderInfo {

    /**
     * 商品名
     */
    private String body;
    /**
     * 订单号
     */
    private String orderNumber;
    /**
     * 订单金额
     */
    private String money;
    /**
     * 支付类型
     */
    private String payType;
    /**
     * 自定义参数
     */
    private String attach;


    public static OrderInfo instance;

    private OrderInfo() {
    }

    public static OrderInfo getInstance(){
        if(instance == null){
            instance = new OrderInfo();
        }
        return instance;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "body='" + body + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", money='" + money + '\'' +
                '}';
    }
}
