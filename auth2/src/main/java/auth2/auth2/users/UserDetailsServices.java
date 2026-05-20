package auth2.auth2.users;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServices implements UserDetailsService {

   @Autowired
    UsersRepo usersRepo;
    @Override
    public UserDetails loadUserByUsername(@NonNull String Email) throws UsernameNotFoundException {
          return usersRepo.findByUserEmail(Email).
                  orElseThrow(()->new UsernameNotFoundException("not found user "+Email));
    }
}
