package com.example.springbootbindmine.modules.common.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
public class KakaoClient {
    private String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

    //토큰 정보 요청
    public ResponseEntity<String> getUserInfo(String accessToken){
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();

        try{
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            var builder = UriComponentsBuilder.fromHttpUrl(userInfoUrl);

            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            var entity = new HttpEntity<>(headers);


            return restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class);

        }catch (Exception e){
            e.printStackTrace();

            return ResponseEntity.badRequest().body("this access token is already expired");
        }
    }
}
