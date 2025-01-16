package com.example.backend.global.redis;

import java.util.Map;

public interface RedisDao {
	void setData(String key, String data, long timeout);

	String getData(String key);

	void delete(String key);

	void setHashDataAll(String key, Map<?, ?> map);

	Map<Object, Object> getHashDataAll(String key);

	String getHashData(String key, String hashKey);

	void setHashData(String key, String hashKey, String data);

	void setTimeout(String key, long timeout);

	Boolean hasKey(String key);

}
