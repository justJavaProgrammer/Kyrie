package com.odeyalo.kyrie.core.authentication;

import com.odeyalo.kyrie.core.Oauth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class EmbeddedOauth2UserAuthenticationService implements Oauth2UserAuthenticationService {

    @Override
    public AuthenticationResult authenticate(Oauth2UserAuthenticationInfo info) {
        if (info.equals(new Oauth2UserAuthenticationInfo("admin", "123"))) {
            Oauth2User user = new Oauth2User(UUID.randomUUID().toString(), "admin", "123", Collections.singleton("ADMIN"), Collections.emptyMap());
            return AuthenticationResult.success(user);
        }
        return AuthenticationResult.failed();
    }
}
