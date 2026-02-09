package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.entity.Favorite;
import hr.abysalto.hiring.mid.entity.User;
import hr.abysalto.hiring.mid.repository.FavoriteRepository;
import hr.abysalto.hiring.mid.repository.UserRepository;
import hr.abysalto.hiring.mid.restclient.productapi.response.ProductResponse;
import hr.abysalto.hiring.mid.restclient.productapi.service.ProductApiService;
import hr.abysalto.hiring.mid.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductApiService productApiService;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long productId) {
        log.info("Adding favorite: userId={}, productId={}", userId, productId);

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            log.info("Favorite already exists: userId={}, productId={}", userId, productId);
            return;
        }

        productApiService.getProductById(productId.intValue());

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> {
                                      log.warn("User not found: userId={}", userId);
                                      return new IllegalArgumentException("User not found");
                                  });

        Favorite favorite = new Favorite(user, productId);
        favoriteRepository.save(favorite);

        log.info("Favorite added successfully: userId={}, productId={}", userId, productId);
    }

    @Override
    public List<ProductResponse> getUserFavorites(Long userId) {
        log.info("Fetching favorites for userId={}", userId);

        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<ProductResponse> products = new ArrayList<>();

        for (Favorite favorite : favorites) {
            try {
                ProductResponse product = productApiService.getProductById(favorite.getProductId().intValue());
                products.add(product);
            } catch (Exception e) {
                log.warn("Failed to fetch product {} for userId={}: {}",
                        favorite.getProductId(), userId, e.getMessage());
            }
        }

        log.info("Fetched {} favorite products for userId={}", products.size(), userId);
        return products;
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        log.info("Removing favorite: userId={}, productId={}", userId, productId);

        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            log.warn("Favorite not found: userId={}, productId={}", userId, productId);
            throw new IllegalArgumentException("Favorite not found");
        }

        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        log.info("Favorite removed successfully: userId={}, productId={}", userId, productId);
    }
}