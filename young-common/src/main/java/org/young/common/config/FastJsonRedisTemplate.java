package org.young.common.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.young.common.protocol.Constants;

/**
 * Redis 使用fast json存储数据模板
 * @author jeasonyoung
 */
public class FastJsonRedisTemplate extends RedisTemplate<Object, Object> {

    /**
     * 构造函数。
     */
    public FastJsonRedisTemplate(){
        final RedisKeySerializer keySerializer = new RedisKeySerializer();
        final GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        //
        setDefaultSerializer(fastJsonRedisSerializer);
        //
        setKeySerializer(keySerializer);
        setValueSerializer(fastJsonRedisSerializer);
        //
        setHashKeySerializer(keySerializer);
        setHashValueSerializer(fastJsonRedisSerializer);
    }


    /**
     * redis键序列化
     */
    public static class RedisKeySerializer implements RedisSerializer<Object> {
        private final String target = "\"", replacement = "";

        @Override
        public byte[] serialize(final Object o) throws SerializationException {
            if(o == null){
                return new byte[0];
            }
            if(o instanceof String){
                return ((String)o).getBytes(Constants.CHARSET);
            }
            if(o instanceof Number){
                return o.toString().getBytes(Constants.CHARSET);
            }
            String json = JSON.toJSONString(o);
            json = json.replace(target, replacement);
            return json.getBytes(Constants.CHARSET);
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            return (bytes == null ? null : new String(bytes, Constants.CHARSET));
        }
    }
}
