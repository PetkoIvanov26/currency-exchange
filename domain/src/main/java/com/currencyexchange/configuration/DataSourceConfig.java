package com.currencyexchange.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:database.yml", factory = YamlPropertySourceFactory.class)
public class DataSourceConfig {

  @Value("${spring.datasource.url}")
  private String datasourceUrl;

  @Value("${spring.datasource.username}")
  private String datasourceUsername;

  @Value("${spring.datasource.password}")
  private String datasourcePassword;

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  @Value("${spring.datasource.hikari.maximum-pool-size}")
  private int maximumPoolSize;

  @Bean
  public DataSource dataSource() {
    DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.url(datasourceUrl);
    dataSourceBuilder.username(datasourceUsername);
    dataSourceBuilder.password(datasourcePassword);
    dataSourceBuilder.driverClassName(driverClassName);

    HikariDataSource dataSource = (HikariDataSource) dataSourceBuilder.build();
    dataSource.setMaximumPoolSize(maximumPoolSize);
    return dataSource;
  }
}