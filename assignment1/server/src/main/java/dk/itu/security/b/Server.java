package dk.itu.security.b;


import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.time.Instant;
import java.util.List;

public class Server {

    private static final int PORT = 7007;

    public static void main(String[] args) {
        try {
            start();
            System.out.format("Server listening on port: %s%n", PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start() throws Exception {
        KeyStore jks = KeyStore.getInstance("JKS");
        InputStream inputStream = Server.class.getClassLoader().getResource("keystore.jks").openStream();
        jks.load(inputStream, "changeit".toCharArray());
        inputStream.close();

        SSLContext context = SSLContexts.custom()
                .loadKeyMaterial(jks, "changeit".toCharArray())
                .build();

        HttpServer httpServer = ServerBootstrap.bootstrap()
                .setListenerPort(PORT)
                .setSslContext(context)
                .registerHandler("*", new MyHandler())
                .create();

        httpServer.start();
    }

    private static class MyHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) {
            try {
                RequestLine request = httpRequest.getRequestLine();
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(request.getUri()), HTTP.UTF_8);

                System.out.format("%s - %s: %s Params: %s%n%n", Instant.now(), request.getMethod(), request.getUri(), params);
                httpResponse.setEntity(new StringEntity(params.get(0).getValue()));
            } catch (URISyntaxException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
