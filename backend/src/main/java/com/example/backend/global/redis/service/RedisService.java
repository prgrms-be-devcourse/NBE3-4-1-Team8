package com.example.backend.global.redis.service;

import java.util.Map;

public interface RedisService {
	public void setData(String key, String data, long timeout);

	public String getData(String key);

	public void delete(String key);

	void setHashDataAll(String key, Map<?, ?> map);

	Map<Object, Object> getHashDataAll(String key);

	String getHashData(String key, String hashKey);

	void setHashData(String key, String hashKey, String data);

	void setTimeout(String key, long timeout);

	Boolean hasKey(String key);
}
