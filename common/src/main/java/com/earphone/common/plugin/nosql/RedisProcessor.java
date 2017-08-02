package com.earphone.common.plugin.nosql;

import com.earphone.common.constant.ListOrder;
import com.earphone.common.utils.JSONExtend;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yaojiamin
 * @description TODO
 * @createTime 2016-6-8 上午10:39:17
 */
@Component
@Lazy(false)
public class RedisProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Resource
    private JedisPool jedisPool;

    private final static String OK = "OK";
    private final AtomicInteger activeCount = new AtomicInteger(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);
    private final AtomicLong totalFreeCount = new AtomicLong(0);

    private Jedis getDB() {
        activeCount.incrementAndGet();
        totalCount.incrementAndGet();
        return jedisPool.getResource();
    }

    private void free(Jedis jedis) {
        activeCount.decrementAndGet();
        totalCount.decrementAndGet();
        totalFreeCount.incrementAndGet();
        jedis.close();
    }

    /***********************************************************************/

    public boolean contains(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                return jedis.exists(key);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public String get(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                return jedis.get(key);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return null;
    }

    public boolean set(String key, String value) {
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
            Jedis jedis = getDB();
            try {
                return jedis.set(key, value).equals(OK);
            } catch (Exception ex) {
                logger.error("key=" + key + ",value=" + value, ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public boolean set(String key, String value, int time) {
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
            Jedis jedis = getDB();
            try {
                return jedis.setex(key, time, value).equals(OK);
            } catch (Exception ex) {
                logger.error("key=" + key + ",value=" + value + ",time=" + time, ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public boolean setObject(String key, Object object, int expire) {
        return Objects.nonNull(object) && set(key, JSONExtend.asJSON(object), expire);
    }

    public boolean setExpire(String key, int expire) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                try {
                    return jedis.expire(key, expire) > 0;
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public boolean remove(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                jedis.del(key);
                return true;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    /***********************************************************************/

    public boolean addToSortedSet(String key, String value, Long score) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            Jedis jedis = getDB();
            try {
                jedis.zadd(key, score, value);
                return true;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public boolean addToSortedSet(String key, String content) {
        return addToSortedSet(key, content, new Date().getTime());
    }

    public boolean removeFromSortedSet(String key, String... content) {
        if (StringUtils.isNotBlank(key) && Objects.nonNull(content) && content.length > 0) {
            Jedis jedis = getDB();
            try {
                jedis.zrem(key, content);
                return true;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public Set<String> getSortedSetByRange(String key, int start, int end, ListOrder order) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                switch (order) {
                    case ASC:
                        return jedis.zrange(key, start, end);
                    case DESC:
                        return jedis.zrevrange(key, start, end);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return Collections.emptySet();
    }

    public Set<String> getSortedSetByScore(String key, double start, double end, ListOrder order) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                switch (order) {
                    case ASC:
                        return jedis.zrangeByScore(key, start, end);
                    case DESC:
                        return jedis.zrevrangeByScore(key, start, end);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return Collections.emptySet();
    }

    /***********************************************************************/

    public Long increaseKey(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                return jedis.incr(key);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return null;
    }

    public Long increaseKeyByStep(String key, long step) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                return jedis.incrBy(key, step);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return null;
    }

    /***********************************************************************/

    private static final Integer ATOM_LOCK_EXPIRED = 2 * 1000;
    private static final Integer ATOM_LOCK_TRIES = 20;

    public boolean lockKey(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                String lockKey = key + ".lock";
                for (int period = ATOM_LOCK_EXPIRED / ATOM_LOCK_TRIES, total = 0; total <= ATOM_LOCK_EXPIRED; total += period) {
                    if (("OK").equals(jedis.set(lockKey, String.valueOf(System.currentTimeMillis()), "NX", "PX", ATOM_LOCK_EXPIRED))) {
                        return true;
                    }
                    TimeUnit.MICROSECONDS.sleep(period);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public boolean lockKeyNoQueue(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                return jedis.set(key + ".lock", String.valueOf(System.currentTimeMillis()), "NX", "PX", ATOM_LOCK_EXPIRED).equals("OK");
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public boolean unLockKey(String key) {
        return remove(key + ".lock");
    }

    /***********************************************************************/

    public boolean pushStringToList(String key, String... value) {
        if (StringUtils.isNotEmpty(key) && Objects.nonNull(value) && value.length > 0) {
            Jedis jedis = getDB();
            try {
                return jedis.lpush(key, value) > 0;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return false;
    }

    public List<String> getListByRange(String key, long start, long end) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                return jedis.lrange(key, start, end);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return Collections.emptyList();
    }

    public long getListSize(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Jedis jedis = getDB();
            try {
                return jedis.llen(key);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                free(jedis);
            }
        }
        return -1L;
    }
}
