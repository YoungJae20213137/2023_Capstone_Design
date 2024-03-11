package ji.vegan.backend.bean.food;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(schema = "public", name = "review")
@Getter
@Setter
@RequiredArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "content")
    private String content;

    @Column(name = "image1")
    private String image1;

    @Column(name = "rating")
    private String rating;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", name = "advantages")
    private List<String> advantages;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", name = "disadvantage")
    private List<String> disadvantage;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime createdAt;

    @Version
    private long version;
}
