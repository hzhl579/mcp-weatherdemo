package cn.bugstack.WeatherMCP.config;


import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GPUOllamaConfig {
        @Bean
        public ChatClient chatClient(OllamaChatModel ollamaChatModel, ToolCallbackProvider tools) {
            DefaultChatClientBuilder defaultChatClientBuilder = new DefaultChatClientBuilder(ollamaChatModel, ObservationRegistry.NOOP, (ChatClientObservationConvention) null);
            return defaultChatClientBuilder
                    .defaultTools(tools)
                    .defaultOptions(OllamaOptions.builder()
                            .build())
                    .build();
        }


}