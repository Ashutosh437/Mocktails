package auth2.auth2.Jwt;

import auth2.auth2.users.Users;
import auth2.auth2.users.UsersRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    jwtService jwtservice;
    @Autowired
    UsersRepo usersRepo;



    @Override
  protected void doFilterInternal
      (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("Path: " + request.getRequestURI());
        System.out.println("Auth header: " + request.getHeader("Authorization"));

        String path = request.getRequestURI();
        if (path.startsWith("/oauth2/callback")) {
            filterChain.doFilter(request, response);
            return;
        }


        String requestToken = request.getHeader("Authorization");

        if(requestToken==null||!requestToken.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        assert requestToken != null;
        String token = requestToken.substring(7);

        String UserEmail = jwtservice.extractUsername(token);
        if(UserEmail!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
            Optional<Users> users = usersRepo.findByUserEmail(UserEmail);
                 if(users.isPresent()){
        SecurityContextHolder.getContext().setAuthentication
                        (new UsernamePasswordAuthenticationToken
                                (users.get(),null,users.get().getAuthorities()));
        }
                 }

        filterChain.doFilter(request,response);

    }


}
