package org.keycloak.cli.web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BasicWebServer {

    private ServerSocket serverSocket;

    public static BasicWebServer start() {
        BasicWebServer webServer = new BasicWebServer();
        try {
            webServer.serverSocket = new ServerSocket(0, 10, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return webServer;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public HttpRequest accept() throws IOException {
        Socket socket = serverSocket.accept();
        return new HttpRequest(socket);
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

}
