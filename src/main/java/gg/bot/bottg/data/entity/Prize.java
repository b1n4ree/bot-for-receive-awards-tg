package gg.bot.bottg.data.entity;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "prizes")
@Entity
@Data
public class Prize {

    @Id
    private Long id;

    @Column(name = "prize_day")
    private Long prizeDay;

    @Column(name = "prize_name")
    private String prizeName;
}