package com.currencyexchange.configuration;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
    Resource actualResource = resource.getResource();
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(actualResource);

    return new org.springframework.core.env.PropertiesPropertySource(
      actualResource.getFilename(), factory.getObject());
  }
}
