package org.whitefat.unmanned.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whitefat.unmanned.service.AccountService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuyong
 * @version 1.0
 * @Description:
 * @Createdate 2021/7/2 10:04 上午
 */
@Slf4j
@Api(value = "账户", tags = {"账户接口"})
@RestController("/accounts")
public class AccountController {

    @Resource
    private AccountService accountService;

    @ApiOperation(value = "查询账户")
    @GetMapping
    public List listAccounts() {
        return accountService.listAccounts();
    }


}
