package org.whitefat.unmanned.service;

import java.util.List;

/**
 * @author liuyong
 * @version 1.0
 * @Description: 账户service
 * @Createdate 2021/6/30 6:59 下午
 */
public interface AccountService {


    /**
     * 查询所有账户
     *
     * @return
     */
    List listAccounts();

    /**
     * 创建账户
     *
     * @param type
     */
    void createAccount(Integer type);

}
