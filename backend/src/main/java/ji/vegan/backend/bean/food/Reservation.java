package ji.vegan.backend.bean.food;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import ji.vegan.backend.bean.user.User;
import ji.vegan.backend.util.NullSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(schema = "public", name = "reservation")
@Getter
@Setter
@RequiredArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "person")
    private int person;

    @Column(name = "option")
    @JsonSerialize(nullsUsing = NullSerializer.class)
    private String option;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime createdAt;
}
