package com.qaprosoft.zafira.log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MetaInfoMessage {

    /*private static final Pattern stringMapMessagePattern = Pattern.compile("(?<=#message@).+(?=#)");
    private static final Pattern stringMapHeadersPattern = Pattern.compile("(?<=#headers@\\{).+(?=})");
    private static final Pattern stringMapHeaderKeyPattern = Pattern.compile(".+(?=:)");
    private static final Pattern stringMapHeaderValuePattern = Pattern.compile("(?<=:).+");*/

    private String message;
    private Map<String, String> headers;

    public MetaInfoMessage() {
        this.headers = new HashMap<>();
    }

    public MetaInfoMessage addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public MetaInfoMessage addMessage(String message) {
        this.message = message;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /*@Override
    public String toString() {
        return "#message@" + this.message + "#headers@{" + this.headers.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(";")) + "}";
    }

    public static MetaInfoMessage toMetaInfo(String stringObject) {
        MetaInfoMessage metaInfoMessage = null;
        Matcher messageMatcher = stringMapMessagePattern.matcher(stringObject);
        if(messageMatcher.find()) {
            new MetaInfoMessage(messageMatcher.group());
            Matcher headersMatcher = stringMapHeadersPattern.matcher(stringObject);
            if (headersMatcher.find()) {
                String headers = headersMatcher.group();
                Arrays.stream(headers.split(";")).forEach(header -> {
                    Matcher keyMatcher = stringMapHeaderKeyPattern.matcher(header);
                    Matcher valueMatcher = stringMapHeaderValuePattern.matcher(header);
                    if(keyMatcher.find() && valueMatcher.find()) {
                        metaInfoMessage.getHeaders().put(keyMatcher.group(), valueMatcher.group());
                    }
                });
                System.out.println();
            }
        }
        return metaInfoMessage;
    }*/
}
