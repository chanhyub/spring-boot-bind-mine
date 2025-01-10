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
public class GoogleClient {
    // TODO : url, 처리 과정 수정
    private String tokenInfoUrl = "https://kapi.kakao.com/v1/user/access_token_info";

    //토큰 정보 요청
    public ResponseEntity<String> getTokenInfo(String accessToken){
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();

        try{
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            var builder = UriComponentsBuilder.fromHttpUrl(tokenInfoUrl);

            headers.add("Authorization", "Bearer " + accessToken);

            var entity = new HttpEntity<>(headers);

            var response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class);

            if(response.getBody() == null || response.getBody().contains("-1")){
                return ResponseEntity.badRequest().body("카카오 플랫폼에서 일시적인 오류가 발생하였습니다.\n잠시 후 다시 시도해주세요.");
            }else if(response.getBody().contains("-2")){
                return ResponseEntity.badRequest().body("엑세스 토큰의 형식이 유효하지 않습니다.");
            }

            return response;
        }catch (Exception e){
            e.printStackTrace();

            return ResponseEntity.badRequest().body("this access token is already expired");
        }
    }
}
