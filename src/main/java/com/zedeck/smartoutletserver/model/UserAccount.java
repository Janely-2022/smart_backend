package com.zedeck.smartoutletserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "user_accounts")
@SQLDelete(sql = "UPDATE user_accounts SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class UserAccount extends BaseEntity implements Serializable {

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "email", unique = true)
    private String username;

    @Column(name = "userType")
    private String userType;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @Basic(optional = true)
    @Column(name = "token_created_at")
    private LocalDateTime tokenCreatedAt = LocalDateTime.now();

    @JsonIgnore
    @Basic(optional = true)
    @Column(name = "last_login")
    private LocalDateTime lastLogin;


    @Basic(optional = true)
    @Column(name = "remember_token")
    private String rememberToken;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @JsonIgnore
    private int loginAttempts = 0;

    @JsonIgnore
    private LocalDateTime lastOtpSentAt;

    @JsonIgnore
    private LocalDateTime lastLoginAttempt;

    @JsonIgnore
    @Column(name = "refresh_token")
    private String refreshToken;

    @JsonIgnore
    @Column(name = "refresh_token_created_at")
    private LocalDateTime refreshTokenCreatedAt;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();

        // Assuming your userRole is a string representing the role
        if (this.userType ==  null) {
            switch (this.userType) {
                case "CUSTOMER":
                    permissions.add("USER_PERMISSION");
                    break;
                case "MANAGER":
                    permissions.add("MANAGER_PERMISSION");
                    break;
                case "SUPER_ADMIN":
                    permissions.add("SUPER_ADMIN_PERMISSION");
                    break;
                // Add more cases if needed
            }
        }

        return permissions;
    }


}
