package com.example.backend.global.redis.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.backend.global.redis.RedisDao;

@Component
public class RedisServiceImpl implements RedisService {
	private final RedisDao redisDao;

	public RedisServiceImpl(RedisDao redisDao) {
		this.redisDao = redisDao;
	}

	@Override
	public void setData(String key, String data, long timeout) {
		redisDao.setData(key, data, timeout);
	}

	@Override
	public String getData(String key) {
		return redisDao.getData(key);
	}

	@Override
	public void delete(String key) {
		redisDao.delete(key);
	}

	@Override
	public void setHashDataAll(String key, Map<?, ?> map) {
		redisDao.setHashDataAll(key, map);
	}

	@Override
	public Map<Object, Object> getHashDataAll(String key) {
		return redisDao.getHashDataAll(key);
	}

	@Override
	public String getHashData(String key, String hashKey) {
		return redisDao.getHashData(key, hashKey);
	}

	@Override
	public void setHashData(String key, String hashKey, String data) {
		redisDao.setHashData(key, hashKey, data);
	}

	@Override
	public void setTimeout(String key, long timeout) {
		redisDao.setTimeout(key, timeout);
	}

	@Override
	public Boolean hasKey(String key) {
		return redisDao.hasKey(key);
	}
}
