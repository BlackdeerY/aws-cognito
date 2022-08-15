package io.blackdeer.cognito.user;

import org.springframework.lang.NonNull;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "SEQ_ACCOUNT", sequenceName = "account_sequence")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ACCOUNT")
    private Long id;

    @Column(nullable = false, unique = true)
    private String sub;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    protected Account() {
    }

    public Account(@NonNull String sub,
                   @NonNull String username,
                   @NonNull String email) {
        assert (sub != null);
        assert (username != null);
        assert (email != null);
        this.sub = sub;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getSub() {
        return sub;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    protected void setSub(String sub) {
        this.sub = sub;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setEmail(String email) {
        this.email = email;
    }
}
