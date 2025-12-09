// java
package com.cleanteam.mandarinplayer.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    String[] origins = { "http://localhost:5173", "http://127.0.0.1:5173" };

    // WebSocket nativo
    registry.addEndpoint("/ws").setAllowedOriginPatterns(origins);

    // SockJS en un endpoint distinto
    registry.addEndpoint("/ws-sockjs").setAllowedOriginPatterns(origins).withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) { }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> converters) {
    converters.add(new MappingJackson2MessageConverter());
    return false; // mantiene convertidores por defecto y Jackson
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/topic", "/queue"); // sin "/user"
    registry.setUserDestinationPrefix("/user");
  }
}