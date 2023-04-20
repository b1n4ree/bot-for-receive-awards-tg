package gg.bot.bottg.data.repository;

import gg.bot.bottg.data.entity.UserAndMessageStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAndMessageStatisticRepository extends JpaRepository<UserAndMessageStatistic, Long> {
}
