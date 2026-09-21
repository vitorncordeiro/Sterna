package com.domainsugester.domain_finder.batch.cache;

import com.domainsugester.domain_finder.batch.dto.batch.BatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public Integer decr(UUID batchId){
        Long value = redisTemplate.opsForValue().decrement("batch:" + batchId.toString() + ":remaining");
        return value == null ? null : value.intValue();
    }
    public void saveRemaining(UUID batchId, Integer remaining){
        redisTemplate.opsForValue().set("batch:" + batchId.toString() + ":remaining", remaining);
    }
    public void saveBatchResult(UUID batchId, BatchResult batchResult){

        redisTemplate.opsForValue().set("batch:" + batchId.toString(), batchResult);

    }

    public void saveRecipient(UUID batchId, String email){
        redisTemplate.opsForValue().set("batch:" + batchId.toString() + ":recipient", email);
    }

    public String getRecipient(UUID batchId){
        return (String) redisTemplate.opsForValue().get("batch:" + batchId.toString() + ":recipient");
    }

    public Integer getBatchRemaining(UUID batchId){
        return (Integer) redisTemplate.opsForValue().get("batch:" + batchId.toString() + ":remaining");
    }
    public BatchResult getBatchResult(UUID batchId){
        Object stored = redisTemplate.opsForValue().get("batch:" + batchId.toString());
        if (stored == null) return null;
        // If we stored a BatchResult object directly, just return it
        if (stored instanceof BatchResult br) {
            return br;
        }
        // When RedisTemplate deserializes a JSON representation it may come back as a LinkedHashMap
        if (stored instanceof LinkedHashMap) {
            LinkedHashMap outer = (LinkedHashMap) stored;
            // common shape: { "domainAvailability": {"example.com": false, ... } }
            Object maybeInner = outer.get("domainAvailability");
            Map<String, Boolean> domainMap = new LinkedHashMap<>();
            if (maybeInner instanceof Map) {
                Map<?,?> innerMap = (Map<?,?>) maybeInner;
                for (Map.Entry<?,?> e : innerMap.entrySet()) {
                    domainMap.put(String.valueOf(e.getKey()), Boolean.valueOf(String.valueOf(e.getValue())));
                }
                return new BatchResult(domainMap);
            }
            // fallback: maybe the outer map is already the domain map
            for (Object key : outer.keySet()) {
                Object v = outer.get(key);
                domainMap.put(String.valueOf(key), Boolean.valueOf(String.valueOf(v)));
            }
            return new BatchResult(domainMap);
        }
        // unknown shape: return empty result to avoid breaking callers
        return new BatchResult(new LinkedHashMap<>());
    }
}
