package my.project.ChatHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import my.project.ChatHub.entity.Users;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <Users, Long> {

    Users findByEmail(String email);

}
