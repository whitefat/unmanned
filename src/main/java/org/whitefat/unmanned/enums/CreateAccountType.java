package org.whitefat.unmanned.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author liuyong
 * @version 1.0
 * @Description: 创建账户类型
 * @Createdate 2021/11/27 1:44 下午
 */
@Getter
@AllArgsConstructor
public enum CreateAccountType {

    /**
     * 助记词
     */
    MNEMONIC(1, "助记词"),

    /**
     * 重建
     */
    REBUILD(2, "重建"),

    /**
     * 签名
     */
    SIGN(3, "签名"),

    ;

    private Integer code;

    private String description;

    public static CreateAccountType get(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        for (CreateAccountType e : CreateAccountType.values()) {
            if (code.compareTo(e.getCode()) == 0) {
                return e;
            }
        }
        return null;
    }

    static {
        // 通过名称构建缓存,通过EnumCache.findByName(StatusEnum.class,"SUCCESS",null);调用能获取枚举
        EnumCache.registerByName(CreateAccountType.class, CreateAccountType.values());
        // 通过code构建缓存,通过EnumCache.findByValue(StatusEnum.class,"S",null);调用能获取枚举
        EnumCache.registerByValue(CreateAccountType.class, CreateAccountType.values(), CreateAccountType::getCode);
    }

}
