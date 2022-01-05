package com.ab.telugumoviequiz.customercare;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.Utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class PostPictureTask implements Runnable {
    private final String reqUri;
    private final int requestId;
    private CallbackResponse callbackResponse;

    private int reqTimeOut = 20 * 1000;

    private Object helperObject;
    private Activity activity;
    private AlertDialog alertDialog;
    private String waitingMessage;
    private byte[] fileContents;
    private String filename;

    public PostPictureTask(String reqUri, int reqId,
                           CallbackResponse callbackResponse) {
        this.reqUri = reqUri;
        this.requestId = reqId;
        this.callbackResponse = callbackResponse;
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

    public void setReqTimeOut(int timeOut) {
        this.reqTimeOut = timeOut;
    }

    public void setHelperObject(Object helperObject) {
        this.helperObject = helperObject;
    }
    public Object getHelperObject() {
        return this.helperObject;
    }

    public void setByteArray(byte[] fileContents) {
        this.fileContents = fileContents;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public void run() {
        if (activity != null) {
            Runnable run = () -> {
                alertDialog = Utils.getProgressDialog(activity, waitingMessage);
                alertDialog.show();
            };
            activity.runOnUiThread(run);
        }
        HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.TEXT_PLAIN);
        String localFileName = filename;
        final ByteArrayResource byteArrayResource = new ByteArrayResource(this.fileContents) {
            @Override
            public String getFilename() {
                return localFileName;
            }
        };
        final HttpEntity<ByteArrayResource> partsEntity = new HttpEntity<>(byteArrayResource, parts);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("user-file", partsEntity);


        // **************************
        try {
            RestTemplate restTemplate = new RestTemplate(true);
            final ResponseEntity<Boolean> exchange = restTemplate.exchange(getReqUri(), HttpMethod.POST,
                    new HttpEntity<>(requestMap, headers), Boolean.class);
            Object result = exchange.getBody();
            if (activity != null) {
                Runnable run = () -> alertDialog.dismiss();
                activity.runOnUiThread(run);
            }
            getCallbackResponse().handleResponse(getRequestId(), false, false, result, getHelperObject());
        } catch (Exception ex) {
            if (activity != null) {
                Runnable run = () -> alertDialog.dismiss();
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

    protected ClientHttpRequestFactory getReqFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(reqTimeOut);
        return requestFactory;
    }

    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(getReqFactory());
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(
                MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }

}
