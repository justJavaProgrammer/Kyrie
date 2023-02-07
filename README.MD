## Kyrie

Kyrie is Oauth2 protocol implementation using Java and Spring Framework


## API
The Kyrie provides the several endpoints:

* [GET /oauth2/authorize](#get-oauth2authorize) - as described in RFC6749, used to prepare the Authorization request and return the login page to the user.
* GET /oauth2/login - endpoint used to log in the user from the remember-me container, it does not require the user credentials
* POST /oauth2/login - endpoint used to log in the user using credentials, this endpoint does not support remember-me 
* GET /oauth2/consent - endpoint used to return the consent page to the user
* POST /oauth2/consent - endpoint that handles submit of the consent, it returns the 302 status code in any case.  
* POST /token - supports only ``application/json`` content type, it returns the access token obtained by authorization code
* POST /token - supports the ``multipart/form-data`` and ``application/x-www-form-urlencoded`` content types, it returns the access token obtained by authorization code  
* POST /tokeninfo - endpoint that returns the info about the access token

## GET oauth2/authorize
Required parameters: 
* client_id - The value of client_id must be the ID of an app client
* response_type - can be ``code``, ``token``, ``id_token``
* redirect_uri - uri that will be used to redirect the user after the authentication, the uri must be valid and associated with client_id
* scope - space delimited string that contains the scopes required by client application
* state - An opaque value used by the client to maintain
  state between the request and callback

``` http
GET /oauth2/authorize?client_id=odeyalo&response_type=code
&redirect_uri=https://example.com/callback&scope=read write&state=dQw4w9WgXcQ
```
The following request returns the login page to the end user, after the user has been authenticated, 
user will be redirected to ``https://example.com/callback?code=a6cb3be8f0ed35ecba5fd2df692e2578`` 

**NOTE:** ``a6cb3be8f0ed35ecba5fd2df692e2578`` is an authorization code that can be used to obtain an access token

Additional request parameters that can be presented in request:
* [prompt](#supported-prompts-by-kyrie-server) - String that used to resolve the html page to return to the user

### Supported prompts by Kyrie Server:
* none - The Authorization Server MUST NOT display any authentication or consent user interface pages
* login - The Authorization Server SHOULD prompt the End-User for reauthentication
* consent - The Authorization Server SHOULD prompt the End-User for consent before returning information to the Client
* select_account - The Authorization Server SHOULD prompt the End-User to select a user account.
* combined - custom prompt defined by Kyrie, the prompt combines all prompts above and returns the prompt by condition

