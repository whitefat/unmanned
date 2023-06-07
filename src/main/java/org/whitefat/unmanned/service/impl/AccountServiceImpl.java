package org.whitefat.unmanned.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.whitefat.unmanned.model.response.AccountResponse;
import org.whitefat.unmanned.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liuyong
 * @version 1.0
 * @Description:
 * @Createdate 2023/6/7 10:57 上午
 **/
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    //public static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:7545"));
    public static Web3j web3 = Web3j.build(new HttpService("https://goerli.infura.io/v3/9e87ea95e89f4ac9b7e62539bc63ccf7"));

    @Override
    public List listAccounts() {
        List<AccountResponse> responses = Lists.newArrayList();
        try {
            EthAccounts ethAccounts = web3.ethAccounts().send();
            List<String> accounts = ethAccounts.getAccounts();

            for (String address : accounts) {
                AccountResponse accountResponse = new AccountResponse();
                accountResponse.setAddress(address);
                EthGetBalance ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
                accountResponse.setBalance(ethGetBalance.getBalance());
                accountResponse.setViewBalance(Convert.fromWei(accountResponse.getBalance().toString(), Convert.Unit.ETHER));
                responses.add(accountResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responses;
    }

    public static void main(String[] args) throws Exception{
        EthAccounts ethAccounts = web3.ethAccounts().send();
        List<String> accounts = ethAccounts.getAccounts();

        EthGetBalance ethGetBalance = web3.ethGetBalance("0x99a4A7dD4edf04701678993B253198304b2cc78D", DefaultBlockParameterName.LATEST).send();
        System.out.println("getBalance -> " +ethGetBalance.getBalance());
        System.out.println("getGasPrice -> " +web3.ethGasPrice().send().getGasPrice());
        System.out.println("getWeb3ClientVersion -> " +web3.web3ClientVersion().send().getWeb3ClientVersion());
        System.out.println("getWork -> " +web3.ethGetWork().send().getJsonrpc());
        System.out.println("getBlockNumber -> " +web3.ethBlockNumber().send().getBlockNumber());
        System.out.println("getChainId -> " +web3.ethChainId().send().getChainId());
        System.out.println("getAddressCoinbase -> " +web3.ethCoinbase().send().getAddress());
        System.out.println("isMining -> " +web3.ethMining().send().isMining());
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("getDataDir -> " +web3.adminDataDir().send().getDataDir());
        System.out.println("adminNodeInfo -> " +web3.adminNodeInfo().send().getJsonrpc());
        System.out.println("adminPeers -> " +web3.adminPeers().send().getJsonrpc());

    }
}
