package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.http.HTTPRequestSender;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.http.ReadOnlyHTTPRequest;
import com.nimbusds.oauth2.sdk.http.ReadOnlyHTTPResponse;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class NimbusApacheHttpClient implements HTTPRequestSender {

    private final CloseableHttpClient httpclient = HttpClients.createDefault();

    @Override
    public ReadOnlyHTTPResponse send(final ReadOnlyHTTPRequest httpRequest)
            throws IOException {

        RequestBuilder builder;
        switch (httpRequest.getMethod()) {
            case GET:
                builder = RequestBuilder.get();
                break;
            case POST:
                builder = RequestBuilder.post();
                break;
            case PUT:
                builder = RequestBuilder.put();
                break;
            case DELETE:
                builder = RequestBuilder.delete();
                break;
            default:
                throw new IOException("Unsupported HTTP method: " +
                        httpRequest.getMethod());
        }
        builder.setUri(httpRequest.getURI());

        for (Map.Entry<String, List<String>> en : httpRequest.getHeaderMap().entrySet()) {
            String headerName = en.getKey();
            List<String> headerValues = en.getValue();
            if (CollectionUtils.isEmpty(headerValues)) {
                continue; // no header values, skip header
            }
            for (String headerValue : headerValues) {
                builder.setHeader(headerName, headerValue);
            }
        }

        if (httpRequest.getBody() != null) {
            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(
                    new ByteArrayInputStream(
                            httpRequest.getBody().getBytes(StandardCharsets.UTF_8)
                    )
            );
            builder.setEntity(entity);
        }

        HttpUriRequest request = builder.build();

        CloseableHttpResponse response = httpclient.execute(request);

        StatusLine statusLine = response.getStatusLine();

        HTTPResponse httpResponse = new HTTPResponse(statusLine.getStatusCode());
        httpResponse.setStatusMessage(statusLine.getReasonPhrase());

        for (Header header : response.getAllHeaders()) {
            httpResponse.setHeader(header.getName(), header.getValue());
        }

        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            String body = EntityUtils.toString(httpEntity);
            if (StringUtils.isNotBlank(body)) {
                httpResponse.setBody(body);
            }
        }
        return httpResponse;
    }
}
