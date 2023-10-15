package org.whitefat.unmanned.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.admin.AdminDataDir;
import org.web3j.protocol.core.methods.response.admin.AdminNodeInfo;
import org.web3j.protocol.core.methods.response.admin.AdminPeers;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.whitefat.unmanned.enums.CreateAccountType;
import org.whitefat.unmanned.model.response.AccountResponse;
import org.whitefat.unmanned.service.AccountService;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;

/**
 * @author liuyong
 * @version 1.0
 * @Description:
 * @Createdate 2023/6/7 10:57 上午
 **/
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    /**
     * path路径
     */
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(
                    new ChildNumber(44, true),
                    new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    //public static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:7545"));
    //public static Web3j web3 = Web3j.build(new HttpService("https://goerli.infura.io/v3/9e87ea95e89f4ac9b7e62539bc63ccf7"));
    public static Web3j web3 = Web3j.build(new HttpService());
    public static Admin admin = Admin.build(new HttpService());

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

    /**
     * HD钱包（Hierarchical Deterministic Wallets）分层确定性钱包，是在BIP32中提出的为了避免管理一堆私钥的麻烦提出的分层推导方案。
     * 而BIP44是给BIP32的分层增强了路径定义规范，同时增加了对多币种的支持。
     * BIP39则通过定义助记词让种子的备份更友好。
     *
     * 创建的钱包的流程为:
     *  1、随机生成一组助记词
     *  2、生成 一个种子seed
     *  3、根据seed生成公钥、私钥、地址
     *  4、根据公钥、 私钥、 密码生成钱包文件，也就是Keystore
     */
    @Override
    public void createAccount(Integer type) {
        CreateAccountType createAccountType = CreateAccountType.get(type);
        if (Objects.isNull(createAccountType)) {
            return;
        }

    }

    /**
     * 创建钱包 password = createWallet1
     *
     * 助记词：
     * [fever, mango, retire, provide, wide, senior, midnight, derive, cancel, scissors, bonus, neglect]
     * 地址：
     * 0x6fa0c2bb9ea7962d478be5d59b19d84cb78c0ce6
     * 私钥：
     * 0x26a7c1108f23daed0eb67a99bea20d50ff5433a83e279279df467a9e5061cfa
     * 公钥：
     * 2983bf5e196f4048b6d3e29e902e2cb5601021688660a90885b4c6521b9c6c2a0208e54ff987c9409a9a1a43d1880f1e4ddceda7a52408bfad86b57a1ab8bc5
     * wallFile：
     * UTC--2023-06-12T09-20-09.233000000Z--6fa0c2bb9ea7962d478be5d59b19d84cb78c0ce6.json
     */
    private static void createWallet1() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
        secureRandom.nextBytes(entropy);
        //生成12位助记词
        List<String>  str = MnemonicCode.INSTANCE.toMnemonic(entropy);
        //使用助记词生成钱包种子
        byte[] seed = MnemonicCode.toSeed(str, "");
        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
        DeterministicKey deterministicKey = deterministicHierarchy
                .deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
        byte[] bytes = deterministicKey.getPrivKeyBytes();
        ECKeyPair keyPair = ECKeyPair.create(bytes);
        // 生成 Keystore
        String walletFilePath="/Users/liuyong/workspace/project/personal/unmanned/src/destination";
        String walletFile = WalletUtils.generateWalletFile("createWallet1", keyPair, new File(walletFilePath), false);
        //通过公钥生成钱包地址
        String address = Keys.getAddress(keyPair.getPublicKey());
        System.out.println("助记词：");
        System.out.println(str);
        System.out.println("地址：");
        System.out.println("0x"+address);
        System.out.println("私钥：");
        System.out.println("0x"+keyPair.getPrivateKey().toString(16));
        System.out.println("公钥：");
        System.out.println(keyPair.getPublicKey().toString(16));
        System.out.println("wallFile：");
        System.out.println(walletFile);
    }

    /**
     * 创建钱包 password = createWallet2
     *
     * 助记词：
     * earn caught duck fever motor taxi popular leisure state wire regular sick
     * 地址：
     * 0x9b2b364ed60d9ce42e2b66835f740e9680462771
     * 私钥：
     * 0xe61cf3cd61fa613e6216c4cba7ed060373bff6d7d5c0a8cc55075d1c9e98e510
     * 公钥：
     * 3695167806a39c8b2f82964cf0fdd7f5ffa50a4db34c1f719bd44f5fd0d47bee782e320dcb6f5668786ec36f1ae93c33c31f9f4d0efc3bb6a11b1b492fd62a25
     * wallFile：
     * UTC--2023-06-12T09-20-09.460000000Z--9b2b364ed60d9ce42e2b66835f740e9680462771.json
     */
    private static void createWallet2() throws Exception {
        //生成12位助记词
        String mnemonic = MnemonicUtils.generateMnemonic(SecureRandom.getSeed(16));
        //使用助记词生成钱包种子
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        //-2147483604, -2147483648, -2147483648, 0, 0
        //-2147483648, -2147483648, -2147483648, 0, 0
        int [] derivationPath = { 44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, Bip32ECKeyPair.HARDENED_BIT, 0 , 0 };
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair keyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);
        // 生成 Keystore
        String walletFilePath="/Users/liuyong/workspace/project/personal/unmanned/src/destination";
        String walletFile = WalletUtils.generateWalletFile("createWallet2", keyPair, new File(walletFilePath), false);
        //通过公钥生成钱包地址
        String address = Keys.getAddress(keyPair.getPublicKey());
        System.out.println("助记词：");
        System.out.println(mnemonic);
        System.out.println("地址：");
        System.out.println("0x"+address);
        System.out.println("私钥：");
        System.out.println("0x"+keyPair.getPrivateKey().toString(16));
        System.out.println("公钥：");
        System.out.println(keyPair.getPublicKey().toString(16));
        System.out.println("wallFile：");
        System.out.println(walletFile);
    }

    /**
     * 创建钱包 password = createWallet3
     *
     * 助记词：
     * slot evoke bike spend soft explain decide orange start dawn couple fiber
     * 地址：
     * 0x575cd404edb322af8a13008423e2ac868a1250bb
     * 私钥：
     * 0xbf60f7354069c0aea35015a7a30c3e5a53a727ac6e7e976a393ac2c3c4acf532
     * 公钥：
     * 240aeae07658a6e65a2aff4e6c753a8ddcb66dffd25ec1a1ba372fe612082cc4076ef0a99be86a3927890bc5d382ade67569a595f9b81df9720a0803e16eb059
     * wallFile：
     * UTC--2023-06-12T09-20-09.578000000Z--575cd404edb322af8a13008423e2ac868a1250bb.json
     */
    private static void createWallet3() throws Exception {
        // 生成 Keystore
        String walletFilePath="/Users/liuyong/workspace/project/personal/unmanned/src/destination";
        Bip39Wallet bip39Wallet = Bip44WalletUtils.generateBip44Wallet("createWallet3", new File(walletFilePath));
        String mnemonic = bip39Wallet.getMnemonic();
        Credentials credentials = Bip44WalletUtils.loadBip44Credentials("", mnemonic);
        ECKeyPair keyPair = credentials.getEcKeyPair();
        System.out.println("助记词：");
        System.out.println(mnemonic);
        System.out.println("地址：");
        System.out.println(credentials.getAddress());
        System.out.println("私钥：");
        System.out.println("0x"+keyPair.getPrivateKey().toString(16));
        System.out.println("公钥：");
        System.out.println(keyPair.getPublicKey().toString(16));
        System.out.println("wallFile：");
        System.out.println(bip39Wallet.getFilename());
    }

    /**
     * 创建远程钱包
     * @throws Exception
     */
    private static void createWallet4() throws Exception {
        String password = "123456";
        String address = admin.personalNewAccount(password).send().getAccountId();
        System.out.println("address -> "+address);
        EthAccounts ethAccounts = admin.ethAccounts().send();
        List<String> accounts = ethAccounts.getAccounts();
        System.out.println("accounts -> " + accounts);
        for (String addr : accounts) {
            EthGetBalance ethGetBalance = admin.ethGetBalance(addr, DefaultBlockParameterName.LATEST).send();
            System.out.println(addr+" getBalance -> " + ethGetBalance.getBalance());
            System.out.println(addr+" getBalanceETHER -> " + Convert.fromWei(ethGetBalance.getBalance().toString(),Convert.Unit.ETHER));
        }
    }

    /**
     * 创建合约
     * @throws Exception
     */
    private static void createContract() throws Exception {

    }

    public static void main(String[] args) throws Exception {
//        EthAccounts ethAccounts = admin.ethAccounts().send();
//        EthGetBalance ethGetBalance = admin.ethGetBalance(ethAccounts.getAccounts().get(0), DefaultBlockParameterName.LATEST).send();
//        EthGasPrice ethGasPrice = admin.ethGasPrice().send();
//        Web3ClientVersion web3ClientVersion = admin.web3ClientVersion().send();
//        EthGetWork ethGetWork = admin.ethGetWork().send();
//        EthBlockNumber ethBlockNumber = admin.ethBlockNumber().send();
//        EthChainId ethChainId = admin.ethChainId().send();
//        EthCoinbase ethCoinbase = admin.ethCoinbase().send();
//        EthMining ethMining = admin.ethMining().send();
//        AdminDataDir adminDataDir = admin.adminDataDir().send();
//        AdminNodeInfo adminNodeInfo = admin.adminNodeInfo().send();
//        AdminPeers adminPeers = admin.adminPeers().send();
//        System.out.println("----------------------------------------------------------------------------");
//        System.out.println("getAccounts -> " + ethAccounts.getAccounts());
//        System.out.println("getBalance -> " + ethGetBalance.getBalance());
//        System.out.println("getBalanceETHER -> " + Convert.fromWei(ethGetBalance.getBalance().toString(),Convert.Unit.ETHER));
//        System.out.println("getGasPrice -> " + ethGasPrice.getGasPrice());
//        System.out.println("getWeb3ClientVersion -> " + web3ClientVersion.getWeb3ClientVersion());
//        System.out.println("getWork -> " + ethGetWork.getJsonrpc());
//        System.out.println("getBlockNumber -> " + ethBlockNumber.getBlockNumber());
//        System.out.println("getChainId -> " + ethChainId.getChainId());
//        System.out.println("getAddressCoinbase -> " + ethCoinbase.getAddress());
//        System.out.println("isMining -> " + ethMining.isMining());
//        System.out.println("----------------------------------------------------------------------------");
//        System.out.println("getDataDir -> " + adminDataDir.getDataDir());
//        System.out.println("adminNodeInfo -> " + adminNodeInfo.getJsonrpc());
//        System.out.println("adminPeers -> " + adminPeers.getJsonrpc());

        //System.out.println("getBlock -> "+web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("661")),true).send().getBlock());

        //System.out.println("getUncleBlock -> "+web3.ethGetUncleByBlockHashAndIndex("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347",new BigInteger("1")).send().getBlock());


//        System.out.println("------------------createWallet1------------------");
//        createWallet1();
//        System.out.println();
//        System.out.println("------------------createWallet2------------------");
//        createWallet2();
//        System.out.println();
//        System.out.println("------------------createWallet3------------------");
//        createWallet3();
        createWallet4();
    }
}
