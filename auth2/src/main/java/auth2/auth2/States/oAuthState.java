package auth2.auth2.States;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.time.Instant;

@Entity
public class oAuthState {
    @Id
   private String state;
   private int id;
    private Instant createdAt;

    public oAuthState(String state, int id, Instant createdAt) {
        this.state = state;
        this.id = id;
        this.createdAt = createdAt;
    }

    public oAuthState() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
