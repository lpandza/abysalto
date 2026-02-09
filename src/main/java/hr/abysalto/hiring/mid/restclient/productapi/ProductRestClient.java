package hr.abysalto.hiring.mid.restclient.productapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductRestClient {

    private final RestClient restClient;

    public ProductRestClient(@Value("${app.product-api.base-url}") String productApiBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(productApiBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public RestClient getRestClient() {
        return restClient;
    }
}