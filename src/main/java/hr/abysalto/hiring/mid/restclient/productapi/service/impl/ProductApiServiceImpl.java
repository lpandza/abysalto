package hr.abysalto.hiring.mid.restclient.productapi.service.impl;

import hr.abysalto.hiring.mid.restclient.productapi.ProductRestClient;
import hr.abysalto.hiring.mid.restclient.productapi.exception.ProductApiException;
import hr.abysalto.hiring.mid.restclient.productapi.request.ProductSearchRequest;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductListResponse;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductResponse;
import hr.abysalto.hiring.mid.restclient.productapi.service.ProductApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class ProductApiServiceImpl implements ProductApiService {

    private static final String PRODUCTS_PATH = "/products";
    private static final String PRODUCT_BY_ID_PATH = "/products/{id}";

    private final RestClient restClient;

    public ProductApiServiceImpl(ProductRestClient productRestClient) {
        this.restClient = productRestClient.getRestClient();
    }

    @Override
    public ProductListResponse getAllProducts(ProductSearchRequest request) {
        log.info("Fetching all products with params: limit={}, skip={}, sortBy={}, order={}",
                request.limit(), request.skip(), request.sortBy(), request.order());

        String uri = buildProductListUri(request);

        ProductListResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("Error fetching products: HTTP {}", res.getStatusCode().value());
                    throw new ProductApiException(
                            "Failed to fetch products from API",
                            res.getStatusCode().value()
                    );
                })
                .body(ProductListResponse.class);

        if (response == null) {
            throw new ProductApiException("Received null response from Product API");
        }

        log.info("Successfully fetched {} products (total: {})",
                response.products().size(), response.total());
        return response;
    }

    @Override
    public ProductResponse getProductById(int id) {
        log.info("Fetching product by ID: {}", id);

        ProductResponse response = restClient.get()
                .uri(PRODUCT_BY_ID_PATH, id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("Error fetching product {}: HTTP {}", id, res.getStatusCode().value());
                    throw new ProductApiException(
                            "Failed to fetch product with ID " + id,
                            res.getStatusCode().value()
                    );
                })
                .body(ProductResponse.class);

        if (response == null) {
            throw new ProductApiException("Received null response for product ID " + id);
        }

        log.info("Successfully fetched product: {} (ID: {})", response.title(), id);
        return response;
    }

    private String buildProductListUri(ProductSearchRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PRODUCTS_PATH);

        if (request.limit() != null) {
            builder.queryParam("limit", request.limit());
        }
        if (request.skip() != null) {
            builder.queryParam("skip", request.skip());
        }
        if (request.sortBy() != null) {
            builder.queryParam("sortBy", request.sortBy());
        }
        if (request.order() != null) {
            builder.queryParam("order", request.order());
        }
        if (request.select() != null) {
            builder.queryParam("select", request.select());
        }

        return builder.toUriString();
    }
}