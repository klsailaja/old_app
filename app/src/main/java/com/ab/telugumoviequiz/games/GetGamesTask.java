package com.ab.telugumoviequiz.games;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GetGamesTask<T> extends GetTask<T> {

    public GetGamesTask(String reqUri, int reqId, CallbackResponse callbackResponse, Class<T> classType, Object helperObject) {
        super(reqUri, reqId, callbackResponse, classType, helperObject);
    }

    public HttpEntity<?> getHttpEntity(List<MediaType> acceptableMediaTypes) {
        if (acceptableMediaTypes == null) {
            acceptableMediaTypes = new ArrayList<>();
            acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
            acceptableMediaTypes.add(MediaType.IMAGE_JPEG);
            acceptableMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(acceptableMediaTypes);

        // Populate the headers in an HttpEntity object to use for the request
        return new HttpEntity<>(requestHeaders);
    }

    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(getReqFactory());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
