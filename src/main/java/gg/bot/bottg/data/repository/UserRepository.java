package gg.bot.bottg.data.repository;

import gg.bot.bottg.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByTelegramId(Long telegramId);
    Optional<User> getUserByGizmoId(Long gizmoId);
    Optional<List<User>> getUsersByGizmoName(String gizmoName);
    Optional<List<User>> getUsersByAuthorizationInGizmoAccount(Boolean isAuth);
    Optional<User> getUserByGizmoName(String gizmoName);
    Optional<List<User>> getUsersByIsEnterPromo(Boolean isEnter);
}
