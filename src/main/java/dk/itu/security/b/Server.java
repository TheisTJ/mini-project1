package dk.itu.security.b;


import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.ssl.SSLContexts;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;

public class Server {

    private static final int PORT = 7007;

    public static void main(String[] args) {
        try {
            start();
            System.out.println("server listening on port: %s%n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore jks = KeyStore.getInstance("JKS");
        jks.load(new FileInputStream("keystore.jks"), "changeit".toCharArray());

        SSLContexts.custom()
                .loadKeyMaterial(jks, "changeit".toCharArray())
                .build();

        HttpServer httpServer = ServerBootstrap.bootstrap()
                .setListenerPort(PORT)
                .setSslContext()
                .registerHandler("*", new MyHandler())
                .create();

        httpServer.start();
    }

    private static class MyHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws IOException {
            String uri = httpRequest.getRequestLine().getUri();
            System.out.println(Instant.now() + ": " + uri + " Start");
            httpResponse.setEntity(new StringEntity("kylling"));
        }
    }
}
