package com.odeyalo.kyrie.core.events.listener.domain;

import com.odeyalo.kyrie.core.events.AuthorizationRequestProcessingFinishedKyrieEvent;
import com.odeyalo.kyrie.core.events.listener.AuthorizationRequestProcessingFinishedKyrieEventListener;
import com.odeyalo.kyrie.core.support.web.TemporaryRequestAttributesRepository;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * The event listener is used to clear the temporary request store.
 */
public class RequestAttributesClearAuthorizationRequestProcessingFinishedKyrieEventListener implements AuthorizationRequestProcessingFinishedKyrieEventListener {
    private final TemporaryRequestAttributesRepository temporaryRequestAttributesRepository;
    private final Logger logger = LoggerFactory.getLogger(RequestAttributesClearAuthorizationRequestProcessingFinishedKyrieEventListener.class);

    public RequestAttributesClearAuthorizationRequestProcessingFinishedKyrieEventListener(TemporaryRequestAttributesRepository temporaryRequestAttributesRepository) {
        this.temporaryRequestAttributesRepository = temporaryRequestAttributesRepository;
    }

    @Override
    public void onEvent(AuthorizationRequestProcessingFinishedKyrieEvent event) {
        logger.info("Received the {}", event);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assert.state(attributes != null, "The listener works only with Servlet environment!");
        HttpServletRequest request = attributes.getRequest();
        temporaryRequestAttributesRepository.clear(request);
        logger.info("The temporary request attributes has been cleared");
    }
}
