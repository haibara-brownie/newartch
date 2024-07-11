package com.cloudwise.archetype.commons.filter;

import com.cloudwise.archetype.commons.concurrent.ThreadLocalUtil;
import com.cloudwise.archetype.commons.constant.LanguageConstants;
import com.cloudwise.archetype.commons.constant.RequestHeaderConstant;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LocaleHttpFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain chain) throws IOException, ServletException {
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
        try {
            // 获取header中的ACCOUNT_ID和USER_ID进行校验
            String accountId = request.getHeader(RequestHeaderConstant.CURRENT_ACCOUNT_ID);
            String userId = request.getHeader(RequestHeaderConstant.CURRENT_USERID);
            // 国际化
            final String language = request.getHeader(LanguageConstants.LANGUAGE_KEY_NAME);
            ThreadLocalUtil.setLanguage(language);
            // 执行正常业务逻辑
            chain.doFilter(requestWrapper, response);
        } finally {
            ThreadLocalUtil.remove();
        }
    }

}