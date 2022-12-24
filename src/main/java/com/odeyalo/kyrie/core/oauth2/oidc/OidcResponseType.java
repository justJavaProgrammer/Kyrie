package com.odeyalo.kyrie.core.oauth2.oidc;

import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent the Oidc response types.
 * This class extending Oauth2ResponseType since Oidc supports Oauth2 response types too
 *
 * @version 1.0
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#id_tokenExample">ID Token Response Type</a>
 */
public class OidcResponseType extends Oauth2ResponseType {
    public static final OidcResponseType ID_TOKEN = OidcResponseType.of("id_token", Oauth2FlowSideType.BOTH);

    /**
     * Unmodifiable Map with all supported Oidc response types.
     * The map also contains values from OAUTH2_RESPONSE_TYPES since Oidc extending Oauth2
     * Key - name of response type by specification
     * Value - Oauth2ResponseType with all fields set
     */
    public static final Map<String, Oauth2ResponseType> OIDC_RESPONSE_TYPES = Collections.unmodifiableMap(
            new HashMap<>(OAUTH2_RESPONSE_TYPES) {{
                put(ID_TOKEN.getSimplifiedName(), ID_TOKEN);
            }}
    );

    protected OidcResponseType(String simplifiedName, Oauth2FlowSideType flowType) {
        super(simplifiedName, flowType);
    }

    public static OidcResponseType of(String simplifiedName, Oauth2FlowSideType flowType) {
        return new OidcResponseType(simplifiedName, flowType);
    }

    @Override
    public String getSimplifiedName() {
        return simplifiedName;
    }

    @Override
    public Oauth2FlowSideType getFlowType() {
        return flowType;
    }
}
