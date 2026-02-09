package hr.abysalto.hiring.mid.restclient.productapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductReview(
        int rating,
        String comment,
        String date,
        String reviewerName,
        String reviewerEmail
) {}