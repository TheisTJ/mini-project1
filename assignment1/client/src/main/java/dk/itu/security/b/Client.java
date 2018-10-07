package dk.itu.security.b;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class Client {

    public static void main(String[] args) {
        try {
            SSLContext context = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();

            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setSSLContext(context)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build();

            HttpGet request = new HttpGet("https://" + args[0] + ":" + args[1]);
            URI uri = new URIBuilder(request.getURI()).addParameter("message", args[2]).build();
            request.setURI(uri);

            CloseableHttpResponse response = httpClient.execute(request);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            bufferedReader.lines().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
