package hr.abysalto.hiring.mid.restclient.productapi.service;

import hr.abysalto.hiring.mid.restclient.productapi.request.ProductSearchRequest;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductListResponse;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductResponse;

public interface ProductApiService {

    ProductListResponse getAllProducts(ProductSearchRequest request);

    ProductResponse getProductById(int id);
}