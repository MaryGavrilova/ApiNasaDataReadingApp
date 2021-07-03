import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=f3Saq98y6Bp1viIyqpt03PlOVFgHl1lzShMBP5M3";

    public static void main(String[] args) {
        try {
            CloseableHttpClient httpClient = createHttpClient();
            HttpGet request1 = new HttpGet(REMOTE_SERVICE_URI);
            CloseableHttpResponse response1 = httpClient.execute(request1);

            ObjectMapper mapper = new ObjectMapper();
            AnswerFromNasa answer = mapper.readValue(response1.getEntity().getContent(), AnswerFromNasa.class);
            System.out.println(answer);

            String imageUri = answer.getUrl();
            System.out.println(imageUri);
            int index = imageUri.lastIndexOf("/");
            String fileName = imageUri.substring(index + 1);
            System.out.println(fileName);

            HttpGet request2 = new HttpGet(imageUri);
            CloseableHttpResponse response2 = httpClient.execute(request2);
            HttpEntity entity = response2.getEntity();
            if (entity != null) {
                FileOutputStream fos = new FileOutputStream("/Users/gavri/ApiNasaDataReadingApp/" + fileName);
                entity.writeTo(fos);
                fos.close();
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static CloseableHttpClient createHttpClient() {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
        return httpClient;
    }
}
