package auth2.auth2.accounts;

import auth2.auth2.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class connectAccountsService {
    @Autowired
    ConnectedAccountsRepo connectedAccountsRepo;

 public void createAccount (String token, int expiresIn, String provider, String sub, Users users,String account_name) throws Exception {
     ConnectedAccounts connectedAccounts = new ConnectedAccounts();
     connectedAccounts.setAccessToken(token);
     connectedAccounts.setProvider(provider);
     connectedAccounts.setSub(sub);
      if(expiresIn!=-1) {
          connectedAccounts.setExpiredAt(Instant.now().plusSeconds(expiresIn));
      } else{
          connectedAccounts.setExpiredAt(null);
      }
     connectedAccounts.setUser(users);
     connectedAccounts.setAccount_name(account_name);
     connectedAccountsRepo.save(connectedAccounts);

 }
    public void createAccount (String token, int expiresIn, String provider, String sub, Users users) throws Exception {
        ConnectedAccounts connectedAccounts = new ConnectedAccounts();
        connectedAccounts.setAccessToken(token);
        connectedAccounts.setProvider(provider);
        connectedAccounts.setSub(sub);
        if(expiresIn!=-1) {
            connectedAccounts.setExpiredAt(Instant.now().plusSeconds(expiresIn));
        } else{
            connectedAccounts.setExpiredAt(null);
        }
        connectedAccounts.setUser(users);
        connectedAccountsRepo.save(connectedAccounts);

    }


}
