package org.whitefat.unmanned.util;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;

/**
 * @author liuyong
 * @version 1.0
 * @Description:
 * @Createdate 2023/6/7 11:03 上午
 **/
public final class UnmannedUtil {

    private UnmannedUtil(){
    }

    private static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:7545"));

    @PostConstruct
    private void init(){

    }


}
