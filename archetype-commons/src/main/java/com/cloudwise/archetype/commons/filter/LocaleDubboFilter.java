package com.cloudwise.archetype.commons.filter;

import com.cloudwise.archetype.commons.concurrent.ThreadLocalUtil;
import com.cloudwise.archetype.commons.constant.LanguageConstants;
import com.cloudwise.concurrent.ThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcServiceContext;

import java.util.Objects;

/**
 * dubbo国际化拦截器
 *
 * @author jiayongming
 */
@Slf4j
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = -999)
public class LocaleDubboFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 是否是服务端
        boolean provider = false;
        try {
            RpcServiceContext context = RpcContext.getServiceContext();
            provider = Objects.isNull(context.getUrl()) || context.isProviderSide();

            if (provider) {
                String language = context.getAttachment(LanguageConstants.LANGUAGE_KEY_NAME);
                //服务端暂存需要传递的值(放入本地ThreadLocal)
                ThreadLocalUtil.setLanguage(language);
            } else {
                // 调用端设置需要传递的值
                if (StringUtils.isBlank(context.getAttachment(LanguageConstants.LANGUAGE_KEY_NAME))) {
                    context.setAttachment(LanguageConstants.LANGUAGE_KEY_NAME, ThreadLocalUtil.getLanguage());
                }
            }

            return invoker.invoke(invocation);
        } finally {
            if (provider) {
                // 服务端调用结束清空ThreadLocal
                ThreadLocalContext.remove();
            }
        }
    }

}