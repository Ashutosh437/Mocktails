package auth2.auth2.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users,Integer> {

    Optional<Users> findByUserEmail(String userEmail);

    Optional<Users> findByUserName(String userName);

    boolean existsByUserEmail(String userEmail);
}

