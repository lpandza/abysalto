package hr.abysalto.hiring.mid.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;
}
