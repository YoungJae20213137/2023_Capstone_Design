package ji.vegan.backend.bean.food;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import ji.vegan.backend.util.NullSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;

@Entity
@Table(schema = "public", name = "menu")
@Getter
@Setter
@RequiredArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "image1")
    private String image1;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", name = "categories")
    private ObjectNode categories;

    @Column(name = "description")
    @JsonSerialize(nullsUsing = NullSerializer.class)
    private String description;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime createdAt;

    @Column(name = "tags")
    private String tags;

    @Version
    private long version;
}
