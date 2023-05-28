package com.coder.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.coder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    KafkaTemplate kafkaTemplate;

    // 处理事件 （发一个消息）
    public void fireEvent(Event event) {
        // 1 将事件发布到指定的 kafka主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
