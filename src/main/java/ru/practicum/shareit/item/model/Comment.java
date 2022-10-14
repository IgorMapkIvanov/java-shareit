package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;

    @Column(name = "text", nullable = false, length = 500)
    private String text;

    @ManyToOne(targetEntity = User.class, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    private User authorName;

    @ManyToOne(targetEntity = Item.class, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    private Item item;

    @Column(name = "created")
    private LocalDateTime created;
}
