package org.young.common.spi;

/**
 *  Spi缓存键
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 11:32
 */
public interface BaseSpiCacheKey {

    /**
     * 缓存键前缀
     */
    String PREFIX = "spi_";

    /**
     * URL前缀
     */
    String URL_PREFIX = "/provider";
}
