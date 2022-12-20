package com.odeyalo.kyrie.core.authorization;

import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.core.oauth2.Oauth2ResponseTypeInformationGetter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

/**
 * Represent the Oauth2 response type
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-3.1.1">Oauth2 Response Type</a>
 */
@ToString
public class Oauth2ResponseType implements Oauth2ResponseTypeInformationGetter {
    /**
     * Represent default Oauth2 CODE response type
     */
    public static final Oauth2ResponseType CODE = Oauth2ResponseType.of("code", Oauth2FlowSideType.SERVER_SIDE);
    /**
     * Represent default Oauth2 TOKEN response type
     */
    public static final Oauth2ResponseType TOKEN = Oauth2ResponseType.of("token", Oauth2FlowSideType.CLIENT_SIDE);

    /**
     * Unmodifiable Map with all supported Oauth2 response types.
     * Key - name of response type by specification
     * Value - Oauth2ResponseType with all fields set
     */
    public static final Map<String, Oauth2ResponseType> OAUTH2_RESPONSE_TYPES = Collections.unmodifiableMap(
            Map.of(CODE.getSimplifiedName(), CODE,
                    TOKEN.getSimplifiedName(), TOKEN)
    );
    // Simplified name is name from specification
    protected final String simplifiedName;
    // Flow type of the response type
    protected final Oauth2FlowSideType flowType;

    protected Oauth2ResponseType(String simplifiedName, Oauth2FlowSideType flowType) {
        this.simplifiedName = simplifiedName;
        this.flowType = flowType;
    }

    public static Oauth2ResponseType of(String simplifiedName, Oauth2FlowSideType flowType) {
        return new Oauth2ResponseType(simplifiedName, flowType);
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
