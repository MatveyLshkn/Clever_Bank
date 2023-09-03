package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    final Lock lock = new ReentrantLock();
    private Integer id;
    private LocalDateTime date;
    private TransactionType type;
    private Optional<Integer> receiverAccId;
    private Optional<Integer> senderAccId;
    private Double total;
}
