/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: OrderServiceImpl
 * Author:   臧浩鹏
 * Date:     2018/7/27 8:13
 * Description: 订单和支付的实现接口
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhp.common.Const;
import com.zhp.common.ServerResponse;
import com.zhp.mapper.*;
import com.zhp.model.*;
import com.zhp.service.IOrderService;
import com.zhp.util.BigDecimalUtil;
import com.zhp.util.DateTimeUtil;
import com.zhp.util.FTPUtil;
import com.zhp.util.PropertiesUtil;
import com.zhp.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单和支付的实现接口〉
 *
 * @author 臧浩鹏
 * @create 2018/7/27
 * @since 1.0.0
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderitemMapper orderitemMapper;
    @Autowired
    private PayinfoMapper payinfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    private static AlipayTradeService tradeService;
    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         *
         */

         tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Override
    public ServerResponse pay(long orderNo, Integer uId, String path){
        HashMap<String, String> res = Maps.newHashMap();

        Order order = orderMapper.selectOrderByONoAndUid(orderNo,uId);
        if(order==null){
            return ServerResponse.createByErrorMessage("该用户没有改订单");
        }
        res.put("orderNo",String.valueOf(order.getOrderNo()));

            // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
            // 需保证商户系统端不能重复，建议通过数据库sequence生成，
            String outTradeNo = order.getOrderNo().toString();

            // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
            String subject = new StringBuilder("大佬门店--扫码支付，订单号：").append(outTradeNo).toString();

            // (必填) 订单总金额，单位为元，不能超过1亿元
            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
            String totalAmount = order.getPayment().toString();

            // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
            // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
            String undiscountableAmount = "0";

            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
            String sellerId = "";

            // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
            String body = new StringBuilder().append("订单").append(outTradeNo).append("购买了总共").append(totalAmount).append("元").toString();

            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
            String operatorId = "test_operator_id";

            // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
            String storeId = "test_store_id";

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId("2088100200300400500");

            // 支付超时，定义为120分钟
            String timeoutExpress = "120m";

            // 商品明细列表，需填写购买商品详细信息，
            List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

            //根据订单号拿到订单item,查找到其中的商品及商品数量及总价格等信息
            List<Orderitem> orderitems = orderitemMapper.selectByOrderNo(order.getOrderNo(),uId);
            for(Orderitem orderitem : orderitems){
                goodsDetailList.add(GoodsDetail.newInstance(orderitem.getProductId().toString(),
                        orderitem.getProductName(),
                        BigDecimalUtil.mul(orderitem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                        orderitem.getQuantity()));
            }


            // 创建扫码支付请求builder，设置请求参数
            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                    .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                    .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                    .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                    .setTimeoutExpress(timeoutExpress)
                    //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                    .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))
                    .setGoodsDetailList(goodsDetailList);




            AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
            switch (result.getTradeStatus()) {
                case SUCCESS:
                    log.info("支付宝预下单成功: )");

                    AlipayTradePrecreateResponse response = result.getResponse();
                    dumpResponse(response);

                    File folder = new File(path);
                    if(!folder.exists()){
                        folder.setWritable(true);
                        folder.mkdirs();
                    }

                    // 需要修改为运行机器上的路径
                    String qrPath = String.format(path+"/qr-%s.png",
                            response.getOutTradeNo());
                    String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                    ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                    File fileTarget = new File(path,qrFileName);

                    try {
                        FTPUtil.uploadFile(Lists.newArrayList(fileTarget));
                    } catch (IOException e) {
                        log.error("二维码上传异常！",e);
                    }
                    log.info("filePath:" + qrPath);

                    String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+fileTarget.getName();
                    res.put("qrUrl",qrUrl);

                    return ServerResponse.createBySuccess(res);
                case FAILED:
                    log.error("支付宝预下单失败!!!");
                    return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

                case UNKNOWN:
                    log.error("系统异常，预下单状态未知!!!");
                    return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

                default:
                    log.error("不支持的交易状态，交易返回异常!!!");
                    return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
            }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    @Override
    public ServerResponse aliCallBack(Map<String,String> params){
        long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNO(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("非大哥商城订单！");
        }
        if(order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复调用!");
        }
        if(Const.AlipayCallBack.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        Payinfo payinfo = new Payinfo();
        payinfo.setOrderNo(order.getOrderNo());
        payinfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payinfo.setUserId(order.getUserId());
        payinfo.setPlatformNumber(tradeNo);
        payinfo.setPlatformStatus(tradeStatus);

        int insert = payinfoMapper.insert(payinfo);
        if (insert>0){
            log.info("支付信息插入成功!");
        }else {
            log.info("支付信息插入失败！");
        }
        return ServerResponse.createBySuccess();

    }

    @Override
    public ServerResponse<Boolean> queryOderPayStatus(Integer orderNo, Integer uId) {
        Order order = orderMapper.selectOrderByONoAndUid(orderNo,uId);
        if(order==null){
            return ServerResponse.createByErrorMessage("该用户没有改订单");
        }
        if(order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse createOrder(Integer uId, Integer shippingId) {

        List<Cart> carts = cartMapper.selectCheckedCartByUserId(uId);

        ServerResponse response = this.getCartOrderItem(uId, carts);
        if(!response.isSuccess()){
            return response;
        }
        List<Orderitem> orderitemList = (List<Orderitem>) response.getData();

        BigDecimal payment = this.getOrderTotalPrice(orderitemList);
        Order order = this.assembleOrder(uId, shippingId, payment);
        if(order==null){
            return ServerResponse.createByErrorMessage("生成订单错误！");
        }
        if(CollectionUtils.isEmpty(orderitemList)){
            return ServerResponse.createByErrorMessage("购物车为空！");
        }
        for(Orderitem orderitem : orderitemList){
            orderitem.setOrderNo(order.getOrderNo());
        }
        //Mybatis的批量插入
        int res = orderitemMapper.batchInsert(orderitemList);

        //订单生成成功后要进行减少库存，以及清空购物车
        this.reduceProductStock(orderitemList);
        this.cleanCart(carts);

        //返回订单支付明细 给前端，封装一个vo
        OrderVo orderVo = this.assembleOrderVo(order, orderitemList);

        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse concelOrder(Integer uid, long orderNo) {
        Order order = orderMapper.selectOrderByONoAndUid(orderNo, uid);
        if(order==null){
            log.info(order.getOrderNo()+"该订单不存在数据库中！");
            return ServerResponse.createByErrorMessage("该订单不存在数据库中！");
        }
        if(order.getStatus()!=Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("已付款 无法取消订单！！！");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int count = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(count>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer uid) {
        OrderProductVo orderProductVo = new OrderProductVo();
        List<Cart> carts = cartMapper.selectCheckedCartByUserId(uid);
        ServerResponse response = this.getCartOrderItem(uid, carts);
        if(!response.isSuccess()){
            return response;
        }
        List<Orderitem> orderitems = (List<Orderitem>)response.getData();

        List<OrderItemVo> orderItemVos = Lists.newArrayList();
        BigDecimal payMent = new BigDecimal("0");
        for(Orderitem oitem : orderitems){
            payMent = BigDecimalUtil.add(payMent.doubleValue(),oitem.getTotalPrice().doubleValue());
            orderItemVos.add(assembleOrderItem(oitem));
        }

        orderProductVo.setProductTotalPrice(payMent);
        orderProductVo.setOrderItemVoList(orderItemVos);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse getOrderDetail(Integer uid, long orderNo) {
        Order order = orderMapper.selectOrderByONoAndUid(orderNo, uid);
        if(order==null){
            return ServerResponse.createByErrorMessage("订单不存在！");
        }
        List<Orderitem> orderitems = orderitemMapper.selectByOrderNo(orderNo, uid);
        OrderVo orderVo = assembleOrderVo(order, orderitems);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse<PageInfo> getOrderList(Integer uid, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.selectOrderByUid(uid);
        List<OrderVo> orderVoList = assembleOrderVoList(orders, uid);
        PageInfo pageRes = new PageInfo(orderVoList);
       // pageRes.setList(orderVoList);
        return ServerResponse.createBySuccess(pageRes);
    }

    @Override
    public ServerResponse<PageInfo> manageList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.listOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orders, null);
        PageInfo orderVoPageInfo = new PageInfo(orders);
        orderVoPageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(orderVoPageInfo);
    }

    @Override
    public ServerResponse<OrderVo> manageorderDetail(long orderNo) {
        Order order = orderMapper.selectByOrderNO(orderNo);
        if(order != null){
            List<Orderitem> orderitems = orderitemMapper.selectAllByAdmin(orderNo);
            OrderVo orderVo = assembleOrderVo(order, orderitems);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在！");
    }

    @Override
    public ServerResponse<PageInfo> manageorderSearch(long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNO(orderNo);
        if(order != null){
            List<Orderitem> orderitems = orderitemMapper.selectAllByAdmin(orderNo);
            OrderVo orderVo = assembleOrderVo(order, orderitems);
            PageInfo pageRes = new PageInfo(Lists.newArrayList(order));
            pageRes.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageRes);
        }
        return ServerResponse.createByErrorMessage("订单不存在！");
    }

    @Override
    public ServerResponse<String> orderSendGoods(long orderNo) {
        Order order = orderMapper.selectByOrderNO(orderNo);
        if(order!=null){

            if (Const.OrderStatusEnum.PAID.getCode() == order.getStatus()){
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("订单发货成功！");
            }

        }
        return ServerResponse.createByErrorMessage("不存在该订单！请重新核实");
    }
    /**
     *
     * @Description: 实现定时关单
     *
     * @auther: 臧浩鹏
     * @date: 14:33 2018/8/1
     * @param: [hour]
     * @return: void
     *
     */
    @Override
    public void closeOrder(int hour) {

        Date deadtime = DateUtils.addHours(new Date(), -hour);
        List<Order> orders = orderMapper.selectOrderStatusByCreateTime(Const.OrderStatusEnum.NO_PAY.getCode(),DateTimeUtil.dateToStr(deadtime));
        for(Order order:orders){
                Long orderNo = order.getOrderNo();
                Integer userId = order.getUserId();
                List<Orderitem> orderitems = orderitemMapper.selectByOrderNo(orderNo, userId);
                for(Orderitem orderitem:orderitems){
                    Integer productId = orderitem.getProductId();
                    Integer stock = productMapper.selectStockByProductId(productId);
                    //考虑到已删除的商品被删除的情况
                    if(stock == null){
                        continue;
                    }
                    Integer quantity = orderitem.getQuantity();
                    Product temp2 = new Product();
                    temp2.setId(productId);
                    temp2.setStock(stock+quantity);
                    productMapper.updateByPrimaryKeySelective(temp2);
                }
            Order temp = new Order();
            temp.setId(order.getId());
            temp.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
            int res = orderMapper.updateByPrimaryKeySelective(temp);
            if(res>0){
                log.info("关闭超时订单 OrderNo {} 成功！",orderNo);
            }
        }
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orders,Integer uid){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for(Order order : orders){
            List<Orderitem> orderitems = Lists.newArrayList();
            if(uid == null) {
                //TODO
                //管理员查询时不需要传id
                orderitems = orderitemMapper.selectAllByAdmin(order.getOrderNo());
            }else {
                orderitems = orderitemMapper.selectByOrderNo(order.getOrderNo(), uid);
            }
            OrderVo orderVo = assembleOrderVo(order, orderitems);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    private OrderVo assembleOrderVo(Order order,List<Orderitem> orderitems){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPostage(order.getPostage());
        orderVo.setPaymentTypeDesc(Const.payMentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setStatus(Const.OrderStatusEnum.codeOf(order.getStatus()).getCode());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping!=null){
            orderVo.setReceiverName(shipping.getReceiverName());
            ShippingVo shippingVo = this.assembleShippingVo(shipping);
            orderVo.setShippingVo(shippingVo);
        }
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        ArrayList<OrderItemVo> orderitemsVo = Lists.newArrayList();
        for(Orderitem orderitem : orderitems){
            orderitemsVo.add(assembleOrderItem(orderitem));
        }
        orderVo.setOrderItemVoList(orderitemsVo);
        return orderVo;

    }

    private OrderItemVo assembleOrderItem(Orderitem orderitem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderitem.getCreateTime()));
        orderItemVo.setCurrentUnitPrice(orderitem.getCurrentUnitPrice());
        orderItemVo.setOrderNo(orderitem.getOrderNo());
        orderItemVo.setProductId(orderitem.getProductId());
        orderItemVo.setProductImage(orderitem.getProductImage());
        orderItemVo.setQuantity(orderitem.getQuantity());
        orderItemVo.setTotalPrice(orderitem.getTotalPrice());
        orderItemVo.setProductName(orderitem.getProductName());
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }

    private void cleanCart(List<Cart> carts){
        for(Cart cart : carts){
        cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<Orderitem> orderitems){
        for(Orderitem orderitem:orderitems) {
            Product product = productMapper.selectByPrimaryKey(orderitem.getProductId());
            product.setStock(product.getStock()-orderitem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.payMentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间等等

        //付款时间
        System.out.println(order.toString());

        int rowCount = orderMapper.insert(order);
        if(rowCount>0){
            return order;
        }
        return null;
    }

    /**
     *
     * @Description: 此处有瑕疵，当并发下单时，不同的人在同时下了单，但数据库要求订单号不同，所以会存在有人下单失败的状态
     *                  此时为了缓解 增加随机数 salt
     *               此时可利用订单缓存池 例如在第二天的时候提前一天进行不同订单号的生成，然后直接从订单池里取，此时伴随一个deamon thread
     *               不断监控订单池总量，若数量不够则再动态的向里面添加！
     *
     * @auther: 臧浩鹏
     * @date: 16:30 2018/7/27
     * @param: []
     * @return: long
     *
     */
    private long generateOrderNo(){
        long res = System.currentTimeMillis();
        return res+new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<Orderitem> orderitems){
        BigDecimal payment = new BigDecimal("0");

        for(Orderitem orderitem : orderitems){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderitem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private ServerResponse getCartOrderItem(Integer uId,List<Cart> carts){
        if(CollectionUtils.isEmpty(carts)){
            return ServerResponse.createByErrorMessage("不存在要提交的购物车商品信息！");
        }
        ArrayList<Orderitem> orderItems = Lists.newArrayList();
        for(Cart cartItem : carts){
            Orderitem orderitem = new Orderitem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()){
                return ServerResponse.createByErrorMessage("对不起，产品已下架，请浏览其他商品~~~");
            }
            //校验库存
            if(cartItem.getQuantity() > product.getStock()){
                return ServerResponse.createByErrorMessage("哈哈 下手晚啦！！！商品已卖光！！！");
            }
            orderitem.setUserId(uId);
            orderitem.setProductId(product.getId());
            orderitem.setProductName(product.getName());
            orderitem.setProductImage(product.getMainImage());
            orderitem.setCurrentUnitPrice(product.getPrice());
            orderitem.setQuantity(cartItem.getQuantity());
            orderitem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            orderItems.add(orderitem);
        }
        return ServerResponse.createBySuccess(orderItems);
    }

}
