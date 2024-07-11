package com.cloudwise.archetype.commons.decrypt;

import cn.hutool.extra.spring.SpringUtil;
import com.cloudwise.cwop.security.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

@Slf4j
public class DecryptUtils {

    protected static final String DECRYPT_KEY = "passwdSecretKey";
    private static final String NACOS_PASSWORD_KEY = "cwNacosPassword";

    /**
     * 解密 如果是密文则解密，不是密文则不解密
     *
     * @param cipherText
     * @return
     */
    public static String decrypt(String cipherText) {
        if (StringUtils.isBlank(cipherText)) {
            return StringUtils.EMPTY;
        }

        String publicKey = StringUtils.EMPTY;
        try {
            Environment environment = SpringUtil.getApplicationContext().getEnvironment();
            publicKey = StringUtils.defaultIfBlank(environment.getProperty(DECRYPT_KEY), StringUtils.EMPTY);
        } catch (Exception ignored) {
        }

        return decrypt(cipherText, publicKey);
    }

    public static String decrypt(String cipherText, String publicKey) {
        publicKey = StringUtils.defaultIfBlank(publicKey, StringUtils.EMPTY);
        try {
            if (cipherText.length() >= 32) {
                String result = RSAUtils.decrypt(publicKey, cipherText);
                log.debug("解密成功,publicKey={},cipherText={},result={}", publicKey, cipherText, result);
                return result;
            }

        } catch (Exception e) {
            log.error("解密失败,publicKey={},cipherText={}", publicKey, cipherText, e);
        }
        return cipherText;
    }

    /**
     * 首次连接Nacos的密码使用默认秘钥解密，其他密码使用commons中的秘钥解密。
     */
    public static void decryptNacosPassword() {
        if (StringUtils.isNotEmpty(System.getProperty(NACOS_PASSWORD_KEY, StringUtils.EMPTY))) {
            try {
                String nacosPassword = RSAUtils.decrypt(System.getProperty(NACOS_PASSWORD_KEY));
                System.setProperty(NACOS_PASSWORD_KEY, nacosPassword);
            } catch (Exception e) {
                log.error("Nacos password decryption failed, try to use the given parameters. {}", e.getMessage(), e);
            }
        } else {
            log.debug("Skip Nacos password decryption, no parameters are passed in {}.", NACOS_PASSWORD_KEY);
        }
    }

}
