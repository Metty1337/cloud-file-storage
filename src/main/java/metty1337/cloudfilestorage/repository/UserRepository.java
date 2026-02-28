package metty1337.cloudfilestorage.repository;

import metty1337.cloudfilestorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
