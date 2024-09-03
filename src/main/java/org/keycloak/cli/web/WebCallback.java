package org.keycloak.cli.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class WebCallback extends AbstractVerticle {

    private HttpServer httpServer = null;

    private final CompletableFuture<String> callback = new CompletableFuture<>();

    @Inject
    Vertx vertx;

    public void start() {
        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            String path = request.path();
            HttpServerResponse response = request.response();

            if (path.equals("/favicon.ico")) {
                send(response, "/favicon.ico", "image/x-icon");
            } else if (path.equals("/callback")) {
                send(response, "/callback.html", "text/html");
                callback.complete(request.query());
            } else {
                response.setStatusCode(404);
                response.end();
            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        httpServer.listen(0, "127.0.0.1").onComplete(event -> latch.countDown());
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return httpServer.actualPort();
    }

    public Map<String, String> getCallback() throws InterruptedException, ExecutionException, TimeoutException {
        String queryString = callback.get(30, TimeUnit.SECONDS);
        Map<String, String> query = new HashMap<>();
        for (String q : queryString.split("&")) {
            String[] s = q.split("=");
            query.put(s[0], s[1]);
        }
        return query;
    }

    private void send(HttpServerResponse response, String resource, String resourceType) {
        try (InputStream is = getClass().getResource(resource).openStream()) {
            byte[] body = is.readAllBytes();
            response.headers()
                    .add("Content-Length", String.valueOf(body.length))
                    .add("Content-Type", resourceType);
            response.write(Buffer.buffer(body));
            response.end();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
