package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    final Lock lock = new ReentrantLock();
    private Integer id;
    private Currency currency;
    private LocalDateTime openingDate;
    private Double balance;
    private Integer bankId;
    private Integer appUserId;
}
