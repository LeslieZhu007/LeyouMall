package com.leyou.trade.DTO;

import com.leyou.common.dto.BaseDTO;
import com.leyou.trade.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author 虎哥
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderDTO extends BaseDTO {
    private Long orderId;
    private Long totalFee;
    private Long postFee;
    private Long actualFee;
    private Integer paymentType;
    private Long userId;
    private Integer status;
    private Date createTime;
    private Date payTime;
    private Date consignTime;
    private Date endTime;
    private Date closeTime;
    private Date commentTime;

    public OrderDTO(Order entity) {
        super(entity);
    }
}