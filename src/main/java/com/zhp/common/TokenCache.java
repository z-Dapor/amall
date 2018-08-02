/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: TokenCache
 * Author:   臧浩鹏
 * Date:     2018/7/24 10:18
 * Description: 缓存
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br> 
 * 〈缓存〉
 *
 * @author 臧浩鹏
 * @create 2018/7/24
 * @since 1.0.0
 */
@Slf4j
/**
 *
 * @Description: deprecated 此类功能已被Redis所替代！
 *
 * @auther: 臧浩鹏
 * @date: 8:36 2018/7/31
 * @param:
 * @return:
 *
 */
public class TokenCache {
        public static final String TOKEN = "token_";

    /**
     *
     * @Description: LRU算法de cache
     *
     * @auther: 臧浩鹏
     * @date: 10:22 2018/7/24
     * @param:
     * @return:
     *
     */
        private static LoadingCache<String,String> cache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return "null";
                    }
                });
        public static void set(String key,String value){
            cache.put(key,value);
        }

        public static String get(String key){
            String value = null;
            try {
                value = cache.get(key);
                if (value.equals("null")){
                    return null;
                }
                return value;
            } catch (ExecutionException e) {
                log.error("localcache get error",e);
            }
            return null;
        }

}
