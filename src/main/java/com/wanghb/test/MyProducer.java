package com.wanghb.test;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class MyProducer {
    private static KafkaProducer<String, String> producer;
    //初始化
    static {
        Properties properties = new Properties();
        //kafka启动，生产者建立连接broker的地址
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        //kafka序列化方式
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //自定义分区分配器
        properties.put("partitioner.class", "com.wanghb.test.CustomPartitioner");
        producer = new KafkaProducer<>(properties);
    }

    /**
     * 创建topic：.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181
     * --replication-factor 1 --partitions 1 --topic kafka-study
     * 创建消费者：.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092
     * --topic imooc-kafka-study --from-beginning
     */
    //发送消息，发送完后不做处理
    private static void sendMessageForgetResult() {
        ProducerRecord<String, String> record = new ProducerRecord<>("kafka-study", "name", "ForgetResult");
        producer.send(record);
        producer.close();
    }
    //发送同步消息，获取发送的消息
    private static void sendMessageSync() throws Exception {
//        ProducerRecord<String, String> record = new ProducerRecord<>("kafka-study", "name", "sync");
        String str = "{\"uid\":\"hw15876580425695\",\"tickets\":10,\"propIds\":[62,62,67,68,62,68,62,60,67,68,10068],\"sign\":\"b3648c0e516549faaf65dc35eaa7bbe5\",\"_loginInfo\":null,\"roomUid\":\"hw15853761467553\",\"time\":1589874671302,\"type\":10}";
//        String str = "{\"uid\":\"hw15876580425695\",\"tickets\":10,\"propIds\":[87],\"sign\":\"b3648c0e516549faaf65dc35eaa7bbe5\",\"_loginInfo\":null,\"roomUid\":\"hw15853761467553\",\"time\":1589874671302,\"type\":10}";
//        String str = "{\"uid\":\"hw15876580425695\",\"tickets\":10,\"propIds\":[88],\"sign\":\"b3648c0e516549faaf65dc35eaa7bbe5\",\"_loginInfo\":null,\"roomUid\":\"hw15853761467553\",\"time\":1589874671302,\"type\":10}";
//        ProducerRecord<String, String> record = new ProducerRecord<>("haiwan_chatroominteraction1", "tf_out_all_price", str);
        ProducerRecord<String, String> record = new ProducerRecord<>("test", "tf_out_all_price", str);
        RecordMetadata result = producer.send(record).get();
        System.out.println(result.topic());//imooc-kafka-study
        System.out.println(result.partition());//分区为0
        System.out.println(result.offset());//已发送一条消息，此时偏移量+1
        producer.close();
    }
    /**
     * 创建topic：.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181
     * --replication-factor 1 --partitions 3 --topic kafka-study-x
     * 创建消费者：.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092
     * --topic kafka-study-x --from-beginning
     */
    private static void sendMessageCallback() {
        ProducerRecord<String, String> record = new ProducerRecord<>("kafka-study-x", "name", "callback");
        producer.send(record, new MyProducerCallback());
        //发送多条消息
        record = new ProducerRecord<>("kafka-study-x", "name-x", "callback");
        producer.send(record, new MyProducerCallback());
        producer.close();
    }
    //发送异步消息
    //场景：每条消息发送有延迟，多条消息发送，无需同步等待，可以执行其他操作，程序会自动异步调用
    private static class MyProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                e.printStackTrace();
                return;
            }
            System.out.println("*** MyProducerCallback ***");
            System.out.println(recordMetadata.topic());
            System.out.println(recordMetadata.partition());
            System.out.println(recordMetadata.offset());
        }
    }
    public static void main(String[] args) throws Exception {
        //sendMessageForgetResult();
        sendMessageSync();
//        sendMessageCallback();
    }
}