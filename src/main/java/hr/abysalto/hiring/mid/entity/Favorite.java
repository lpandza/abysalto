package hr.abysalto.hiring.mid.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Favorite {
    @EmbeddedId
    private FavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "product_id", nullable = false, insertable = false, updatable = false)
    private Long productId;

    public Favorite(User user, Long productId) {
        this.user = user;
        this.productId = productId;
        this.id = new FavoriteId(user.getId(), productId);
    }
}
