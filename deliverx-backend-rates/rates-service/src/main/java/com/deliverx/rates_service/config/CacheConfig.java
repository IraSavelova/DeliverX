package com.deliverx.rates_service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Кэш для результатов перевозчиков.
 *
 * Зачем: ДЛ ограничивает частоту запросов (rate limit 429).
 * Решение: кэшируем ответ на 10 минут по ключу маршрут+габариты.
 * Одинаковые запросы от разных пользователей не идут к ДЛ повторно.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("rates");
        manager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // кэш живёт 10 минут
                .maximumSize(500)                        // максимум 500 уникальных маршрутов
                .recordStats()                           // для отладки — считает hit/miss
        );
        return manager;
    }
}
