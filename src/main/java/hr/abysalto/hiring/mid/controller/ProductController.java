package hr.abysalto.hiring.mid.controller;

import hr.abysalto.hiring.mid.restclient.productapi.request.ProductSearchRequest;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductListResponse;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductResponse;
import hr.abysalto.hiring.mid.restclient.productapi.service.ProductApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products", description = "Product browsing endpoints")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApiService productApiService;

    @Operation(
            summary = "Get all products",
            responses = {
                    @ApiResponse(
                            description = "Products retrieved successfully",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductListResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "External API error", responseCode = "503")
            }
    )
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String select
    ) {
        ProductSearchRequest request = new ProductSearchRequest(limit, skip, sortBy, order, select);
        ProductListResponse response = productApiService.getAllProducts(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get product by ID",
            responses = {
                    @ApiResponse(
                            description = "Product retrieved successfully",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Product not found", responseCode = "404")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable int id) {
        ProductResponse response = productApiService.getProductById(id);
        return ResponseEntity.ok(response);
    }


}