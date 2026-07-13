package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrdersCancelDTO implements Serializable {

    @JsonAlias({"orderId"})
    private Long id;
    //订单取消原因
    private String cancelReason;

}
