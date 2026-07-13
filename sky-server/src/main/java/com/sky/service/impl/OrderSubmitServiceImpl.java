package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderSubmitMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.AddressBookService;
import com.sky.service.OrderSubmitService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderSubmitServiceImpl implements OrderSubmitService {

    @Value("${sky.shop.address}")
    private String shopAddress;

    @Value("${sky.baidu.ak}")
    private String ak;

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private OrderSubmitMapper orderSubmitMapper;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        /*处理各种异常情况(地址，购物车为空/不存在)*/
        Integer addressBookId = ordersSubmitDTO.getAddressBookId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.SelectByUserId(BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new DeletionNotAllowedException("地址不存在");
        }

        // 检查收货地址是否超出配送范围
        String fullAddress = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        checkOutOfRange(fullAddress);
        if(shoppingCarts == null || shoppingCarts.isEmpty()){
            throw new DeletionNotAllowedException("购物车为空");
        }
        /*向订单表插入数据*/
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setUserId(BaseContext.getCurrentId());
        order.setAddressBookId(addressBookId.longValue());  // 手动设置地址簿ID
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        // 使用上面已拼接的完整地址
        order.setAddress(fullAddress);
        order.setPayStatus(Orders.UN_PAID);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setOrderTime(LocalDateTime.now());
        order.setNumber("000000" + System.currentTimeMillis());
        orderSubmitMapper.insert(order);

        /*向订单明细表插入数据*/
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetails.add(orderDetail);
        }
        orderSubmitMapper.submit(orderDetails);

        /*清空购物车*/
        shoppingCartMapper.clean();

        /*返回VO*/
        OrderSubmitVO build = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

        return build;

    }

    @Override
    public void repetition(Long id) {
        // 1. 查询订单所有明细
        List<OrderDetail> detailList = orderSubmitMapper.getByOrderId(id);
        if (detailList == null || detailList.isEmpty()) {
            throw new DeletionNotAllowedException("订单不存在或无菜品");
        }

        Long userId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();
        List<ShoppingCart> cartList = new ArrayList<>();

        // 循环把所有菜品转为购物车对象
        for (OrderDetail detail : detailList) {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(detail, cart, "id");
            cart.setUserId(userId);
            cart.setCreateTime(now);
            cartList.add(cart);
        }

        // 批量新增，不是update
        orderSubmitMapper.insertBatch(cartList);
    }

    @Override
    public PageResult pageQuery4User(Integer pageNum, Integer pageSize, Integer status) {
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderSubmitMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderSubmitMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    @Override
    public void cancel(Long id) {
        // 1. 查询订单
        Orders order = orderSubmitMapper.getById(id);
        if (order == null) {
            throw new DeletionNotAllowedException("订单不存在");
        }
        // 2. 校验订单状态是否为待支付或待接单
        if (order.getStatus() != Orders.PENDING_PAYMENT && order.getStatus() != Orders.TO_BE_CONFIRMED) {
            throw new DeletionNotAllowedException("订单状态不允许取消");
        }
        // 3. 取消订单
        order.setStatus(Orders.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setPayStatus(Orders.REFUND);
        order.setCancelReason("用户取消");
        orderSubmitMapper.updateById(order);
    }

    @Override
    public OrderVO details(Long id) {
        // 根据id查询订单
        Orders orders = orderSubmitMapper.getById(id);

        // 查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderSubmitMapper.getByOrderId(orders.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;

    }

    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map<String, String> map = new HashMap<>();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

        //获取店铺的经纬度坐标
        log.info("百度地图店铺地址解析请求：shopAddress={}, ak={}", shopAddress, ak);
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        log.info("百度地图店铺地址解析响应：{}", shopCoordinate);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(jsonObject == null || !"0".equals(jsonObject.getString("status"))){
            log.error("店铺地址解析失败，响应：{}", shopCoordinate);
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address", address);
        //获取用户收货地址的经纬度坐标
        log.info("百度地图用户地址解析请求：address={}, ak={}", address, ak);
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        log.info("百度地图用户地址解析响应：{}", userCoordinate);

        jsonObject = JSON.parseObject(userCoordinate);
        if(jsonObject == null || !"0".equals(jsonObject.getString("status"))){
            log.error("收货地址解析失败，响应：{}", userCoordinate);
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin", shopLngLat);
        map.put("destination", userLngLat);
        map.put("steps_info", "0");

        //路线规划
        log.info("百度地图路线规划请求：origin={}, destination={}", shopLngLat, userLngLat);
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);
        log.info("百度地图路线规划响应：{}", json);

        jsonObject = JSON.parseObject(json);
        if(jsonObject == null || !"0".equals(jsonObject.getString("status"))){
            log.error("配送路线规划失败，响应：{}", json);
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderSubmitMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderSubmitMapper.update(orders);

        /*设置来单提醒*/
        Map<String, Object> map = new HashMap<>();
        map.put("content", outTradeNo);
        map.put("orderId", ordersDB.getId());
        map.put("type",1);
        String json = JSON.toJSONString(map);
        //调用消息队列，发送消息
        webSocketServer.sendToAllClient(json);
    }

    @Override
    public void reminder(Long id) {
        // 根据订单id查询订单
        Orders ordersDB = orderSubmitMapper.getById(id);
        if(ordersDB == null){
            throw new OrderBusinessException("订单不存在");
        }
        /*设置催单提醒*/
        Map<String, Object> map = new HashMap<>();
        map.put("content", ordersDB.getNumber());
        map.put("orderId", ordersDB.getId());
        map.put("type",2);
        String json = JSON.toJSONString(map);
        //调用消息队列，发送消息
        webSocketServer.sendToAllClient(json);
    }

}