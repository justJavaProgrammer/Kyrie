package com.odeyalo.kyrie.core.oauth2;

/**
 * Enum to represent flow side type.
 * <p>Side can be CLIENT, SERVER or support both of them.</p>
 *
 * @version 1.0
 */
public enum Oauth2FlowSideType {
    /**
     * Represent the client side flow type. Client side flow is Implicit and etc
     */
    CLIENT_SIDE,
    /**
     * Represent the server side flow type. Server side flow is Authorization code.
     */
    SERVER_SIDE,
    /**
     * Represent Server and Client side together.
     * <p>Example of BOTH flow type is Hybrid from Oidc Specification</p>
     * It can be helpful when using id_token response type from OpenIDConnect specification.
     */
    BOTH
}
