package com.odeyalo.kyrie.core.events;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;

/**
 * The domain Kyrie event that will be invoked when the authorization request processing has been finished.
 */
@Getter
public class AuthorizationRequestProcessingFinishedKyrieEvent extends AbstractKyrieEvent {
    private final AuthorizationRequest request;

    public AuthorizationRequestProcessingFinishedKyrieEvent(AuthorizationRequest request) {
        super(request, RandomStringUtils.randomAlphanumeric(20));
        this.request = request;
    }

    public AuthorizationRequestProcessingFinishedKyrieEvent(AuthorizationRequest request, String eventId) {
        super(request, eventId);
        this.request = request;
    }
}
