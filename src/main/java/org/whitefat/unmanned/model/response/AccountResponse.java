package org.whitefat.unmanned.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author liuyong
 * @version 1.0
 * @Description:
 * @Createdate 2021/7/8 6:43 下午
 */
@Data
@ApiModel("查询账户响应体")
public class AccountResponse implements Serializable {

    private static final long serialVersionUID = 7689439868979033420L;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("余额")
    private BigInteger balance;

    @ApiModelProperty("显示余额")
    private BigDecimal viewBalance;

}
