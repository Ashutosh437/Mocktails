package auth2.auth2.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConnectedAccountsRepo extends JpaRepository<ConnectedAccounts,Integer> {

    public Optional<ConnectedAccounts> findByIdAndProvider(int id, String provider);
}
