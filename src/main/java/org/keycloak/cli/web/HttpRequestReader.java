package org.keycloak.cli.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestReader {

    private InputStream is;
    private InputStreamReader reader;

    public HttpRequestReader(InputStream inputStream) {
        this.is = inputStream;
        this.reader = new InputStreamReader(is);
    }

    public String[] readFirstLine() throws IOException {
        String firstLine = readLine();
        return firstLine.split(" ");
    }

    public Map<String, String> readHeaders() throws IOException {
        Map<String, String> headers = new HashMap<>();
        for (String l = readLine(); l != null && !l.equals(""); l = readLine()) {
            String[] s = l.split(": ");
            headers.put(s[0], s[1]);
        }
        return headers;
    }

    public String readBody(int contentLength) throws IOException {
        char[] chars = new char[contentLength];
        for (int i = 0; i < contentLength; i++) {
            char c = (char) reader.read();
            if (c == -1) {
                break;
            }
            chars[i] = c;
        }
        String body = new String(chars);
        return body;
    }

    private String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = (char) reader.read();
            if (c == '\r') {
                // ignore
            } else if (c == '\n') {
                return sb.toString();
            } else {
                sb.append(c);
            }
        }
    }

}
