package auth2.auth2.accounts;

import auth2.auth2.users.Users;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class ConnectedAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String provider;
    @Column(nullable = false)
     private String sub ;
    @Column(columnDefinition ="TEXT")
    private String accessToken;

    private String refreshToken;
    @Column(nullable = true)
       private String account_name;
    private String scope;
    @Column(nullable = true)
    private Instant expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",referencedColumnName = "id")
    private Users user;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public ConnectedAccounts(int id, String provider, String accessToken, String refreshToken, String scope, Instant expiredAt, String sub, Users user) {
        this.id = id;
        this.sub = sub;
        this.provider = provider;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.expiredAt = expiredAt;
        this.user = user;
    }

    public ConnectedAccounts(int id, String provider, String sub, String accessToken, String refreshToken, String account_name, String scope, Instant expiredAt, Users user) {
        this.id = id;
        this.provider = provider;
        this.sub = sub;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.account_name = account_name;
        this.scope = scope;
        this.expiredAt = expiredAt;
        this.user = user;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public ConnectedAccounts() {
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}


