package com.odeyalo.kyrie.core.events.listener.domain;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.events.authentication.UserLoginAuthenticationGrantedKyrieEvent;
import com.odeyalo.kyrie.core.events.listener.UserLoginAuthenticationGrantedKyrieEventListener;
import com.odeyalo.kyrie.core.support.web.TemporaryRequestAttributesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Domain listener in Kyrie, that saves the user in TemporaryRequestAttributesRepository after authentication
 */
public class Oauth2UserStoreUserLoginAuthenticationGrantedKyrieEventListener implements UserLoginAuthenticationGrantedKyrieEventListener {
    public static final String AUTHENTICATED_USER_ATTRIBUTE_NAME = "authenticated_user";
    private final TemporaryRequestAttributesRepository temporaryRequestAttributesRepository;
    private final Logger logger = LoggerFactory.getLogger(Oauth2UserStoreUserLoginAuthenticationGrantedKyrieEventListener.class);

    public Oauth2UserStoreUserLoginAuthenticationGrantedKyrieEventListener(TemporaryRequestAttributesRepository temporaryRequestAttributesRepository) {
        this.temporaryRequestAttributesRepository = temporaryRequestAttributesRepository;
    }

    @Override
    public void onEvent(UserLoginAuthenticationGrantedKyrieEvent event) {
        Oauth2User oauth2User = event.getOauth2User();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        temporaryRequestAttributesRepository.save(request, AUTHENTICATED_USER_ATTRIBUTE_NAME, oauth2User);

        this.logger.debug("Saved the oauth2 user: {} with key: {}", oauth2User, AUTHENTICATED_USER_ATTRIBUTE_NAME);
    }
}
