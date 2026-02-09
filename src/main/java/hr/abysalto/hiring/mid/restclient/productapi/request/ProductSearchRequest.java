package hr.abysalto.hiring.mid.restclient.productapi.request;

public record ProductSearchRequest(
        Integer limit,
        Integer skip,
        String sortBy,
        String order,
        String select
) {
    public ProductSearchRequest() {
        this(null, null, null, null, null);
    }
}