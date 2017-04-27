package com.nuoshenggufen.e_treasure.main.model.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nuoshenggufen.e_treasure.core.page.Page;
import com.nuoshenggufen.e_treasure.support.ListProp;
import com.nuoshenggufen.e_treasure.support.O;
import com.nuoshenggufen.e_treasure.support.util.CommonUtils;

/**
 * 保存数据到主存数据库做缓存
 * 
 * @类名称：storeService.java
 * @文件路径：com.ewppay.ewp.app.ptrs.EwpAppPtrs.mainstore
 * @author：email: <a href="bentengwu@163.com"> thender </a>
 * @Date 2014-11-3 下午4:24:48
 */
@Component
@Lazy(false)
public class StoreService {
	private final static String OK = "OK";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private long activeCount = 0L;
	private long totalCount = 0L;
	private long totalFreeCount = 0L;
	
	@Autowired
	private JedisPool jedisPool;
	
	/**
	 * 
	* @Title: setAdd
	* @Description: 向名称为key的set中添加元素value
	* @param @param key
	* @param @param value
	* @param @return    
	* @return Long    返回类型
	* @throws
	 */
	public Long setAdd(String key, String value) {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
			return null;
		}
		Jedis jedis = getJedis();
		try {
			return jedis.sadd(key, value);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	* @Title: sismember
	* @Description: 测试value是否是名称为key的set的元素
	* @param @param key
	* @param @param value
	* @param @return    
	* @return boolean    返回类型
	* @throws
	 */
	public boolean sismember(String key, String value) {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
			return false;
		}
		Jedis jedis = getJedis();
		try {
			return jedis.sismember(key, value);
		} catch (Exception ex) {
			return false;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	* @Title: smembers
	* @Description: 返回名称为key的set的所有元素
	* @param @param key
	* @param @return    
	* @return Set<String>    返回类型
	* @throws
	 */
	public Set<String> smembers(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		try {
			return jedis.smembers(key);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	* @Title: setPop
	* @Description: 随机返回并删除名称为key的set中一个元素
	* @param @param key
	* @param @return    
	* @return String    返回类型
	* @throws
	 */
	public String setPop(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		try {
			return jedis.spop(key);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	* @Title: setPop
	* @Description: 删除名称为key的set中的元素数组values
	* @param @param key
	* @param @param values
	* @param @return    
	* @return Long    返回类型
	* @throws
	 */
	public Long setRem(String key,String... values) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		try {
			return jedis.srem(key, values);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	
	/**
	 * 程序计数器，递增 <br />
	 * 
	 * @date 2015-5-25 下午7:49:38
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 *            需要被递增的key
	 * @return 递增后的数值 如果key的值为null,则返回null 如果异常,返回null.
	 * @since 2.0.2
	 */
	public Long incrKey(String key) {
		if (key == null)
			return null;
		Jedis jedis = getJedis();
		try {
			return jedis.incr(key);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 计数器，递增 <br />
	 *
	 * @date 2015-9-3 下午7:49:38
	 * @author zhaogl
	 * @param key 需要被递增的key
	 * @param value 需要增的值，为负数时为减
	 * @return 递增减后的数值.
	 * @since 2.0.2
	 */
	public Long incrbyKey(String key,long step) {
		if (key == null)
			return null;
		Jedis jedis = getJedis();
		try {
			return jedis.incrBy(key,step);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 对多个key进行递增操作. <br />
	 * 
	 * @date 2015-6-17 下午6:00:30
	 * @author <a href="xwh@ewppay.com">伟宏</a>
	 * @param keys
	 * @return
	 * @since 1.0
	 */
	public Long[] incrKey(String... keys) {
		if (keys == null)
			return null;
		Jedis jedis = getJedis();
		try {
			Long[] res = new Long[keys.length];
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				if (StringUtils.isNotBlank(key)) {
					res[i] = jedis.incr(key);
				}
			}
			return res;
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 程序计数器，递减 <br />
	 * 
	 * @date 2015-5-25 下午7:49:38
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 *            需要被递减的key
	 * @return 递减后的数值 如果key的值为null,则返回null 如果异常,返回null.
	 * @since 2.0.2
	 */
	public Long decrKey(String key) {
		if (key == null)
			return null;
		Jedis jedis = getJedis();
		try {
			Long times = jedis.decr(key);
			if (times < 0) {
				rmKey(key);
				return -1L;
			}
			return times;
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	* @Title: decrbyKey
	* @Description: 名称为key的string减少num
	* @param @param key
	* @param @param num
	* @param @return    
	* @return Long    返回类型
	* @throws
	 */
	public Long decrbyKey(String key,Long num) {
		if (key == null)
			return null;
		Jedis jedis = getJedis();
		try {
			Long times = jedis.decrBy(key, num);
			if (times < 0) {
				rmKey(key);
				return -1L;
			}
			return times;
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 判断是否包含该键 <br />
	 * 
	 * @date 2014-11-6 下午4:23:32
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param key
	 *            键
	 * @return true 包含 false 不包含
	 * @since 3.0
	 */
	public boolean contain(String key) {
		if (key == null)
			return false;
		Jedis jedis = getJedis();
		try {
			return jedis.exists(key);
		} catch (Exception ex) {
			return false;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 从列表中读取字符串列表 <br />
	 * 
	 * @date 2015-5-26 下午6:04:05
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 *            列表key
	 * @param start
	 *            开始下标
	 * @param end
	 *            结束下标
	 * @return 返回对应的列表
	 * @since 2.0.2
	 */
	public List<String> getStringListFromList(String key, long start, long end) {
		if (key == null || StringUtils.isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		try {
			/* 获取索引id的列表信息 */
			List<String> indexList = jedis.lrange(key, start, end);
			return indexList;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 通过下标获取列表的数据 <br />
	 * 
	 * @date 2015-5-22 下午10:42:12
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 *            mainkey
	 * @param index
	 *            下标
	 * @return
	 * @since 2.0.2
	 */
	public String getStringFromList(String key, long index) {
		if (key == null) {
			return null;
		}
		Jedis jedis = getJedis();
		if (jedis == null) {
			return null;
		}
		try {
			return jedis.lindex(key, index);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	 * SETNX works exactly like SET with the only difference that if the key already exists no operation is performed. SETNX actually means "SET if Not eXists". 

Time complexity: O(1)

	 *<br />
			*@date 2015-8-28 下午2:04:29
			*@author <a href="xwh@ewppay.com">thender</a>
			*@param key
	*@param value
	*@return
			*@since 1.0
			*/
	public Long setnx(String key,String value)
	{
		Jedis jedis = getJedis();
		try {
			jedis.set(key,value);
			return jedis.setnx(key, value);
		} finally {
			free(jedis);
		}
	}

	/**
	 * 设置一个值带超时时间的key到redis。 如果已经存在则返回失败 0L.
	 *<br />
	 *@date 2015-8-28 下午2:08:45
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param key
	 *@param value	
	 *@param expire 单位S.
	 *@return	1L成功  0L 失败
	 *@since 1.0
	 */
	public Long setnx(String key,String value,int expire)
	{
		Jedis jedis = getJedis();
		try {
			Long lg = jedis.setnx(key, value);
			if(lg.equals(1L))
			{
				lg	=	jedis.expire(key, expire);
			}
			return lg;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 设置一个值带超时时间的key到redis。 如果已经存在则返回失败 false.
	 *<br />
	 *@date 2015-9-3 下午2:08:45
	 *@author zhaogl
	 *@param key
	 *@param value
	 *@param expire 单位S.
	 *@return	true成功  false 失败
	 *@since 2.6.12
	 */
	public boolean set(String key,String value,int expire)
	{
		Jedis jedis = getJedis();
		try {
			return jedis.set(key,value,"NX","EX",expire).equals("OK");
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
			return false;
		} finally {
			free(jedis);
		}
	}


	/**
	 * 根据Key值进行加锁,单例时使用 <br />
	 * 
	 * @date 2015-7-31
	 * @author zhaogl
	 * @param key
	 * @return boolean true为锁成功，false为锁失败
	 * @since 2.6.0
	 */
	public boolean lockKey(String key) {
		if (key == null) {
			return false;
		}
		try {
			String lockKey = key + ".lock";
			boolean bret = set(lockKey,"1", O.ATOM_LOCK_EXPIRED);
			int i=0;
			while(i<20 && !bret)
			{
				TimeUnit.MICROSECONDS.sleep(100);
				bret = set(lockKey, "1",O.ATOM_LOCK_EXPIRED);
				i++;
			}
			return bret;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		} 
	}
	
	/**
	 * 对Key值解锁，配合加锁时使用,单例时使用 <br />
	 * 
	 * @date 2015-7-31
	 * @author zhaogl
	 * @param key
	 * @return boolean true为锁成功，false为锁失败
	 * @since 2.6.0
	 */
	public boolean unLockKey(String key) {
		if (key == null) {
			return false;
		}
		Jedis jedis = getJedis();
		if (jedis == null) {
			return false;
		}
		
		try {
			jedis.del(key+".lock");

			return true;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 获取列表的长度 <br />
	 * 
	 * @date 2015-5-22 下午9:49:48
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 * @return
	 * @since 2.0.2
	 */
	public Long getListLen(String key) {
		if (key == null) {
			return null;
		}
		Jedis jedis = getJedis();
		if (jedis == null) {
			return null;
		}
		try {
			return jedis.llen(key);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		} finally {
			free(jedis);
		}

	}

	/**
	 * 在指定Key所关联的List Value的头部插入参数中给出的所有Values。
	 * 如果该Key不存在，该命令将在插入之前创建一个与该Key关联的空链表，之后再将数据从链表的头部插入。
	 * 如果该键的Value不是链表类型，该命令将返回相关的错误信息。 <br />
	 * 
	 * @date 2015-5-19 下午3:40:10
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 *            对应的数据库中的key.
	 * @param t
	 *            对象实体，可以是 JSONObject Map 也可以是JAVA bean.
	 * @return
	 * @since 2.0.2
	 */
	public boolean saveBeanToList(String key, Object t) {
		if (t == null || key == null)
			return false;
		try {
			ObjectMapper m = new ObjectMapper();
			String json = m.writeValueAsString(t);
			return pushStringToList(key, json);
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 将值push到list中。 <br />
	 * 
	 * @date 2015-5-26 下午5:53:50
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 * @param value
	 * @return
	 * @since 2.0.2
	 */
	public boolean pushStringToList(String key, String value) {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
			return false;
		}
		Jedis jedis = getJedis();
		try {
			jedis.lpush(key, value);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 将值push到list中。value为数组 <br />
	 * 
	 * @date 2015-7-31
	 * @author zhaogl
	 * @param key
	 * @param value[]
	 * @return
	 * @since 2.6.0
	 */
	public boolean pushStringToList(String key, String ... value) {
		if (StringUtils.isBlank(key) || value.length == 0) {
			return false;
		}
		Jedis jedis = getJedis();
		try {
			Long lengthBefore = getListLen(key);
			jedis.lpush(key, value);
			Long lengthAfter = getListLen(key);
			if(lengthAfter.equals(lengthBefore)){
				return false;
			}
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 限制队列的长度 <br />
	 * 
	 * @date 2015-6-1 下午6:50:03
	 * @author <a href="xwh@ewppay.com">伟宏</a>
	 * @param key
	 *            对限制的mainkey
	 * @param limit
	 *            长度
	 * @since 1.0
	 */
	public void ltrim(String key, long limit) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		Jedis jedis = getJedis();
		try {
			long len = jedis.llen(key);
			if (limit < len)
				jedis.ltrim(key, 0, limit);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			free(jedis);
		}
	}

	/**
	 * 删除0-num之间的数据 <br />
	 * 
	 * @date 2015-7-31
	 * @author zhaogl
	 * @param key
	 * @param num 删除至哪个下标
	 * @since 2.6.0
	 */
	public void ltrim(String key, int num) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		Jedis jedis = getJedis();
		try {
			jedis.ltrim(key, num, -1);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 用新的list替换原先的list.
	 * 
	 * <br />
	 * 
	 * @date 2015-5-29 下午1:45:46
	 * @author <a href="xwh@ewppay.com">伟宏</a>
	 * @param key
	 *            需要被替换的key.
	 * @param list
	 *            要求传入的list长度要和原先的 一样。
	 * @since 1.0
	 */
	public boolean coverList(String key, List list) {
		if (key == null || list == null)
			return false;
		Jedis jedis = getJedis();
		try {
			if (jedis.llen(key) == list.size()) {
				for (Object t : list) {
					ObjectMapper m = new ObjectMapper();
					String json = m.writeValueAsString(t);
					jedis.lpush(key, json);
				}
				jedis.ltrim(key, 0, list.size() - 1);
			}
			return true;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			free(jedis);
		}
		return false;
	}
    
    /**
     * @description 添加到可排序的Set集合
     * @createTime 2015-11-23 下午4:49:27
     * @fileName StoreService.java
     * @author yaojiamin
     */
    public boolean addToSortedSet ( String key , String content ) {
        Boolean result = false;
        if (StringUtils.isNotBlank ( key ) && StringUtils.isNotBlank ( content )) {
            Jedis jedis = getJedis ( );
            try {
                jedis.zadd ( key , new Date ( ).getTime ( ) , content );
                result = true;
            } catch (Exception ex) {
                logger.error ( ex.getMessage ( ) , ex );
            } finally {
                free ( jedis );
            }
        }
        return result;
    }

    /**
     * @description 按score大小顺序获取集合元素，使用order参数选择顺序，1为升序，0为降序
     * @createTime 2015-11-23 下午5:26:12
     * @fileName StoreService.java
     * @author yaojiamin
     */
    public List<String> getSortedSet ( String key ,int order, int start , int end ) {
        List<String> set = new ArrayList<String> ( );
        Jedis jedis = getJedis ( );
        try {
            if (order == 1) {
                set.addAll ( jedis.zrange ( key , start , end ) );
            } else {
                set.addAll ( jedis.zrevrange ( key , start , end ) );
            }
        } catch (Exception ex) {
            logger.error ( ex.getMessage ( ) , ex );
        } finally {
            free ( jedis );
        }
        return set;
    }

	/**
	 * 保存字符串到list中。 <br />
	 * 
	 * @date 2015-5-22 下午5:55:26
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 * @param t
	 * @return
	 * @since 2.0.2
	 */
	public boolean saveString2List(String key, String t) {
		return pushStringToList(key, t);
	}

	/**
	 * 批量保存，不考虑回滚 <br />
	 * 
	 * @date 2015-5-19 下午3:42:14
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 *            业务key ,对应数据库中的key
	 * @param list
	 *            数据列表 可以是 JSONObject Map 也可以是JAVA bean.
	 * @return
	 * @since 2.0.2
	 */
	public boolean saveBeansToList(String key, List list) {

		if (key == null || list == null)
			return false;
		Jedis jedis = getJedis();
		try {
			ObjectMapper m = new ObjectMapper();
			for (Object t : list) {
				String json = m.writeValueAsString(t);
				jedis.lpush(key, json);
			}
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 保存字符串到主存 <br />
	 * 
	 * @date 2014-11-3 下午4:40:55
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param key
	 * @param value
	 */
	public boolean save(String key, String value) {
		if (value == null)
			return false;
		Jedis jedis = getJedis();
		try {
			return jedis.set(key, value).equals(OK);
		} catch (Exception ex) {
			logger.debug("保存数据失败[" + key + ":" + value + "]", ex);
			return false;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 
	 * @Title: saveByTime
	 * @Description: setex(key, time, value)
	 *               向库中添加string（名称为key，值为value）同时，设定过期时间time
	 * @param: @param key
	 * @param: @param value
	 * @param: @param time
	 * @param: @return
	 * @return: boolean
	 * @throws
	 * @author cyb
	 * @Date 2015-7-29 下午1:42:34
	 */
	public boolean saveByTime(String key, String value, int time) {
		if (value == null)
			return false;
		Jedis jedis = getJedis();
		try {
			return jedis.setex(key, time, value).equals(OK);
		} catch (Exception ex) {
			logger.debug("保存数据失败[" + key + ":" + time + ":" + value + "]", ex);
			return false;
		} finally {
			free(jedis);
		}
	}

	/**
	 * appptrs 待实现 保存BEAN到主存。 <br />
	 * 
	 * @date 2014-11-3 下午4:37:54
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param key
	 * @param t
	 *            必须是可序列化的对象
	 */
	public boolean saveBean(String key, Object t) {
		if (t == null)
			return false;
		try {
			ObjectMapper m = new ObjectMapper();
			String json = m.writeValueAsString(t);
			return save(key, json);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 待实现 读取bean <br />
	 * 
	 * @date 2014-11-3 下午4:40:09
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param key
	 * @return
	 */
	public <T> T getBean(String key, Class<T> cls) {
		try {
			String json = getStringVal(key);
			if (json != null) {
				ObjectMapper m = new ObjectMapper();
				return m.readValue(json, cls);
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
		return null;
	}

	/**
	 * appptrs 保存数组到主存。 <br />
	 * 
	 * @date 2014-11-3 下午4:37:54
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param key
	 * @param t
	 *            必须是可序列化的对象
	 */
	public boolean saveArray(String key, String[] arr) {
		if (arr == null)
			return false;
		return save(key, Arrays.toString(arr));
	}

	/**
	 * 获取字符串值 <br />
	 * 
	 * @date 2014-11-3 下午8:17:43
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param strKey
	 *            字符串值
	 * @return 返回获取的字符串值
	 * @since 3.0
	 */
	public String getStringVal(String strKey) {
		if (strKey == null)
			return null;
		Jedis jedis = getJedis();
		try {
			return jedis.get(strKey);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	
	/**
	 * 读取一个或者多个key的值
	 *<br />
	 *@date 2015-8-8 上午10:51:12
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param strKeys	一个或者多个值.
	 *@return	对应的值列表
	 *@since 1.0
	 */
	public List<String> getStringVal(String...strKeys)
	{
		Jedis jedis = getJedis();
		try {
			return jedis.mget(strKeys);
		} catch (Exception e) {
			throw new RuntimeException("redis : Error to get keys "+ ArrayUtils.toString(strKeys));
		}finally{
			free(jedis);
		}
	}

	/**
	 * 
	 * <br />
	 * 
	 * @date 2015-5-22 下午4:37:52
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param redisKey
	 *            对应的redis的key.他的VALUE是个MAP
	 * @param mapKey
	 *            需要存的map的key值
	 * @param val
	 *            需要存的map的val值
	 * @return true 存入成功 false 存入失败
	 * @since 2.0.2
	 */
	public boolean putToMap(String redisKey, String mapKey, Object val) {
		if (redisKey == null || mapKey == null || val == null)
			return false;
		Jedis jedis = getJedis();
		try {
			ObjectMapper m = new ObjectMapper();
			String json = m.writeValueAsString(val);
			if(val instanceof String){
				json = val.toString();
			}
			Long pst = jedis.hset(redisKey, mapKey, json);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			free(jedis);
		}

	}

	// public void setExpireMapKey(String redisKey,String mapKey)
	// {
	// if (redisKey == null || mapKey == null )
	// return ;
	// Jedis jedis = getJedis();
	// try {
	// jedis.expireAt(key, unixTime)
	// return ;
	// } catch (Exception ex) {
	// logger.error(ex.getMessage(),ex);
	// } finally {
	// free(jedis);
	// }
	// }

	/**
	 * 设置超时时间 <br />
	 * 
	 * @date 2015-5-29 下午7:00:42
	 * @author <a href="xwh@ewppay.com">伟宏</a>
	 * @param key
	 *            mainkey
	 * @param seconds
	 *            时间。
	 * @since 1.0
	 */
	public void setExpireWithPrefix(String key, int seconds) {
		Jedis jedis = getJedis();
		try {
			jedis.expire(key, seconds);
		} finally {
			free(jedis);
		}
	}

	/**
	 * 获取key的失效时间，单位秒。
	 *<br />
	 *@date 2015-11-19 下午4:22:12
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param key 
	 *@return -1 the key is not exists or no ttl
	 *@since 1.0
	 */
	public Long ttl(String key)
	{
		Jedis jedis = getJedis();
		try {
			return jedis.ttl(key);
		} finally {
			free(jedis);
		}
	}
	
	
	/**
	 * 读取MAP中的值。 <br />
	 * 
	 * @date 2014-11-4 下午5:03:13
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param key
	 *            MAP实例对应的KEY。
	 * @param mapKey
	 *            MAP中的KEY。
	 * @return 对应的值。 如果获取失败或者没有该值则返回NULL
	 * @since 3.0
	 */
	public String getMapVal(String key, String mapKey) {
		if (key == null)
			return null;
		try {
			List<String> args = getMapVals(key, mapKey);
			if (args != null && args.size() > 0) {
				return args.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 校验不存在的hashKey,并对不存在的hashKey进行返回. 如果全部存在,则返回size为0的ArrayList. <br />
	 * 
	 * @date 2015-6-24 下午3:45:18
	 * @author <a href="xwh@ewppay.com">伟宏</a>
	 * @param key
	 *            mainkey
	 * @param mapKeys
	 *            hashkey
	 * @return 不存在的hashkey 如果全部都存在 则List中不存在元素.size=0;
	 * @since 1.0
	 */
	public List<String> validateMapKeys(String key, String... mapKeys) {
		List<String> list = new ArrayList<>(mapKeys.length);
		Jedis jedis = getJedis();
		try {
			for (String hashKey : mapKeys) {
				if (!jedis.hexists(key, hashKey)) {
					list.add(hashKey);
				}
			}
			return list;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	* @Title: hexists
	* @Description: 名称为key的hash中是否存在键为hashKey的域
	* @param @param key
	* @param @param hashKey
	* @param @return    
	* @return boolean    返回类型
	* @throws
	 */
	public boolean hexists(String key,String hashKey){
		Jedis jedis = getJedis();
		try {
			return jedis.hexists(key, hashKey);
		} finally {
			free(jedis);
		}
	}

	/**
	 * 读取MAP中的多个KEY的值！ <br />
	 * 
	 * @date 2014-11-4 下午4:58:17
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param key
	 *            MAP实例对应的KEY。
	 * @param mapKeys
	 *            MAP中的KEY。
	 * @return MAP中多个KEY的值
	 * @since 3.0
	 */
	public List<String> getMapVals(String key, String... mapKeys) {
		if (key == null || mapKeys == null)
			return null;
		Jedis jedis = getJedis();
		List<String> args = new ArrayList<>(mapKeys.length);
		try {
			args.addAll(jedis.hmget(key, mapKeys));
			return args;
		} catch (Exception ex) {
			logger.debug("批量读取异常 {}, 更改读取方式为:\"逐个\"读取定位并忽略不存在的hashkey ",
					ex.getMessage());
			for (String hashKey : mapKeys) {
				String hashValue = jedis.hget(key, hashKey);
				if (StringUtils.isNotBlank(hashValue)) {
					args.add(hashValue);
				}
			}
			return args;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 读取hash key的一个或者多个值。 <br />
	 * 
	 * @date 2015-5-22 下午6:01:08
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param key
	 *            rediskey
	 * @param clazz
	 *            数据类型
	 * @param keys
	 *            map的keys
	 * @return 解析异常则返回null
	 * 
	 * @since 2.0.2
	 */
	public List getMapVals(String key, Class clazz, List<String> mapKeys) {
		try {
			List<String> vals = getMapVals(key, mapKeys.toArray(new String[0]));
			if (vals == null)
				return null;
			List resList = new ArrayList<>(vals.size());
			for (String v : vals) {
				if (StringUtils.isNotBlank(v)) {
					try {
						Object t = CommonUtils.mapper.readValue(v, clazz);
						resList.add(t);
					} catch (Exception ex) {
						logger.debug("继续遍历,忽略异常:" + ex.getMessage(), ex);
					}
				}
			}
			return resList;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 删除队列中的某个值 <br />
	 * 
	 * @date 2015-5-26 下午4:58:41
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param val
	 *            list中的某个值
	 * @return -1非法操作 0 不存在需要删除的值 >0 删除的个数 这里只会返回1 如果正常的话.
	 * @since 2.0.2
	 */
	public Long rmListValue(String key, String val) {
		if (val == null)
			return -1L;
		Jedis jedis = getJedis();
		try {
			return jedis.lrem(key, 1, val);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return -1L;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 删除当前会话的主存数据库中的KEY。 <br />
	 * 
	 * @date 2015-1-23 上午9:23:32
	 * @author <a href="cj@ewppay.com">chen-jie</a>
	 * @param key
	 * @since 3.0
	 */
	public void rmKey(String key) {
		if (key == null)
			return;
		Jedis jedis = getJedis();
		try {
			jedis.del(key);
		} catch (Exception ex) {
			logger.debug("", ex);
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 批量删除对应的键值
	 *<br />
	 *@date 2015-10-10 下午5:56:05
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param keyPattern  匹配
	 *@since 1.0
	 */
	public void rmKeyPattern(String keyPattern)
	{
		Set<String>  keys =	this.keys(keyPattern);
		if(keys==null || keys.size()==0){
			return;
		}
		rmKey(keys.toArray(new String[]{}));
	}
	
	
	
	public Long rmKey(String... key) {
		if (key == null)
			return 0L;
		Jedis jedis = getJedis();
		try {
			return jedis.del(key);
		} catch (Exception ex) {
			logger.debug("", ex);
		} finally {
			free(jedis);
		}
		return 0L;
	}

	/**
	 * 删除hash中的某个key值。 <br />
	 * 
	 * @date 2015-5-22 下午4:52:40
	 * @author <a href="bentengwu@163.com">伟宏</a>
	 * @param redisKey
	 * @param mapKey
	 * @since 2.0.2
	 */
	public void rmKeyInHash(String redisKey, String... mapKey) {
		if (redisKey == null || mapKey == null)
			return;
		Jedis jedis = getJedis();
		try {
			jedis.hdel(redisKey, mapKey);
		} catch (Exception ex) {
			logger.debug("", ex);
		} finally {
			free(jedis);
		}
	}

	/**
	 * 从池中拿出一个jedis,并选中对应的数据库 <br />
	 * 
	 * @date 2014-11-25 下午8:05:56
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @return
	 * @since 3.0
	 */
	private Jedis getJedis() {
		activeCount++;
		totalCount++;
		return jedisPool.getResource();
	}

	/**
	 * 将一个jedis放到池中. <br />
	 * 
	 * @date 2014-11-25 下午8:06:20
	 * @author <a href="bentengwu@163.com">thender</a>
	 * @param jedis
	 * @since 3.0
	 */
	private void free(Jedis jedis) {
		activeCount--;
		totalFreeCount++;
		jedisPool.returnResource(jedis);
	}
	
	/**
	 * 
	* @Title: getFuzzyQueryForString
	* @Description: String类型的模糊查询
	* @param @param key
	* @param @return    
	* @return List    返回类型
	* @throws
	 */
	public List getFuzzyQueryForString(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		List list = new ArrayList();
		try {
			Set keys = jedis.keys(key + "*");// 列出所有的key，
			Iterator keyIter = keys.iterator();
			String[] keyArr = (String[]) CollectionUtils.asCollection(keyIter).toArray();
			list = jedis.mget(keyArr);
			//jedis.mget(strKeys);
//			while (keyIter.hasNext()) {
//				Object obj = keyIter.next();
//				String strValue = getStringVal(obj.toString());
//				if (StringUtils.isNotBlank(strValue)) {
//					list.add(strValue);
//				}
//			}
			return list;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 
	* @Title: popFromList
	* @Description: 
	* @param @param strKey
	* @param @return    
	* @return String    返回类型
	* @throws
	 */
	public String popFromList(String strKey){
		if (strKey == null)
			return null;
		Jedis jedis = getJedis();
		try {
			return jedis.lpop(strKey);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 移除并返回列表key的尾元素。
	* @Title: rPopFromList 
	* @author cyb
	* @Description: TODO
	* @param @param strKey
	* @param @return    
	* @return String    返回类型 
	* @throws
	 */
	public String rPopFromList(String strKey){
		if (strKey == null)
			return null;
		Jedis jedis = getJedis();
		try {
			return jedis.rpop(strKey);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	
	/**
	 * 
	* @Title: getHashForMap
	* @Description: 返回名称为strKey的hash中所有的键（field）及其对应的value
	* @param @param strKey
	* @param @return    
	* @return Map<String,String>    返回类型
	* @throws
	 */
	public Map<String,String> getHashForMap(String strKey){
		if (strKey == null)
			return null;
		Jedis jedis = getJedis();
		try {
			//{20150810069=02178, 20150810068=96007, 20150810076=76461}
			return jedis.hgetAll(strKey);
		} catch (Exception ex) {
			return null;
		} finally {
			free(jedis);
		}
	}
	
	/**
	 * 
	 * @Title: getFuzzyQueryForMap
	 * @Description: 模糊查询key，返回Map类型的List:Map中存放key和key对应的val值
	 * @param: @param key
	 * @param: @param keyDes 返回list的map中key的描述字符
	 * @param: @param keyValDes 返回list的map中key对应value的描述字符
	 * @param: @param valueType key对应的val类型:String,List
	 * @param: @return
	 * @return: List
	 * @throws
	 * @author cyb
	 * @Date 2015-7-28 下午1:14:50
	 */
	public List getFuzzyQueryForMap(String key,String keyDes,String keyValDes,String valueType) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		if(StringUtils.isBlank(keyDes)){
			keyDes = "key";
		}
		if(StringUtils.isBlank(keyValDes)){
			keyValDes = "keyVal";
		}
		if(StringUtils.isBlank(valueType)){
			valueType = "String";
		}
		Jedis jedis = getJedis();
		List list = new ArrayList();
		try {
			Set keys = jedis.keys(key + "*");// 列出所有的key，
			Iterator keyIter = keys.iterator();
			while (keyIter.hasNext()) {
				Map map = Maps.newHashMap();
				Object obj = keyIter.next();
				String strValue = "";
				if(valueType.equals("List")){
					strValue = jedis.llen(obj.toString())+"";
				}else{
					strValue = jedis.get(obj.toString());
				}
				
				if (StringUtils.isNotBlank(strValue)) {
					map.put(keyDes, obj);
					map.put(keyValDes, strValue);
					list.add(map);
				}
			}
			return list;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 按条件查询匹配的key.
	 *<br />
	 *@date 2015-9-30 下午12:55:53
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param filterKeys
	 *@return	满足条件的 key。
	 *@since 1.0
	 */
	public Set<String> keys(String filterKeys)
	{
		if(StringUtils.isBlank(filterKeys))
		{
			return new HashSet<>(0);
		}
		Jedis jedis = getJedis();
		try {
			return jedis.keys(filterKeys);
		} finally {
			free(jedis);
		}
	}
	
	
	/**
	 * 
	* @Title: getFuzzyQueryByPageForMap
	* @Description: 分页模糊查询
	* @param @param page
	* @param @param keyDes
	* @param @param keyValDes
	* @param @param valueType
	* @param @return    
	* @return List    返回类型
	* @throws
	 */
	public List getFuzzyQueryByPageForMap(Page page,String keyDes,String keyValDes,String valueType) {
		String key = page.getKey();
		if (StringUtils.isBlank(key)) {
			return null;
		}
		if(StringUtils.isBlank(keyDes)){
			keyDes = "key";
		}
		if(StringUtils.isBlank(keyValDes)){
			keyValDes = "keyVal";
		}
		if(StringUtils.isBlank(valueType)){
			valueType = "String";
		}
		Jedis jedis = getJedis();
		List list = new ArrayList();
		try {
			Set keys = jedis.keys(key + "*");// 列出所有的key，
			Iterator keyIter = keys.iterator();
			page.setTotalCount(keys.size());
			int i = 0;
			while (keyIter.hasNext()) {
				Map map = Maps.newHashMap();
				Object obj = keyIter.next();
				String strValue = "";
				if(valueType.equals("List")){
					strValue = jedis.llen(obj.toString())+"";
				}else{
					strValue = jedis.get(obj.toString());
				}
				
				if (StringUtils.isNotBlank(strValue)) {
					
					if (page == null) {
						map.put(keyDes, obj);
						map.put(keyValDes, strValue);
						list.add(map);
					}else{
						int start =  (page.getPageNo()-1) * page.getPageSize();
						int end = page
								.getPageNo() * page.getPageSize() - 1;
						if(i >= start && i <= end){
							map.put(keyDes, obj);
							map.put(keyValDes, strValue);
							list.add(map);
						}
						/**
						 * page != null
									&& i >= page.getPageNo()
											* page.getPageSize() && i <= ((page
									.getPageNo() + 1) * page.getPageSize() - 1)
						 */
					}
				}
				
				i++;
			}
			return list;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		} finally {
			free(jedis);
		}
	}
	
	
	/**
	 * 匹配list的所有key，并排序
	 *<br />
	 *@date 2015-9-30 下午1:37:20
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param patterKeys
	 *@param sort  1 正序   -1 倒叙
	 *@param type  类型：fridayactivities(星期五活动)
	 *@return 对满足规则的key 的 列表 长度进行排序。 并返回排序好的列表。
	 *@since 1.0
	 */
	public List<ListProp> listSort(String patterKeys, final int sort, String type)
	{
		if(StringUtils.isBlank(patterKeys))
		{
			return new ArrayList<>(0);
		}
		Set<String> keys = keys(patterKeys);
		if(keys.size()==0) return new ArrayList<>(0);
		long start = System.currentTimeMillis();
		int timeout = 10*1000; //超时时间
		List sortList = Lists.newArrayList();

		Jedis jedis = getJedis();
		try {
			ListProp prop = null;
			for(String key : keys)
			{
				//去掉星期五活动的key
				if (!this.contain(type + ":" + key)) {
					long len = jedis.llen(key);
					prop = new ListProp(key,len);
					sortList.add(prop);
				}
			}
		} finally {
			free(jedis);
		}
		
		
		if(sortList.size()>0)
		{
			Collections.sort(sortList, new Comparator<ListProp>() {
				@Override
				public int compare(ListProp one, ListProp next) {
					return one.getLen()>next.getLen()?sort:(sort*-1);
				}
			});
		}
		
		return sortList;
	}
	
	
	
	/**
	 * 模糊查询 这方法用于检索 对象类型为clazz的列表。按长度返回
	 * 
	 * @author cyb
	 * @date 2015-5-21 下午7:08:51
	 * @param key
	 *            所有以key开头的mainkey。
	 * @param clazz
	 *            列表中的数据类型
	 * @param 列表的开始位置
	 * @param 列表的结束位置
	 */
	@SuppressWarnings("rawtypes")
	public List getFuzzyQuery(String key, Class clazz, long start, long end) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		List list = new ArrayList();
		try {
			Set keys = jedis.keys(key + "*");// 列出所有的key，
			Iterator keyIter = keys.iterator();
			while (keyIter.hasNext()) {
				Object obj = keyIter.next();// 广告Key
				List<String> valueList = jedis.lrange(obj.toString(), start,
						end);
				if (valueList != null && valueList.size() > 0) {
					for (String valueStr : valueList) {
						ObjectMapper m = new ObjectMapper();
						list.add(m.readValue(valueStr, clazz));
					}
				}
			}
			return list;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		} finally {
			free(jedis);
		}
	}

	/**
	 * 读取连接redis的连接活跃资源信息 <br />
	 * 
	 * @date 2015-6-12 下午7:45:08
	 * @author <a href="xwh@ewppay.com">伟宏</a>
	 * @return 信息数组 下标0 池自己统计的活跃数. 下标1 ,自己统计活跃数. 下标2, 从系统启动到现在的总链接数. 下标3, 总释放数
	 * @since 1.0
	 */
	public String[] getRedisInfo() {
		return new String[] { jedisPool.getNumActive() + "", activeCount + "",
				totalCount + "", totalFreeCount + "" };
	}

}
