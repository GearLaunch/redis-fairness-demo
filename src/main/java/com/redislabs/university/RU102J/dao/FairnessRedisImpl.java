package com.redislabs.university.RU102J.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.redislabs.university.RU102J.api.CapacityReport;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.SiteCapacityTuple;
import com.redislabs.university.RU102J.resources.FairnessJob;
import org.eclipse.jetty.http.HttpStatus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.UUID.*;

public class FairnessRedisImpl implements FairnessDao {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    public FairnessRedisImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void update(MeterReading reading) {
        String capacityRankingKey = RedisSchema.getCapacityRankingKey();
        Long siteId = reading.getSiteId();

        double currentCapacity = reading.getWhGenerated() - reading.getWhUsed();

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(capacityRankingKey, currentCapacity, String.valueOf(siteId));
        }
    }

    @Override
    public CapacityReport getReport(Integer limit) {
        CapacityReport report;
        String key = RedisSchema.getCapacityRankingKey();

        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline p = jedis.pipelined();
            Response<Set<Tuple>> lowCapacity = p.zrangeWithScores(key, 0, limit-1);
            Response<Set<Tuple>> highCapacity = p.zrevrangeWithScores(key, 0,
                    limit-1);
            p.sync();

            List<SiteCapacityTuple> lowCapacityList = lowCapacity.get().stream()
                    .map(SiteCapacityTuple::new)
                    .collect(Collectors.toList());

            List<SiteCapacityTuple> highCapacityList = highCapacity.get().stream()
                    .map(SiteCapacityTuple::new)
                    .collect(Collectors.toList());

            report = new CapacityReport(highCapacityList, lowCapacityList);
        }

        return report;
    }

    // Challenge #4
    @Override
    public Long getRank(Long siteId) {
        // START Challenge #4
        try(Jedis jedis = jedisPool.getResource()) {
            String key = RedisSchema.getCapacityRankingKey();
            return jedis.zrevrank(key, String.valueOf(siteId));
        }
        // END Challenge #4
    }

    @Override
    public List<FairnessJob> list() {
        return null;
    }

    @Override
    public String add(final List<FairnessJob> jobs) {
        // create transaction
        final String batchUuid = randomUUID().toString();
        try(Jedis jedis = jedisPool.getResource()) {
            final Transaction tx = jedis.multi();
            final Builder<Response<Long>> builder = ImmutableList.builder();

            for (final FairnessJob job : jobs) {
                final String jobQueueKey = "queue:" + job.getStore();
                final String jobUuid = randomUUID().toString();
                try {
                    final String value = objectMapper.writeValueAsString(job);
                    final Response<Long> response = tx.lpush(jobQueueKey,value);
                    builder.add(response);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            List<Object> result = tx.exec();
            for (Response<Long> response : builder.build()) {
                System.out.println("response: " + response.get());
            }

            final Map<String,String> ret = ImmutableMap.of("batchUuid",batchUuid);
            return objectMapper.writeValueAsString(ret);

        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object run() {
        return javax.ws.rs.core.Response.ok();
    }
}
