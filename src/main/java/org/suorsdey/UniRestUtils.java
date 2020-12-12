package org.suorsdey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.HashMap;

public class UniRestUtils<T> {

    private static UniRestUtils SINGLE_INSTANCE = null;

    public static UniRestUtils getInstance() {
        if (SINGLE_INSTANCE == null) {
            synchronized (UniRestUtils.class) {
                if (SINGLE_INSTANCE == null) {
                    SINGLE_INSTANCE = new UniRestUtils();
                }
            }
        }

        Unirest.setObjectMapper(new ObjectMapper() {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

            public String writeValue(Object value) {
                try {
                    return mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {

                    return mapper.readValue(value, valueType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        return SINGLE_INSTANCE;
    }

    public T get(T newInstance,String url) {
        try {
            HttpResponse<T> response = (HttpResponse<T>) Unirest.get(url).asObject(newInstance.getClass());
            if (response.getStatus() == 200) {
                return response.getBody();
            }
            return null;
        } catch (
                UnirestException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public T get(T newInstance, String url, String basicAuthUsername, String basicAuthPassword, HashMap<String, String> headerParameters) {
        try {
            if (headerParameters == null) {
                headerParameters = new HashMap<>();
            }
            headerParameters.put("Content-Type", "application/json");
            HttpResponse<T> response = (HttpResponse<T>) Unirest.get(url).headers(headerParameters).basicAuth(basicAuthUsername, basicAuthPassword).asObject(newInstance.getClass());
            if (response.getStatus() == 200) {
                return response.getBody();
            }
            return null;
        } catch (
                UnirestException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public T post(T requestObjectBody, String url, String basicAuthUsername, String basicAuthPassword, HashMap<String, String> headerParameters) {
        try {
            if (headerParameters == null) {
                headerParameters = new HashMap<>();
            }
            headerParameters.put("Content-Type", "application/json");
            HttpResponse<T> response = (HttpResponse<T>) Unirest.post(url).basicAuth(basicAuthUsername, basicAuthPassword).headers(headerParameters).body(AppUtils.getInstance().toJsonString(requestObjectBody)).asObject(requestObjectBody.getClass());
            if (response.getStatus() == 200) {
                return response.getBody();
            }
            return null;
        } catch (
                UnirestException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
