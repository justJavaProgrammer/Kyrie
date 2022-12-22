package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import org.springframework.core.convert.converter.Converter;

/**
 * Custom Converter to convert String to Oauth2ResponseType
 * @see Oauth2ResponseType
 */
public class String2ResponseTypeConverter implements Converter<String, Oauth2ResponseType> {

    /**
     * Convert String to Oauth2ResponseType, if converting cannot be completed null will be returned
     * @param source - string source that will be used to converting to Oauth2ResponseType
     * @return - Oauth2ResponseType by source, null otherwise
     */
    @Override
    public Oauth2ResponseType convert(String source) {
        return OidcResponseType.OIDC_RESPONSE_TYPES.get(source);
    }
}
