package gg.bot.bottg.data.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hosts")
@Data
public class Hosts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "host_number")
    private Long hostNumber;

    @Column(name = "host_id")
    private Long hostId;

    @Column(name = "host_group")
    private String hostGroup;

    @Column(name = "time")
    private String time = "свободен";
}
