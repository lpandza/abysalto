package hr.abysalto.hiring.mid.restclient.productapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductResponse(
        int id,
        String title,
        String description,
        String category,
        double price,
        double discountPercentage,
        double rating,
        int stock,
        List<String> tags,
        String brand,
        String sku,
        int weight,
        ProductDimensions dimensions,
        String warrantyInformation,
        String shippingInformation,
        String availabilityStatus,
        List<ProductReview> reviews,
        String returnPolicy,
        int minimumOrderQuantity,
        ProductMeta meta,
        List<String> images,
        String thumbnail
) {}