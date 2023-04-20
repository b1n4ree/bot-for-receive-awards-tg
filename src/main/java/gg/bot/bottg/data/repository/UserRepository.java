package gg.bot.bottg.data.repository;

import gg.bot.bottg.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> getUserByTelegramId(Long telegramId);
    public Optional<User> getUserByGizmoId(Long gizmoId);
    public Optional<List<User>> getUsersByGizmoName(String gizmoName);
    public Optional<List<User>> getUsersByAuthorizationInGizmoAccount(Boolean isAuth);
}
