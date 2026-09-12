package com.domainsugester.domain_finder.batch.cache;

import com.domainsugester.domain_finder.batch.dto.batch.BatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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
        LinkedHashMap batchResult = (LinkedHashMap) redisTemplate.opsForValue().get("batch:" + batchId.toString());
        return new BatchResult(batchResult);
    }
}
