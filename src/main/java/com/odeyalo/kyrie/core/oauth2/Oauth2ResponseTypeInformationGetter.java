package com.odeyalo.kyrie.core.oauth2;

/**
 * Provide basic information about oauth2 response type
 */
public interface Oauth2ResponseTypeInformationGetter {
    /**
     * Getter to get response type simplified name.
     * Simplified name is name of response type that equal to name from specification.
     * For example, ID_TOKEN MUST have "id_token" simplified name.
     * @return - specification name of response type
     */
    String getSimplifiedName();

    /**
     * Method to resolve if response type is server side flow.
     * @return - Oauth2FlowType for implementation
     */
    Oauth2FlowSideType getFlowType();

}
