package com.oreilly.restclient.services;

import com.oreilly.restclient.config.MyProperties;
import com.oreilly.restclient.json.JokeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class JokeService {
    private final WebClient client;
    private final RestTemplate template;
    private final String baseUrl;

    @Autowired
    public JokeService(WebClient.Builder builder, RestTemplateBuilder restTemplateBuilder,
                       MyProperties properties) {
        baseUrl = properties.getJokeUrl();
        client = builder.baseUrl(baseUrl).build();
        template = restTemplateBuilder.build();
    }

    public String getJokeRT(String first, String last) {
        String url = baseUrl + "/jokes/random?limitTo=[nerdy]&firstName="
                + first + "&lastName=" + last;
        return template.getForObject(url, JokeResponse.class).getValue().getJoke();
    }

    public String getJoke(String first, String last) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/jokes/random")
                        .queryParam("limitTo", "[nerdy]")
                        .queryParam("firstName", first)
                        .queryParam("lastName", last)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JokeResponse.class)
                .map(jokeResponse -> jokeResponse.getValue().getJoke())
                .block(Duration.ofSeconds(2));
    }
}
