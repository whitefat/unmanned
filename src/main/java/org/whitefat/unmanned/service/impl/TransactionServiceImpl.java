package org.whitefat.unmanned.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Bip44WalletUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.whitefat.unmanned.service.TransactionService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author liuyong
 * @version 1.0
 * @Description:
 * @Createdate 2023/6/14 4:43 下午
 **/
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    public static Web3j web3 = Web3j.build(new HttpService());
    public static Admin admin = Admin.build(new HttpService());

    public static void main(String[] args) throws Exception {
        //sendHoxTransaction();
        //sendColdRawTransaction();

        //EthAccounts ethAccounts = admin.ethAccounts().send();
        //List<String> accounts = ethAccounts.getAccounts();
//        List<String> accounts = Lists.newArrayList("0x98043f4a6bd912ec5c06bd95cc53abdd9e33cb41",
//                "0x4696608854c17bced1f23f5f1ff58ddb0b0fdf46",
//                "0x575cd404edb322af8a13008423e2ac868a1250bb");
//        System.out.println("accounts -> " + accounts);
//        for (String addr : accounts) {
//            EthGetBalance ethGetBalance = admin.ethGetBalance(addr, DefaultBlockParameterName.LATEST).send();
//            System.out.println(addr+" getBalance -> " + ethGetBalance.getBalance());
//            System.out.println(addr+" getBalanceETHER -> " + Convert.fromWei(ethGetBalance.getBalance().toString(),Convert.Unit.ETHER));
//        }

//        BigInteger a = new BigInteger("1588417366").multiply(new BigInteger("21000"));
//        BigInteger b = new BigInteger("2100000000000000000").subtract(new BigInteger("1999991485644143000")).subtract(Convert.toWei("0.1", Convert.Unit.ETHER).toBigIntegerExact());
//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(a.compareTo(b) == 0);

//        BigInteger a = new BigInteger("8514355857000");
//        BigInteger b = new BigInteger("405445517");
//        System.out.println(a.divide(b));

        //System.out.println("MaxPriorityFeePerGas -> "+admin.ethMaxPriorityFeePerGas().send().getMaxPriorityFeePerGas());
        //System.out.println("Block -> "+JSON.toJSONString(admin.ethGetBlockByNumber(DefaultBlockParameterName.LATEST,true).send().getBlock()));

        BigInteger a = new BigInteger("396445517").add(new BigInteger("9000000"));
        BigInteger b = new BigInteger("405445517");
        System.out.println(a.compareTo(b) == 0);
    }

    /**
     * step1:交易前查询余额
     * 0x98043f4a6bd912ec5c06bd95cc53abdd9e33cb41   3899969826699381000   3.899969826699381
     * 0x4696608854c17bced1f23f5f1ff58ddb0b0fdf46   100000000000000000    0.1
     * 0xd5baffe73076eb1263b72886107e0a5a29c729fa   2000000000000000000   2
     *
     * step2:交易
     * nonce -> 0
     * gasPrice -> 1671198839
     * gasLimit -> 21000
     * value -> 100000000000000000
     * fromAddress -> 0xd5baffe73076eb1263b72886107e0a5a29c729fa
     * toAddress -> 0x4696608854c17bced1f23f5f1ff58ddb0b0fdf46
     * hash -> 0x3a97c0a1e251667f11cb898632ff8698cfddc7a7a5abe5b98ec39dda6c4446c4
     * fee1 -> 35095175619000 = gasPrice * gasLimit
     *
     * step3:交易后查询余额 fee1=fee2
     * fee2 -> 0.000035095.175619000 = 0xd5baffe73076eb1263b72886107e0a5a29c729fa余额变动(2-1.899964904824381-0.1)
     * 0x98043f4a6bd912ec5c06bd95cc53abdd9e33cb41   5899992565110314000   5.899992565110314
     * 0x4696608854c17bced1f23f5f1ff58ddb0b0fdf46   200000000000000000    0.2
     * 0xd5baffe73076eb1263b72886107e0a5a29c729fa   1899964904824381000   1.899964904824381
     *
     * @throws Exception
     */
    private static void sendHoxTransaction() throws Exception {
        String fromAddress = "0xd5baffe73076eb1263b72886107e0a5a29c729fa";
        PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(fromAddress, "123456").send();
        if (personalUnlockAccount.accountUnlocked()) {
            BigInteger nonce = web3.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send().getTransactionCount();
            BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
            BigInteger gasLimit = Transfer.GAS_LIMIT;
            String toAddress = "0x575cd404edb322af8a13008423e2ac868a1250bb";
            BigInteger value = Convert.toWei(new BigDecimal("0.1"), Convert.Unit.ETHER).toBigInteger();
            System.out.println("nonce -> " + nonce);
            System.out.println("gasPrice -> " + gasPrice);
            System.out.println("gasLimit -> " + gasLimit);
            System.out.println("value -> " + value);
            System.out.println("fromAddress -> " + fromAddress);
            System.out.println("toAddress -> " + toAddress);
            Transaction transaction = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, value);
            EthSendTransaction sendTransaction = web3.ethSendTransaction(transaction).send();
            String hash = sendTransaction.getTransactionHash();
            System.out.println("hash -> " + hash);
        }
    }

    /**
     * eip1559 - cold
     * <p>
     * step1:交易前查询余额
     * 0x98043f4a6bd912ec5c06bd95cc53abdd9e33cb41   7900005592475623000   7.900005592475623
     * 0x4696608854c17bced1f23f5f1ff58ddb0b0fdf46   200000000000000000    0.2
     * 0x575cd404edb322af8a13008423e2ac868a1250bb   2100000000000000000   2.1
     * <p>
     * step2:交易
     * chainId -> 100
     * nonce -> 0
     * value -> 100000000000000000
     * maxPriorityFeePerGas -> 9000000
     * maxFeePerGas -> 3100000000
     * fromAddress -> 0x575cd404edb322af8a13008423e2ac868a1250bb
     * toAddress -> 0x4696608854c17BCED1F23f5F1fF58Ddb0B0FDF46
     * hexValue -> 0x02f87164808389544084b8c63f00825208944696608854c17bced1f23f5f1ff58ddb0b0fdf4688016345785d8a000080c080a0621b54cfb25719dd5ad802f86c8eda79c0502176d69e7ddc8557fd90a59e6418a0672be7142e7ffc30f9c68b95a22c2091df5b9ab308ba2c7fdc0606aa53db9a09
     * hash -> 0x95e5222972c77db390a21564e11af076fc154c8ee3a3906aadc9b21a9e9ca1fc
     *
     * eth.getTransaction("0x95e5222972c77db390a21564e11af076fc154c8ee3a3906aadc9b21a9e9ca1fc")查询交易得到下面
     * gas: 21000,
     * gasPrice: 405445517,
     * baseFeePerGas: 396445517, -- getBlock获取
     * maxFeePerGas: 3100000000, -- getTransaction获取
     * maxPriorityFeePerGas: 9000000, -- getTransaction获取
     * fee -> 8514355857000 = gasPrice( = baseFeePerGas+maxPriorityFeePerGas) * gasLimit
     *
     * step3:交易后查询余额
     * fee -> 0.000008514.355857000 = 0x575cd404edb322af8a13008423e2ac868a1250bb余额变动(2.1-1.999991485644143-0.1)
     * 0x98043f4a6bd912ec5c06bd95cc53abdd9e33cb41   9900005781475623000   9.900005781475623
     * 0x4696608854c17bced1f23f5f1ff58ddb0b0fdf46   300000000000000000    0.3
     * 0x575cd404edb322af8a13008423e2ac868a1250bb   1999991485644143000   1.999991485644143
     *
     * @throws Exception
     */
    private static void sendColdRawTransaction() throws Exception {
        String password = "createWallet3";
        String mnemonic = "slot evoke bike spend soft explain decide orange start dawn couple fiber";
        String source = "/Users/liuyong/workspace/project/personal/unmanned/src/destination/UTC--2023-06-12T09-20-09.578000000Z--575cd404edb322af8a13008423e2ac868a1250bb.json";
        String content = "{\"address\":\"575cd404edb322af8a13008423e2ac868a1250bb\",\"id\":\"e1644325-5e00-474d-885e-1c56b0ddb626\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"c20c02609a3a8565666b89f1d72ce939548363e2b237e5c0f9345b632ce527ae\",\"cipherparams\":{\"iv\":\"46087633adca923db4bbe388ab286218\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"34e233fbc03036022eaa1c75c2fb886f2878885f4a052d5efb5df688c5b6b946\"},\"mac\":\"8612d2661857277d1f809e8119ae448ffdd7414d113008c15b421d49ebb9cd17\"}}";
        //使用source获取凭证
        //Credentials credentials = Bip44WalletUtils.loadCredentials(password, source);
        //使用助记词获取凭证-Bip44
        //Credentials credentials = Bip44WalletUtils.loadBip44Credentials("", mnemonic);
        //使用json获取凭证
        Credentials credentials = Bip44WalletUtils.loadJsonCredentials(password, content);
        String fromAddress = credentials.getAddress();
        long chainId = admin.ethChainId().send().getChainId().longValue();
        BigInteger nonce = web3.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        //BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = Transfer.GAS_LIMIT;
        String toAddress = "0x4696608854c17BCED1F23f5F1fF58Ddb0B0FDF46";
        BigInteger value = Convert.toWei(new BigDecimal("0.1"), Convert.Unit.ETHER).toBigInteger();
        BigInteger maxPriorityFeePerGas = DefaultGasProvider.GAS_LIMIT;
        //0.009000000
        //3.100000000
        BigInteger maxFeePerGas = BigInteger.valueOf(3_100_000_000L);
        System.out.println("chainId -> " + chainId);
        System.out.println("nonce -> " + nonce);
        //System.out.println("gasPrice -> " + gasPrice);
        System.out.println("gasLimit -> " + gasLimit);
        System.out.println("value -> " + value);
        System.out.println("maxPriorityFeePerGas -> " + maxPriorityFeePerGas);
        System.out.println("maxFeePerGas -> " + maxFeePerGas);
        System.out.println("fromAddress -> " + fromAddress);
        System.out.println("toAddress -> " + toAddress);
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(chainId, nonce, gasLimit, toAddress, value, maxPriorityFeePerGas, maxFeePerGas);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        System.out.println("hexValue -> " + hexValue);
        EthSendTransaction sendTransaction = web3.ethSendRawTransaction(hexValue).send();
        String hash = sendTransaction.getTransactionHash();
        System.out.println("hash -> " + hash);
    }


}
