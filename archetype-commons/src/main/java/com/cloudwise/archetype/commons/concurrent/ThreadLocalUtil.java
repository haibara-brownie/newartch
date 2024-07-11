package com.cloudwise.archetype.commons.concurrent;

import com.cloudwise.archetype.commons.constant.LanguageConstants;
import com.cloudwise.concurrent.ThreadLocalContext;

/**
 * ThreadLocal工具类
 *
 * @author zhangmengyang
 */
public class ThreadLocalUtil {

    private static final String ACCOUNT_ID_NAME = "accountId";
    private static final String TOP_ACCOUNT_ID_NAME = "topAccountId";
    public static final String USERID_NAME = "userId";
    private static final String USER_NAME = "userName";
    private static final String CURRENT_USER_IP = "userIP";



    public static void remove() {
        ThreadLocalContext.remove();
    }

    public static void setAccountId(Long accountId) {
        ThreadLocalContext.setValue(ACCOUNT_ID_NAME, accountId);
    }

    public static Long getAccountId() {
        return ThreadLocalContext.defaultIfNull(ACCOUNT_ID_NAME, 110L);
    }

    public static void setTopAccountId(Long topAccountId) {
        ThreadLocalContext.setValue(TOP_ACCOUNT_ID_NAME, topAccountId);
    }

    public static Long getTopAccountId() {
        return ThreadLocalContext.defaultIfNull(TOP_ACCOUNT_ID_NAME, 110L);
    }

    public static void setUserId(Long userId) {
        ThreadLocalContext.setValue(USERID_NAME, userId);
    }

    public static Long getUserId() {
        return ThreadLocalContext.getValue(USERID_NAME);
    }

    public static Long getUserIdNonNull() {
        return ThreadLocalContext.defaultIfNull(USERID_NAME, 2L);
    }

    public static void setUserName(String userName) {
        ThreadLocalContext.setValue(USER_NAME, userName);
    }

    public static String getUserName() {
        return ThreadLocalContext.getValue(USER_NAME);
    }

    public static void setUserIp(String userIp) {
        ThreadLocalContext.setValue(CURRENT_USER_IP, userIp);
    }

    public static String getUserIp() {
        return ThreadLocalContext.getValue(CURRENT_USER_IP);
    }

    public static void setLanguage(String language) {
        ThreadLocalContext.setValue(LanguageConstants.LANGUAGE_KEY_NAME, language);
    }

    public static String getLanguage() {
        return ThreadLocalContext.defaultIfNull(LanguageConstants.LANGUAGE_KEY_NAME, LanguageConstants.CHINESE_ZH_CN);
    }

    public static boolean isChinese() {
        return LanguageConstants.CHINESE_ZH_CN.equalsIgnoreCase(ThreadLocalUtil.getLanguage());
    }

    public static boolean isEnglish() {
        return LanguageConstants.ENGLISH_EN_US.equalsIgnoreCase(ThreadLocalUtil.getLanguage());
    }

}
