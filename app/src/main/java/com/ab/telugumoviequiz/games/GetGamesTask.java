package com.ab.telugumoviequiz.games;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

public class GetGamesTask<T> extends GetTask<T> {

    public GetGamesTask(String reqUri, int reqId, CallbackResponse callbackResponse, Class<T> classType, Object helperObject) {
        super(reqUri, reqId, callbackResponse, classType, helperObject);
    }

    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(getReqFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
