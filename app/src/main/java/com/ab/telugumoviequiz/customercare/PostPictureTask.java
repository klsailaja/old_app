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

import java.util.Arrays;

public class PostPictureTask implements Runnable {
    private final String reqUri;
    private final int requestId;
    private CallbackResponse callbackResponse;

    private int reqTimeOut = 20 * 1000;

    private MultiValueMap<String, Object> formData;

    private Object helperObject;
    private Activity activity;
    private AlertDialog alertDialog;
    private String waitingMessage;
    private byte[] fileContents;

    public PostPictureTask(String reqUri, int reqId, MultiValueMap<String, Object> formData,
                           CallbackResponse callbackResponse) {
        this.reqUri = reqUri;
        this.requestId = reqId;
        this.formData = formData;
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

    public MultiValueMap<String, Object> getFormData() {
        return formData;
    }
    public void setFormData(MultiValueMap<String, Object> formData) {
        this.formData = formData;
    }

    public void setByteArray(byte[] fileContents) {
        this.fileContents = fileContents;
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
        String fileName = "test.txt";
        HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.TEXT_PLAIN);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(this.fileContents) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        final HttpEntity<ByteArrayResource> partsEntity = new HttpEntity<>(byteArrayResource, parts);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("user-file", partsEntity);


        // **************************
        //HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setContentType(MediaType.IMAGE_PNG);

        //HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                //formData, requestHeaders);
        try {
        // Create a new RestTemplate instance
        //RestTemplate restTemplate = getRestTemplate();
        RestTemplate restTemplate = new RestTemplate(true);
        // Make the network request, posting the message, expecting a String in response from the server
        System.out.println("B4 sending " + reqUri);
        System.out.println(formData);
        /*ResponseEntity<Boolean> response = restTemplate.exchange(reqUri, HttpMethod.POST, requestEntity,
                Boolean.class);*/
        final ResponseEntity<Boolean> exchange = restTemplate.exchange(getReqUri(), HttpMethod.POST,
                new HttpEntity<>(requestMap, headers), Boolean.class);
        System.out.println("response is " + exchange);

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
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }

}
