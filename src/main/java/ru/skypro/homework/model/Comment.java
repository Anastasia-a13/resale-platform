package ru.skypro.homework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skypro.homework.model.user.User;

/**
 * Сущность комментария к объявлению.
 * <p>
 * Хранит текст комментария, дату создания (timestamp в миллисекундах),
 * объявление и автора. Маппится на таблицу {@code comments}.
 * </p>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at")
    private Long createdAt = System.currentTimeMillis();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
