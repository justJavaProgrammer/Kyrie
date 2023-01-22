package com.odeyalo.kyrie.core.oauth2.tokens.customizer;

import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;

/**
 * Simple functional interface that used to customize the {@link Oauth2Token}.
 *
 * @see Oauth2Token
 * @see CombinedOauth2Token
 * @version 1.0
 */
@FunctionalInterface
public interface Oauth2TokenCustomizerProcessor {

    /**
     * Customize the original {@link Oauth2Token} and put customized fields in builder
     * @param original - original Oauth2Token that was generated.
     * @param builder - builder to put the customized field values from original Oauth2Token
     */
    void enhanceOauth2Token(Oauth2Token original, CombinedOauth2Token.CombinedOauth2TokenBuilder<?, ?> builder);

}
