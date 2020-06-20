package io.github.shreeshasa.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author shreeshasa
 */
@Profile ("local-discovery")
@EnableDiscoveryClient
@Configuration
public class LocalDiscoveryConfig {

}
