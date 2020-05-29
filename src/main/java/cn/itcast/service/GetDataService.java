package cn.itcast.service;

import cn.itcast.util.JedisUtil;
import cn.itcast.util.UiBean;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import java.util.Map;
@Service
public class GetDataService {
    //获取 Jedis 对象
    Jedis jedis = JedisUtil.getJedis();
    public String getData() {
        //获取 Redis 数据库中键为 bussiness::order::total 的数据
        Map<String, String> testData = jedis.hgetAll("bussiness::order::total");
        String [] produceId = new String [10];
        String [] producetSumPrice = new String [10];
        int i=0;
        //封装数据
        for(Map.Entry<String,String> entry : testData.entrySet()){
            produceId[i]=entry.getKey();
            producetSumPrice[i] =entry.getValue();
            i++;
        }
        //可以查看 UiBean 的定义。
        UiBean ub = new UiBean();
        ub.setProducetSumPrice(producetSumPrice);
        ub.setProduceId(produceId);
        //将 ub 对象转换为 Json 格式的字符串
        return JSONObject.toJSONString(ub);
    } }