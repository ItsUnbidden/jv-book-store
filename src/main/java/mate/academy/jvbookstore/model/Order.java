package mate.academy.jvbookstore.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "orders")
@Data
@SQLDelete(sql = "UPDATE orders SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted <> true")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Status status;
    
    @Column(nullable = false)
    private BigDecimal total;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private String shippingAddress;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderItem> orderItems;
    
    @Column(nullable = false)
    private boolean isDeleted;

    public Order() {
        orderItems = new HashSet<>();
    }
    
    public static enum Status {
        COMPLETED,
        PENDING,
        DELIVERED,
        CANCELED,
        CREATED
    }
}
