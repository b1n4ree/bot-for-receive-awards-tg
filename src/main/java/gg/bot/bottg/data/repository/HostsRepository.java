package gg.bot.bottg.data.repository;

import gg.bot.bottg.data.entity.Hosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostsRepository extends JpaRepository<Hosts, Long> {
}
