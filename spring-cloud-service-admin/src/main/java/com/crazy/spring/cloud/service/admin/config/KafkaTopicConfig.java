package com.crazy.spring.cloud.service.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.MessageChannel;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 项目启动动态读取kafka的topic配置并创建topic，创建完成回写表状态已在kafka生成topic
 * @author Louis
 */
@Slf4j
public class KafkaTopicConfig {

    @Autowired
    private BinderAwareChannelResolver resolver;

    @PostConstruct
    public void initKafkaTopic() {
        // 获取本地数据表中的topic，只获取kafka中没有的topic
        // List<TopicConfigVo> list = topicConfigService.getTopicList();
        List<String> topics = new ArrayList<>();
        for (String topic : topics) {
            // 动态去生成topic的，先检查kafka中有没有传入的topic，有就直接返回topic，没有则新建
            MessageChannel messageChannel = resolver.resolveDestination(topic);
            if(messageChannel != null) {
                // 更新表中的状态为kafka中已存在改topic
                // topicConfigService.updateTopicStatusById(topicConfigVo.getTopicId());
            }
        }
    }
}
