package org.keycloak.cli.web;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private Socket socket;
    private String method;
    private String path;
    private String protocol;
    private Map<String, String> queryParams;
    private Map<String, String> headerParams;
    private Map<String, String> bodyParams;

    public HttpRequest(Socket socket) throws IOException {
        this.socket = socket;
        socket.setSoTimeout(10000);

        HttpRequestReader reader = new HttpRequestReader(socket.getInputStream());

        String[] firstLine = reader.readFirstLine();
        if (firstLine != null) {
            method = firstLine[0];
            path = firstLine[1];
            protocol = firstLine[2];

            queryParams = new HashMap<>();
            headerParams = new HashMap<>();

            int queryIndex = path.indexOf('?');
            if (queryIndex > 0) {
                String[] rawQuery = path.substring(queryIndex + 1).split("&");
                path = path.substring(0, queryIndex);

                for (String query : rawQuery) {
                    String[] split = query.split("=");
                    queryParams.put(URLDecoder.decode(split[0], StandardCharsets.UTF_8), URLDecoder.decode(split[1], StandardCharsets.UTF_8));
                }
            }

            headerParams = reader.readHeaders();

            if (method.equals(HttpMethods.POST)) {
                readBodyIfAvailable(reader);
            }
        }
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    public void ok(byte[] body, MimeType contentType) throws IOException {
        HttpResponse.ok(body, contentType).send(socket);
    }

    public void badRequest() throws IOException {
        HttpResponse.badRequest().send(socket);
    }

    private void readBodyIfAvailable(HttpRequestReader reader) throws IOException {
        String contentType = getHeaderParams().get(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && contentType.startsWith(MimeType.FORM.toString())) {
            String contentLengthString = getHeaderParams().get(HttpHeaders.CONTENT_LENGTH);
            Integer contentLength = contentLengthString != null ? Integer.parseInt(contentLengthString) : null;
            if (contentLength != null) {
                bodyParams = new HashMap<>();
                String body = reader.readBody(contentLength);
                for (String p : body.split("&")) {
                    String[] split = p.split("=");
                    String key = URLDecoder.decode(split[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(split[1], StandardCharsets.UTF_8);
                    bodyParams.put(key, value);
                }
            }
        }
    }

}
