package ru.practicum.shareit.booking.model;

import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "bookings")
public class Booking {
    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private LocalDateTime start;
    @Column
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status{
        WARRING,
        APPROVED,
        REJECTED,
        CANCELED
    }
    
}
