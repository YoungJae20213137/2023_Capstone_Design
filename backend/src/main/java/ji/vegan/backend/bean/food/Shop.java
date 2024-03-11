package ji.vegan.backend.bean.food;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import ji.vegan.backend.util.DateUtils;
import ji.vegan.backend.util.NullSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(schema = "public", name = "shop")
@Getter
@Setter
@RequiredArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image1")
    private String image1;

    @Column(name = "image2")
    private String image2;

    @Column(name = "rating")
    private String rating;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "tags")
    private String tags;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", name = "times")
    private List<String> times;
    @Column(name = "description")
    @JsonSerialize(nullsUsing = NullSerializer.class)
    private String description;
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime createdAt;
    @Version
    private long version;

    @Transient
    public boolean isShopOpen() {
        String[] before = StringUtils.split(times.get(0), ':');
        String[] after = StringUtils.split(times.get(1), ':');

        LocalTime startTime = LocalTime.of(Integer.parseInt(before[0]), Integer.parseInt(before[1]));
        LocalTime endTime = LocalTime.of(Integer.parseInt(after[0]), Integer.parseInt(after[1]));

        return DateUtils.isDateRange(LocalTime.now(), startTime, endTime);
    }
}
