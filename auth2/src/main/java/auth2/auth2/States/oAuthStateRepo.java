package auth2.auth2.States;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface oAuthStateRepo extends JpaRepository<oAuthState,String>{

}
