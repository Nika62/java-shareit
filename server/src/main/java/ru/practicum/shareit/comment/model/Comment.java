package ru.practicum.shareit.comment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotBlank
    private String text;
    @ManyToOne()
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "author_id")
    private User author;
    @Column
    private LocalDateTime created;

    public Comment(String text, Item item, User author, LocalDateTime created) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }
}
