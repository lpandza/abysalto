package hr.abysalto.hiring.mid.restclient.productapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductListResponse(
        List<ProductResponse> products,
        int total,
        int skip,
        int limit
) {}