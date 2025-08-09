package org.springframework.ai.mcp.sample.server;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import org.springframework.ai.mcp.sample.server.config.AppConfig;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
	@Resource
	private RestTemplate restTemplate;

	@Value("${weather.key}")
	private String amapApiKey;

	// 高德地图天气查询API密钥（留空待填写）
//	private String amapApiKey = "0b92fc585f716446274dd127a38d3b5f";

	@Tool(description = "Get real-time weather for a specific city using Amap API. " +
			"Requires city code (e.g., 110000 for Beijing, 310000 for Shanghai)")
	public String getRealTimeWeather(
			String cityCode  // 城市编码（例如：北京110000，上海310000）
	) {
		// 验证参数和API密钥
		if (cityCode == null || cityCode.trim().isEmpty()) {
			return "Error: City code cannot be empty";
		}
		if (amapApiKey.isEmpty()) {
			return "Error: Amap API key is not configured";
		}

		// 构建API请求URL
		String apiUrl = String.format(
				"https://restapi.amap.com/v3/weather/weatherInfo?city=%s&key=%s&extensions=base",
				cityCode, amapApiKey
		);

		try {
			// 发起HTTP请求并解析响应
			JsonNode response = restTemplate.getForObject(apiUrl, JsonNode.class);

			// 验证API返回状态
			if (response == null || !"1".equals(response.get("status").asText())) {
				return "Failed to get weather data: " + response.get("info").asText();
			}

			// 提取实时天气信息
			JsonNode weatherNode = response.get("lives").get(0);
			return String.format(
					"Real-time weather in %s:\nTemperature: %s°C\nWeather: %s\nWind direction: %s\nWind power: %s\nHumidity: %s%%",
					weatherNode.get("city").asText(),
					weatherNode.get("temperature").asText(),
					weatherNode.get("weather").asText(),
					weatherNode.get("winddirection").asText(),
					weatherNode.get("windpower").asText(),
					weatherNode.get("humidity").asText()
			);
		} catch (Exception e) {
			return "Error retrieving weather data: " + e.getMessage();
		}
	}

	@Tool(description = "Get 3-day weather forecast for a specific city using Amap API. " +
			"Requires city code (e.g., 110000 for Beijing, 310000 for Shanghai)")
	public String getWeatherForecast(
			String cityCode  // 城市编码（例如：北京110000，上海310000）
	) {
		// 验证参数和API密钥
		if (cityCode == null || cityCode.trim().isEmpty()) {
			return "Error: City code cannot be empty";
		}
		if (amapApiKey.isEmpty()) {
			return "Error: Amap API key is not configured";
		}

		// 构建API请求URL
		String apiUrl = String.format(
				"https://restapi.amap.com/v3/weather/weatherInfo?city=%s&key=%s&extensions=all",
				cityCode, amapApiKey
		);

		try {
			// 发起HTTP请求并解析响应
			JsonNode response = restTemplate.getForObject(apiUrl, JsonNode.class);

			// 验证API返回状态
			if (response == null || !"1".equals(response.get("status").asText())) {
				return "Failed to get forecast data: " + response.get("info").asText();
			}

			// 提取3天预报信息
			StringBuilder forecast = new StringBuilder("3-day weather forecast:\n");
			for (JsonNode forecastNode : response.get("forecasts").get(0).get("casts")) {
				forecast.append(String.format(
						"%s: %s to %s, %s, Wind: %s %s\n",
						forecastNode.get("date").asText(),
						forecastNode.get("nighttemp").asText(),
						forecastNode.get("daytemp").asText(),
						forecastNode.get("dayweather").asText(),
						forecastNode.get("daywind").asText(),
						forecastNode.get("daypower").asText()
				));
			}
			return forecast.toString();
		} catch (Exception e) {
			return "Error retrieving forecast data: " + e.getMessage();
		}
	}

}

