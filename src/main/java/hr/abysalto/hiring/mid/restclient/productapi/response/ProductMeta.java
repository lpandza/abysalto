package hr.abysalto.hiring.mid.restclient.productapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductMeta(
        String createdAt,
        String updatedAt,
        String barcode,
        String qrCode
) {}