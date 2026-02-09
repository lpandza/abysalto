package hr.abysalto.hiring.mid.controller;

import hr.abysalto.hiring.mid.entity.User;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductResponse;
import hr.abysalto.hiring.mid.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Favorites", description = "User favorite products endpoints")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "Add product to favorites",
            responses = {
                    @ApiResponse(description = "Product added to favorites", responseCode = "201"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Product not found", responseCode = "404")
            }
    )
    @PostMapping("/{productId}")
    public ResponseEntity<Void> addToFavorites(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId
    ) {
        favoriteService.addFavorite(user.getId(), productId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Get user's favorite products",
            responses = {
                    @ApiResponse(
                            description = "Favorite products retrieved successfully",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401")
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getUserFavorites(
            @AuthenticationPrincipal User user
    ) {
        List<ProductResponse> favorites = favoriteService.getUserFavorites(user.getId());
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "Remove product from favorites",
            responses = {
                    @ApiResponse(description = "Product removed from favorites", responseCode = "204"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Favorite not found", responseCode = "409")
            }
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromFavorites(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId
    ) {
        favoriteService.removeFavorite(user.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}