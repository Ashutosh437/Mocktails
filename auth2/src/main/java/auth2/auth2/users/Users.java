package auth2.auth2.users;

import auth2.auth2.accounts.ConnectedAccounts;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
public class Users implements UserDetails {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;


    private String userPassword;

    private String userName;

    @Enumerated(EnumType.STRING)
    private UsersRoles userRole;

    @Column(nullable = false,unique = true)
    private String userEmail;

    @OneToMany( mappedBy = "user")
    @JsonIgnore
    private List<ConnectedAccounts> userAccounts;


    public Users( List<ConnectedAccounts> userAccounts,int id, String userName, String userPassword, UsersRoles user_Role, String userEmail) {
        this.id = id;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRole = user_Role;
        this.userEmail = userEmail;
        this.userAccounts = userAccounts;
    }

    public Users() {

    }
    public String getUserEmail(){ return userEmail;}

    public void setUserEmail(String User_email){this.userEmail =User_email;}

    public UsersRoles getUserRole() {
        return userRole;
    }

    public void setUserRole(UsersRoles userRole) {
        this.userRole = userRole;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public  String getUserName() {return this.userName;}

    public List<ConnectedAccounts> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<ConnectedAccounts> userAccounts) {
        this.userAccounts = userAccounts;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton( new SimpleGrantedAuthority("ROLE_" + getUserRole()));
    }

    @Override
    public  String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
         return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
