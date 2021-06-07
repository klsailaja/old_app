package com.ab.telugumoviequiz.common;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class PostTask<T,R> implements Runnable {
    private final String reqUri;
    private final int requestId;
    private CallbackResponse callbackResponse;
    private int reqTimeOut = 20 * 1000;
    private T postObject;
    private final Class<R> classType;
    private Object helperObject;
    private Activity activity;
    private AlertDialog alertDialog;
    private String waitingMessage;

    public PostTask(String reqUri, int reqId, CallbackResponse callbackResponse, T postObject, Class<R> classType) {
        this.reqUri = reqUri;
        this.requestId = reqId;
        this.callbackResponse = callbackResponse;
        this.postObject = postObject;
        this.classType = classType;
    }

    public void setActivity(Activity activity, String waitingMessage) {
        this.activity = activity;
        this.waitingMessage = waitingMessage;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getReqUri() {
        return reqUri;
    }

    public CallbackResponse getCallbackResponse() {
        return callbackResponse;
    }

    public void setCallbackResponse(CallbackResponse callbackResponse) {
        this.callbackResponse = callbackResponse;
    }

    public void setPostObject(T postObject) {
        this.postObject = postObject;
    }

    public void setReqTimeOut(int timeOut) {
        this.reqTimeOut = timeOut;
    }

    public void setHelperObject(Object helperObject) {
        this.helperObject = helperObject;
    }
    public Object getHelperObject() {
        return this.helperObject;
    }

    public HttpEntity<?> getHttpEntity(List<MediaType> acceptableMediaTypes) {
        if (acceptableMediaTypes == null) {
            acceptableMediaTypes = new ArrayList<>();
            acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(acceptableMediaTypes);

        // Populate the headers in an HttpEntity object to use for the request
        return new HttpEntity<>(postObject, requestHeaders);
    }

    private ClientHttpRequestFactory getReqFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(reqTimeOut);
        return requestFactory;
    }

    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(getReqFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }
    public void run() {
        if (activity != null) {
            Runnable run = () -> {
                alertDialog = Utils.getProgressDialog(activity, waitingMessage);
                alertDialog.show();
            };
            activity.runOnUiThread(run);
        }
        try {
            RestTemplate restTemplate = getRestTemplate();
            ResponseEntity<R> responseEntity = restTemplate.exchange(getReqUri(), HttpMethod.POST, getHttpEntity(null),
                    classType);
            Object result = responseEntity.getBody();
            if (activity != null) {
                Runnable run = () -> {
                    alertDialog.dismiss();
                };
                activity.runOnUiThread(run);
            }
            getCallbackResponse().handleResponse(getRequestId(), false, false, result, getHelperObject());

        } catch (Exception ex) {
            if (activity != null) {
                Runnable run = () -> {
                    alertDialog.dismiss();
                };
                activity.runOnUiThread(run);
            }
            ex.printStackTrace();
            String errMessage = "Check your internet connectivity and retry";
            boolean isAPIException = false;
            if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException clientExp = (HttpClientErrorException) ex;
                errMessage = clientExp.getResponseBodyAsString();
                isAPIException = true;
            } else if (ex instanceof HttpServerErrorException) {
                HttpServerErrorException serverExp = (HttpServerErrorException) ex;
                errMessage = serverExp.getResponseBodyAsString();
                isAPIException = true;
            }
            getCallbackResponse().handleResponse(getRequestId(), true, isAPIException, errMessage, getHelperObject());
        }
    }
}
