package dev.gabriel.idempotent_payment_gateway.model.entities;

import dev.gabriel.idempotent_payment_gateway.model.enums.EntryType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "entry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private BankTransaction transaction;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private BankAccount account;

    @Column(nullable = false)
    private BigDecimal amount; // positive = credit, negative = debit

    @Enumerated(EnumType.STRING)
    private EntryType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
