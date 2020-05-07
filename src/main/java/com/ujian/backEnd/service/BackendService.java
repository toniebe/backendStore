package com.ujian.backEnd.service;

import com.ujian.backEnd.model.Chart;
import com.ujian.backEnd.repository.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BackendService {

    @Autowired
    private UserMapper userMapper;

    Logger logger = LoggerFactory.getLogger(BackendService.class);

    @Async
    public CompletableFuture<List<Chart>> saveitem(String key){
        List<Chart> charts = parseRedis(key);
        System.out.println(charts.size());
        logger.info("saving list of item of size {}", charts.size(), "" + Thread.currentThread().getName());
        for (Chart cs : charts){
            charts = userMapper.insert(cs);
        }
        return CompletableFuture.completedFuture(charts);
    }


    public CompletableFuture<List<Chart>> show(){
        logger.info("get shoping chart " + Thread.currentThread().getName());
        List<Chart> charts = userMapper.showAll();
        return CompletableFuture.completedFuture(charts);
    }



    public List<Chart> parseRedis(String key){
        Jedis jedis = new Jedis("localhost");
        final List<Chart> charts = new ArrayList<>();
        List<String> list = jedis.lrange(key, 0 ,15);
        for(int i = 0;i<list.size();i++){
            Chart chart = new Chart();
            System.out.println("isi " +list.get(i));
            chart.setItem(list.get(i));
            chart.setNama(key);
            charts.add(chart);
        }
        return charts;
    }

}
