package cn.bugstack.WeatherMCP.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WeatherController {

    @Autowired
    private ChatClient client;


    @GetMapping("/weather/forecast")
    public String getWeatherForecast(String cityCode) {
        // 直接调用天气预报工具的便捷接口
        return client.prompt()
                .user("获取城市编码为" + cityCode + "的三天天气预报")
                .call()
                .content();
    }

    @GetMapping("/chat")
    public String chat(String input) {
        return client.prompt()
                .user(input)
                .call()
                .content();
    }
}
