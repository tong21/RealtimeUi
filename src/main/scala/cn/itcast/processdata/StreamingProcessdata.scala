package cn.itcast.processdata
import com.alibaba.fastjson.{JSON, JSONObject}
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis
object StreamingProcessdata {
  //每件商品总销售额
  val orderTotalKey = "bussiness::order::total"
  //总销售额
  val totalKey = "bussiness::order::all"
  //Redis 数据库
  val dbIndex = 0
  def main(args: Array[String]): Unit = {
    //1、创建 SparkConf 对象
    val sparkConf: SparkConf = new SparkConf()
      .setAppName("KafkaStreamingTest")
      .setMaster("local[4]")
    //2、创建 SparkContext 对象
    val sc = new SparkContext(sparkConf)
    sc.setLogLevel("WARN")
    //3、构建 StreamingContext 对象
    val ssc = new StreamingContext(sc, Seconds(3))
    //4、消息的偏移量就会被写入到 checkpoint 中
    ssc.checkpoint("./spark-receiver")
    //4、设置 Kafka 参数
    val kafkaParams = Map("bootstrap.servers" ->
      "hadoop002:9092,hadoop003:9092,hadoop004:9092",
      "group.id" -> "spark-receiver")
    //5、指定 Topic 相关信息
    val topics = Set("itcast_order")
    //6、通过 KafkaUtils.createDirectStream 利用低级 api 接受 kafka 数据
    val kafkaDstream: InputDStream[(String, String)] =
      KafkaUtils.createDirectStream
        [String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    //7、获取 Kafka 中 Topic 数据，并解析 JSON 格式数据
    val events: DStream[JSONObject] = kafkaDstream.flatMap(line =>
      Some(JSON.parseObject(line._2)))
    //按照 productID 进行分组统计个数和总价格
    val orders: DStream[(String, Int, Long)] = events.map(x => (x.getString("productId"),
      x.getLong("productPrice"))).groupByKey().map(x => (x._1, x._2.size, x._2.reduceLeft(_ + _)))
    orders.foreachRDD(x =>
      x.foreachPartition(partition =>
        partition.foreach(x => {
          println("productId="
            + x._1 + " count=" + x._2 + " productPricrice=" + x._3)
          //获取 Redis 连接资源
          val jedis: Jedis = RedisClient.pool.getResource()
          //指定数据库
          jedis.select(dbIndex)
          //每个商品销售额累加
          jedis.hincrBy(orderTotalKey, x._1, x._3)
          //总销售额累加
          jedis.incrBy(totalKey, x._3)
//          RedisClient.pool.returnResource(jedis) Jedis3.0之后使用jedis.close()
          jedis.close()
        })
      )
    )
    ssc.start()
    ssc.awaitTermination()
  }
}