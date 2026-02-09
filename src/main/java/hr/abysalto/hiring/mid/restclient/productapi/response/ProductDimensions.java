package hr.abysalto.hiring.mid.restclient.productapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDimensions(
        double width,
        double height,
        double depth
) {}