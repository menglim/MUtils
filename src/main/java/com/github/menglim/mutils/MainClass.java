package com.github.menglim.mutils;

import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class MainClass {

    private static int TIMEOUT = 3000;

    public static void main(String[] args) {
        String testUser = processToServer(HttpMethod.GET, null, "https://reqres.in/api/users?page=2", null);
        System.out.println("=> " + testUser);
    }

    public static String processToServer(HttpMethod httpMethod, String payload, String url, HashMap<String, String> headerParameters) {
        String urlForLog = url;
        try {
            Unirest.config().reset();
            Unirest.config().connectTimeout(10);
            Unirest.config().socketTimeout(10);
            HttpResponse<String> response = null;

            urlForLog = urlForLog.replaceAll("[paygo24.com/api/pre_pay?sid=]@[\\s\\S]*$", "=******");
            if (HttpMethod.GET.equals(httpMethod)) {
                log.info(httpMethod.name() + " to " + urlForLog);
                response = Unirest.get(url).headers(headerParameters).asString();

            } else if (HttpMethod.PUT.equals(httpMethod)) {
                String logPayloadPut = payload.replaceFirst("(?s)<web:cm_password[^>]*>.*?</web:cm_password>", "<web:cm_password>*****</web:cm_password>");
                logPayloadPut = logPayloadPut.replaceFirst("(?s)<cm_password[^>]*>.*?</cm_password>", "<cm_password>*****</cm_password>");
                log.info(httpMethod.name() + " to " + urlForLog + " with body => " + logPayloadPut);
                response = Unirest.put(url).headers(headerParameters).body(payload).asString();
            } else if (HttpMethod.HEAD.equals(httpMethod)) {
            } else if (HttpMethod.POST.equals(httpMethod)) {
                String logPayload = payload.replaceFirst("(?s)<web:cm_password[^>]*>.*?</web:cm_password>", "<web:cm_password>*****</web:cm_password>");
                logPayload = logPayload.replaceFirst("(?s)<cm_password[^>]*>.*?</cm_password>", "<cm_password>*****</cm_password>");
                log.info(httpMethod.name() + " to " + urlForLog + " with body => " + logPayload);
                response = Unirest.post(url).headers(headerParameters).body(payload).asString();
            } else if (HttpMethod.PATCH.equals(httpMethod)) {
            } else if (HttpMethod.DELETE.equals(httpMethod)) {
                log.info(httpMethod.name() + " to " + urlForLog);
                response = Unirest.delete(url).asString();
            } else if (HttpMethod.OPTIONS.equals(httpMethod)) {
            }
            if (response == null) {
                log.error("response is NULL");
                return null;
            }
            String jsonResponse = response.getBody();
            String jsonResponseForLog = jsonResponse;
            if (AppUtils.getInstance().nonNull(jsonResponseForLog)) {
                jsonResponseForLog = jsonResponseForLog.replaceAll("(?s)<tran:Specific[^>]*>.*?</tran:Specific>", "<tran:Specific><tran:CreateVirtualCard Cvv2=\"*\"/></tran:Specific>");
            }
            if (response.getStatus() != 200) {
                log.error("HttpStatus = " + response.getStatus() + " with raw response => " + jsonResponseForLog);
            } else {
                log.info("HttpStatus = " + response.getStatus() + " with raw response => " + jsonResponseForLog);
            }
            return jsonResponse;
        } catch (UnirestException var7) {
//            new TelegramSendMessageExecutor(null, urlForLog + "|" + var7.getMessage()).run();
            var7.printStackTrace();
        }
        return null;
    }
}
