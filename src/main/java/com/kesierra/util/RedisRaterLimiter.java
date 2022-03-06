package com.kesierra.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisRaterLimiter {
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 令牌的
     *
     * @param key        key值
     * @param limitCount 容量
     * @param seconds    时间间隔
     * @return
     */
    public boolean tryAccess(String key, int limitCount, int seconds) {
        String luaScript = buildLuaScript();
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        List<String> keys = new ArrayList<>();
        keys.add(key);
        Long count = stringRedisTemplate.execute(redisScript, keys, String.valueOf(limitCount), String.valueOf(seconds));
        if (count != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 脚本
     *
     * @return
     */
    private static final String buildLuaScript() {
        StringBuilder lua = new StringBuilder();
        lua.append(" local key = KEYS[1]");
        lua.append("\nlocal limit = tonumber(ARGV[1])");
        lua.append("\nlocal curentLimit = tonumber(redis.call('get', key) or \"0\")");
        lua.append("\nif curentLimit + 1 > limit then");
        lua.append("\nreturn 0");
        lua.append("\nelse");
        lua.append("\n redis.call(\"INCRBY\", key, 1)");
        lua.append("\nredis.call(\"EXPIRE\", key, ARGV[2])");
        lua.append("\nreturn curentLimit + 1");
        lua.append("\nend");
        return lua.toString();
    }
}
