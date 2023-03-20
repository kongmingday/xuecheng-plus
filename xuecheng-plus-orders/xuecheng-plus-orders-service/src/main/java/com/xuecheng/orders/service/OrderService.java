package com.xuecheng.orders.service;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

/**
 * @author eotouch
 * @version 1.0
 * @description 订单接口
 * @date 2023/03/19 22:50
 */
public interface OrderService {

    /**
     * @description 创建订单
     * @param userId 用户名称
     * @param addOrderDto 添加订单参数
     * @return com.xuecheng.orders.model.dto.PayRecordDto
     * @author eotouch
     * @date 2023-03-19 22:50
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @description 获取支付交易记录
     * @param payNo 交易id
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @author eotouch
     * @date 2023-03-19 23:16
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * @description 保存支付状态
     * @param payStatusDto  支付状态参数
     * @return void
     * @author eotouch
     * @date 2023-03-20 15:20
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto);

    /**
     * @description 查询支付结果
     * @param payNo 支付订单号
     * @return com.xuecheng.orders.model.dto.PayRecordDto
     * @author eotouch
     * @date 2023-03-20 15:28
     */
    public PayRecordDto queryPayResult(String payNo);

    /**
     * 发送通知结果
     * @param message
     */
    public void notifyPayResult(MqMessage message);

}
