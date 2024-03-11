package org.keycloak.cli.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UriBuilder {

    private String endpoint;
    private Map<String, String> queryParams;

    public static UriBuilder create(String endpoint) {
        UriBuilder builder = new UriBuilder();
        builder.endpoint = endpoint;
        builder.queryParams = new HashMap<>();
        return builder;
    }

    public UriBuilder query(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    public URI toURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(endpoint);
        if (!queryParams.isEmpty()) {
            sb.append("?");

            Iterator<Map.Entry<String, String>> queryItr = queryParams.entrySet().iterator();
            while (queryItr.hasNext()) {
                Map.Entry<String, String> q = queryItr.next();
                if (q.getValue() != null) {
                    sb.append(URLEncoder.encode(q.getKey(), StandardCharsets.UTF_8));
                    sb.append("=");
                    sb.append(URLEncoder.encode(q.getValue(), StandardCharsets.UTF_8));
                    if (queryItr.hasNext()) {
                        sb.append("&");
                    }
                }
            }
        }
        try {
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
