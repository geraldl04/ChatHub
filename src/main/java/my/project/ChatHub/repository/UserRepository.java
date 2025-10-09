package my.project.ChatHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import my.project.ChatHub.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    User findByEmail(String email);

}
