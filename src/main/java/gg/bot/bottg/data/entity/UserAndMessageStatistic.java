package gg.bot.bottg.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Table(name = "user_and_message_statistic")
@Entity
@Data
public class UserAndMessageStatistic {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "day")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDate day;

    @Column(name = "count_message_per_day")
    private Long countMessagePerDay;

    @Column(name = "count_user_usage_per_day")
    private Long countUserUsagePerDay;

    @Column(name = "receive_prize_per_day")
    private Long receivePrizePerDay;
}
