package com.interswitch.discountvoucherz.api.config;

import com.interswitch.discountvoucherz.api.queue.AuditEventPublisher;
import com.interswitch.discountvoucherz.api.queue.DiscountVoucherDistPublisher;
import com.interswitch.discountvoucherz.api.queue.publisher.impl.AuditEventPublisherImpl;
import com.interswitch.discountvoucherz.api.queue.publisher.impl.DiscountVoucherDistPublisherImpl;
import com.interswitch.discountvoucherz.api.queue.subscriber.DeleteCampaignVoucherSubscriber;
import com.interswitch.discountvoucherz.api.service.DiscountVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RedisConfig {
    @Autowired
    private DiscountVoucherService service;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        template.afterPropertiesSet();
        return template;
    }


    @Bean
    DiscountVoucherDistPublisher voucherMailPublisher() {
        return new DiscountVoucherDistPublisherImpl(redisTemplate(),
                distributeVoucherTopic());
    }

    @Bean
    MessageListenerAdapter deleteVoucherListener() {
        return new MessageListenerAdapter(new DeleteCampaignVoucherSubscriber(service));
    }

    @Bean
    AuditEventPublisher auditEventPublisher(){
        return new AuditEventPublisherImpl(redisTemplate(), new ChannelTopic("pubsub:audit-trail"));
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(deleteVoucherListener(), deleteVoucherTopic());
        return container;
    }

    @Bean
    ChannelTopic deleteVoucherTopic() {
        return new ChannelTopic("pubsub:discount-voucher-delete");
    }

    @Bean
    ChannelTopic distributeVoucherTopic() {
        return new ChannelTopic("pubsub:distribute-discount-voucher");
    }
}
