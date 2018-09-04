package org.young.common.protocol;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Nonnull;

/**
 * 报文-编解码器
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/8/27 15:02
 */
@Slf4j
public class CodecUtils {

    /**
     * 是否为base64编码
     * @param data
     * 编码数据
     * @return 是否为base64编码
     */
    private static boolean isBase64Encode(@Nonnull final String data){
        if(!Strings.isNullOrEmpty(data)){
            return Base64.isBase64(data);
        }
        return false;
    }

    /**
     * 报文解码
     * @param source
     * 源报文
     * @return 解码后报文
     */
    public static String decode(@Nonnull final String source){
        log.debug("decode(source: {})....", source);
        if(!Strings.isNullOrEmpty(source) && isBase64Encode(source)){
            //解码
            final byte[] data = Base64.decodeBase64(source);
            if(data != null && data.length > 0){
                return new String(data, Constants.CHARSET);
            }
        }
        return source;
    }

}
