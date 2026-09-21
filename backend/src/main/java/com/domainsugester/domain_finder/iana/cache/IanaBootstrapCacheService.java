package com.domainsugester.domain_finder.iana.cache;

import com.domainsugester.domain_finder.iana.dto.IanaTldCachedResponse;
import com.domainsugester.domain_finder.tld.dto.TldCachedResponse;
import com.domainsugester.domain_finder.tld.cache.TldCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IanaBootstrapCacheService implements TldCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Duration TTL = Duration.ofDays(10);

    @Override
    public void save(String key, String value) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, TTL);
    }

    @Override
    public TldCachedResponse fetchBootstrap() {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Set<String> keys = scanKeys("hostinger:tld:*");
        Map<String, String> map = new HashMap<>();
        keys.forEach(key -> {
            map.put(key, ops.get(key).toString());
        });
        String version = ops.get("iana:version").toString();
        String description = ops.get("iana:description").toString();
        return new IanaTldCachedResponse(description, version, map);
    }
    public String get(String key) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        var value = ops.get(key);
        if(value == null) return null;
        return value.toString();
    }
    private Set<String> scanKeys(String keyPrefix){
        Set<String> keys = new HashSet<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(keyPrefix)
                .count(100)
                .build();
        try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                connection -> connection.keyCommands().scan(options))) {

            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        }
        return keys;
    }
}
