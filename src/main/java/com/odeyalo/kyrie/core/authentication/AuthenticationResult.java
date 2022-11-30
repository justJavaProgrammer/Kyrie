package com.odeyalo.kyrie.core.authentication;

import com.odeyalo.kyrie.core.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Return result of user authentication process
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResult {
    private boolean success;
    private User user;

    public AuthenticationResult(boolean success) {
        this.success = success;
    }

    public static AuthenticationResult success(User user) {
        return new AuthenticationResult(true, user);
    }

    public static AuthenticationResult failed() {
        return new AuthenticationResult(false);
    }
}
