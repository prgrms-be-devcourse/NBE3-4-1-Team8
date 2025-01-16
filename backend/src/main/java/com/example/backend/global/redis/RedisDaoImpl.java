package com.example.backend.global.redis;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisDaoImpl implements RedisDao {
	private final RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public RedisDaoImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 *
	 * @param key redis key 값 (member-id)
	 * @param data uuid 인증 코드 저장 (certificationCode)
	 * @param timeout 저장 시간
	 */
	@Override
	public void setData(String key, String data, long timeout) {
		ValueOperations<String, Object> value = redisTemplate.opsForValue();
		value.set(key, data, timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	public String getData(String key) {
		ValueOperations<String, Object> value = redisTemplate.opsForValue();
		return (String)value.get(key);
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void setHashDataAll(String key, Map<?, ?> map) {
		HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
		for (Object o : map.keySet()) {
			log.info("redisDao={}", map.get(o));
		}
		hash.putAll(key, map);
	}

	@Override
	public Map<Object, Object> getHashDataAll(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	@Override
	public String getHashData(String key, String hashKey) {
		return (String)redisTemplate.opsForHash().get(key, hashKey);
	}

	@Override
	public void setHashData(String key, String hashKey, String data) {
		redisTemplate.opsForHash().put(key, hashKey, data);
	}

	@Override
	public void setTimeout(String key, long timeout) {
		redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}
}
