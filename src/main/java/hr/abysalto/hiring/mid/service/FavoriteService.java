package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.restclient.productapi.response.ProductResponse;

import java.util.List;

public interface FavoriteService {
    void addFavorite(Long userId, Long productId);

    List<ProductResponse> getUserFavorites(Long userId);

    void removeFavorite(Long userId, Long productId);
}
