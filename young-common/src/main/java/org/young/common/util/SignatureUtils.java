package org.young.common.util;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.young.common.protocol.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 参数签名工具.
 * @author jeasonyoung
 */
@Slf4j
public class SignatureUtils {
    private static final String SIGN_PARAM_NAME = Constants.REQ_HEAD_BY_SIGN_KEY;

    /**
     * 创建参数签名。
     * @param parameters
     * 参数集合。
     * @param secret
     * 签名盐值。
     * @return 签名值。
     */
    public static String createSignature(@Nonnull final Map<String, Object> parameters,@Nullable final String secret){
        log.debug("createSignature(parameters: {}, secret: {})", Joiner.on(",").withKeyValueSeparator("->").join(parameters), secret);
        Assert.notEmpty(parameters, "'parameters'不能为空!");
        //初始化参数集合
        final List<String> params = new ArrayList<>();
        //参数处理
        createSignatureParameters(params, parameters);
        //检查参数集合
        Assert.notEmpty(params, "未设置渠道号");
        //参数排序
        log.debug("排序前字符串: {}", Joiner.on(",").join(params));
        Collections.sort(params);
        log.debug("排序后字符串: {}", Joiner.on(",").join(params));
        //字符串拼接
        final String source = Joiner.on("&").join(params) + (Strings.isNullOrEmpty(secret) ? "" : secret);
        log.debug("拼接后的字符串: {}", source);
        //sha256签名
        return EncryptUtils.createSignatureEncrypt(source);
    }

    /**
     * 创建签名参数.
     * @param outParams
     * 输出参数。
     * @param parameters
     * 输入参数。
     */
    @SuppressWarnings("unchecked")
    private static void createSignatureParameters(@Nonnull final List<String> outParams,@Nonnull final Map<String, Object> parameters){
        if(parameters.size() == 0){
            return;
        }
        //添加参数
        for(Map.Entry<String, Object> entry : parameters.entrySet()){
            //剔除含有令牌名称的参数
            if(entry.getKey().equalsIgnoreCase(SIGN_PARAM_NAME)){
                continue;
            }
            //参数值
            final Object value = entry.getValue();
            //剔除参数值为NULL的参数
            if(value == null){
                continue;
            }
            //剔除参数值为bool且值为false的参数
            if((value instanceof Boolean) && !((Boolean) value)){
                continue;
            }
            //剔除参数值为Number且值为0的参数
            if((value instanceof Number) && ((Number)value).floatValue() == 0){
                continue;
            }
            //数组类型
            if(value instanceof Collection) {
                outParams.add(entry.getKey() + "=" + Joiner.on(",").join((Collection)value));
                continue;
            }
            if(value instanceof Map){
                createSignatureParameters(outParams, (Map)value);
                continue;
            }
            String strVal = value.toString();
            if(!Strings.isNullOrEmpty(strVal)){
                outParams.add(entry.getKey() + "=" + strVal);
            }
        }
    }
}
