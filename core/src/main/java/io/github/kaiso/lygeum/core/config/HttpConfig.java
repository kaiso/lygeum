package io.github.kaiso.lygeum.core.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class HttpConfig {

  @Bean
  public HttpMessageConverters converters(ObjectMapper objectMapper) {
    return new HttpMessageConverters(
        true, Arrays.asList(new MappingJackson2HttpMessageConverter(objectMapper)));
  }
}
