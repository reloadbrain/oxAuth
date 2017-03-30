/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.ws.rs;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.client.AuthorizationRequest;
import org.xdi.oxauth.client.QueryStringDecoder;
import org.xdi.oxauth.client.RegisterRequest;
import org.xdi.oxauth.client.RegisterResponse;
import org.xdi.oxauth.model.authorize.AuthorizeResponseParam;
import org.xdi.oxauth.model.common.Prompt;
import org.xdi.oxauth.model.common.ResponseType;
import org.xdi.oxauth.model.register.ApplicationType;
import org.xdi.oxauth.model.register.RegisterResponseParam;
import org.xdi.oxauth.model.util.StringUtils;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.testng.Assert.*;
import static org.xdi.oxauth.model.register.RegisterResponseParam.*;

/**
 * Functional tests for Authorize Web Services (embedded)
 *
 * @author Javier Rojas Blum
 * @version December 12, 2016
 */
public class AuthorizeRestWebServiceEmbeddedTest extends BaseTest {

	@ArquillianResource
	private URI url;

    private static String clientId1;
    private static String clientId2;
    private static String accessToken2;

    @Parameters({"registerPath", "redirectUris"})
    @Test
    public void dynamicClientRegistration(final String registerPath, final String redirectUris) throws Exception {


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + registerPath).request();

            @Override
            
                try {
                    

                    List<ResponseType> responseTypes = Arrays.asList(
                            ResponseType.CODE,
                            ResponseType.TOKEN,
                            ResponseType.ID_TOKEN);

                    RegisterRequest registerRequest = new RegisterRequest(ApplicationType.WEB, "oxAuth test app",
                            StringUtils.spaceSeparatedToList(redirectUris));
                    registerRequest.setResponseTypes(responseTypes);
                    registerRequest.addCustomAttribute("oxAuthTrustedClient", "true");

                    request.setContentType(MediaType.APPLICATION_JSON);
                    String registerRequestContent = registerRequest.getJSONParameters().toString(4);
                    Response response = request.post(Entity.json(registerRequestContent));
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("dynamicClientRegistration", response, entity);

                assertEquals(response.getStatus(), 200, "Unexpected response code. " + entity);
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    final RegisterResponse registerResponse = RegisterResponse.valueOf(entity);
                    ClientTestUtil.assert_(registerResponse);

                    clientId1 = registerResponse.getClientId();
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationCode(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCode", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                try {
                    URI uri = new URI(response.getLocation().toString());
                    assertNotNull(uri.getQuery(), "Query string is null");

                    Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                    assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                    assertNotNull(params.get(AuthorizeResponseParam.SCOPE), "The scope is null");
                    assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                    assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    fail("Response URI is not well formed");
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationCodeNoRedirection(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.header("X-Gluu-NoRedirect", "");
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeNoRedirection", response, entity);

                assertEquals(response.getStatus(), 200, "Unexpected response code.");
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    JSONObject jsonObj = new JSONObject(entity);
                    assertTrue(jsonObj.has("redirect"), "Unexpected result: redirect not found");

                    URI uri = new URI(jsonObj.getString("redirect"));
                    assertNotNull(uri.getQuery(), "Query string is null");

                    Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                    assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                    assertNotNull(params.get(AuthorizeResponseParam.SCOPE), "The scope is null");
                    assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                    assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret"})
    @Test
    public void requestAuthorizationCodeFail1(
            final String authorizePath, final String userId, final String userSecret) throws Exception {
        // Testing with missing parameters
        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(null, null, null, null, null);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeFail1", response, entity);

                assertEquals(response.getStatus(), 400, "Unexpected response code.");
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    JSONObject jsonObj = new JSONObject(entity);
                    assertTrue(jsonObj.has("error"), "The error type is null");
                    assertTrue(jsonObj.has("error_description"), "The error description is null");
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationCodeFail2(final String authorizePath,
                                              final String userId, final String userSecret) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, "https://INVALID_REDIRECT_URI", null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeFail2", response, entity);

                assertEquals(response.getStatus(), 400, "Unexpected response code.");
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    JSONObject jsonObj = new JSONObject(entity);
                    assertTrue(jsonObj.has("error"), "The error type is null");
                    assertTrue(jsonObj.has("error_description"), "The error description is null");
                    assertEquals(jsonObj.get(AuthorizeResponseParam.STATE), state);
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test
    public void requestAuthorizationCodeFail3(
            final String authorizePath, final String userId, final String userSecret, final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                String clientId = "@!1111!0008!INVALID_VALUE";

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeFail3", response, entity);

                assertEquals(response.getStatus(), 401, "Unexpected response code.");
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    JSONObject jsonObj = new JSONObject(entity);
                    assertTrue(jsonObj.has("error"), "The error type is null");
                    assertEquals(jsonObj.getString("error"), "unauthorized_client");
                    assertTrue(jsonObj.has("error_description"), "The error description is null");
                    assertEquals(jsonObj.get(AuthorizeResponseParam.STATE), state);
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationToken(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(
                        ResponseType.TOKEN,
                        ResponseType.ID_TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = UUID.randomUUID().toString();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationToken", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getFragment(), "Fragment is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                        assertNotNull(params.get("access_token"), "The access token is null");
                        assertNotNull(params.get("state"), "The state is null");
                        assertNotNull(params.get("token_type"), "The token type is null");
                        assertNotNull(params.get("expires_in"), "The expires in value is null");
                        assertNotNull(params.get("scope"), "The scope must be null");
                        assertNull(params.get("refresh_token"), "The refresh_token must be null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test
    public void requestAuthorizationTokenFail1(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        // Testing with missing parameters
        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(
                        ResponseType.TOKEN,
                        ResponseType.ID_TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = UUID.randomUUID().toString();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, null, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationTokenFail1", response, entity);

                assertEquals(response.getStatus(), 400, "Unexpected response code.");
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    JSONObject jsonObj = new JSONObject(entity);
                    assertTrue(jsonObj.has("error"), "The error type is null");
                    assertEquals(jsonObj.getString("error"), "invalid_request");
                    assertTrue(jsonObj.has("error_description"), "The error description is null");
                    assertEquals(jsonObj.get(AuthorizeResponseParam.STATE), state);
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationTokenFail2(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = null;

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationTokenFail2", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getFragment(), "Fragment is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                        assertNotNull(params.get("error"), "The error value is null");
                        assertNotNull(params.get("error_description"), "The errorDescription value is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationTokenIdToken(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(
                        ResponseType.TOKEN,
                        ResponseType.ID_TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = UUID.randomUUID().toString();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationTokenIdToken", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getFragment(), "Fragment is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                        assertNotNull(params.get(AuthorizeResponseParam.ACCESS_TOKEN), "The access token is null");
                        assertNotNull(params.get(AuthorizeResponseParam.TOKEN_TYPE), "The token type is null");
                        assertNotNull(params.get(AuthorizeResponseParam.ID_TOKEN), "The id token is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationCodeIdToken(final String authorizePath,
                                                final String userId, final String userSecret,
                                                final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();
        final String nonce = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = new ArrayList<ResponseType>();
                responseTypes.add(ResponseType.CODE);
                responseTypes.add(ResponseType.ID_TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeIdToken", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                try {
                    URI uri = new URI(response.getLocation().toString());
                    assertNotNull(uri.getFragment(), "Query string is null");

                    Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                    assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                    assertNotNull(params.get(AuthorizeResponseParam.ID_TOKEN), "The id token is null");
                    assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                    assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    fail("Response URI is not well formed");
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationTokenCode(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(
                        ResponseType.TOKEN,
                        ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = UUID.randomUUID().toString();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationTokenCode", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getFragment(), "Fragment is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                        assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                        assertNotNull(params.get(AuthorizeResponseParam.ACCESS_TOKEN), "The access token is null");
                        assertNotNull(params.get(AuthorizeResponseParam.TOKEN_TYPE), "The token type is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationTokenCodeIdToken(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(
                        ResponseType.TOKEN,
                        ResponseType.CODE,
                        ResponseType.ID_TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = UUID.randomUUID().toString();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationTokenCodeIdToken", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getFragment(), "Fragment is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                        assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                        assertNotNull(params.get(AuthorizeResponseParam.ACCESS_TOKEN), "The access token is null");
                        assertNotNull(params.get(AuthorizeResponseParam.TOKEN_TYPE), "The token type is null");
                        assertNotNull(params.get(AuthorizeResponseParam.ID_TOKEN), "The id token is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationIdToken(final String authorizePath, final String userId, final String userSecret,
                                            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.ID_TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = UUID.randomUUID().toString();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationIdToken", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getFragment(), "Fragment is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                        assertNotNull(params.get(AuthorizeResponseParam.ID_TOKEN), "The id token is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationWithoutScope(final String authorizePath,
                                                 final String userId, final String userSecret,
                                                 final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();
        final String nonce = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(
                        ResponseType.CODE,
                        ResponseType.ID_TOKEN);
                List<String> scopes = new ArrayList<String>();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationWithoutScope", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                try {
                    URI uri = new URI(response.getLocation().toString());
                    assertNotNull(uri.getFragment(), "Query string is null");

                    Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                    assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                    assertNotNull(params.get(AuthorizeResponseParam.ID_TOKEN), "The id token is null");
                    assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                    assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    fail("Response URI is not well formed");
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationPromptNone(final String authorizePath,
                                               final String userId, final String userSecret,
                                               final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request()

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationPromptNone", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "The query string is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                        assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                        assertNotNull(params.get(AuthorizeResponseParam.SCOPE), "The scope is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationPromptNoneFail(final String authorizePath,
                                                   final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request()

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);

                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationPromptNoneFail", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "Query is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                        assertNotNull(params.get("error"), "The error value is null");
                        assertNotNull(params.get("error_description"), "The errorDescription value is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationPromptLogin(final String authorizePath,
                                                final String userId, final String userSecret,
                                                final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request()

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.LOGIN);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationPromptLogin", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "The query string is null");
                        assertEquals(uri.getPath(), "/authorize");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationPromptConsent(final String authorizePath,
                                                  final String userId, final String userSecret,
                                                  final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request()

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.CONSENT);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationPromptConsent", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "The query string is null");
                        assertEquals(uri.getPath(), "/authorize");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationPromptLoginConsent(final String authorizePath,
                                                       final String userId, final String userSecret,
                                                       final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request()

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.LOGIN);
                authorizationRequest.getPrompts().add(Prompt.CONSENT);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationPromptLoginConsent", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "The query string is null");
                        assertEquals(uri.getPath(), "/authorize");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationPromptNoneLoginConsentFail(final String authorizePath,
                                                               final String userId, final String userSecret,
                                                               final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request()

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.getPrompts().add(Prompt.LOGIN);
                authorizationRequest.getPrompts().add(Prompt.CONSENT);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationPromptNoneLoginConsentFail", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "Query is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                        assertNotNull(params.get("error"), "The error value is null");
                        assertNotNull(params.get("error_description"), "The errorDescription value is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"registerPath", "redirectUri"})
    @Test
    public void requestAuthorizationCodeWithoutRedirectUriStep1(
            final String registerPath, final String redirectUri) throws Exception {


                Builder request = ResteasyClientBuilder.newClient().target(url.toString() + registerPath).request();

            @Override
            
                try {
                    
                    request.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);

                    RegisterRequest registerRequest = new RegisterRequest(ApplicationType.WEB, "oxAuth test app",
                            Arrays.asList(redirectUri));
                    registerRequest.addCustomAttribute("oxAuthTrustedClient", "true");

                    request.setContentType(MediaType.APPLICATION_JSON);
                    String registerRequestContent = registerRequest.getJSONParameters().toString(4);
                    Response response = request.post(Entity.json(registerRequestContent));
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeWithoutRedirectUriStep1", response, entity);

                assertEquals(response.getStatus(), 200, "Unexpected response code. " + entity);
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    JSONObject jsonObj = new JSONObject(entity);
                    assertTrue(jsonObj.has(RegisterResponseParam.CLIENT_ID.toString()));
                    assertTrue(jsonObj.has(CLIENT_SECRET.toString()));
                    assertTrue(jsonObj.has(REGISTRATION_ACCESS_TOKEN.toString()));
                    assertTrue(jsonObj.has(REGISTRATION_CLIENT_URI.toString()));
                    assertTrue(jsonObj.has(CLIENT_ID_ISSUED_AT.toString()));
                    assertTrue(jsonObj.has(CLIENT_SECRET_EXPIRES_AT.toString()));

                    clientId2 = jsonObj.getString(RegisterResponseParam.CLIENT_ID.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret"})
    @Test(dependsOnMethods = "requestAuthorizationCodeWithoutRedirectUriStep1")
    public void requestAuthorizationCodeWithoutRedirectUriStep2(
            final String authorizePath, final String userId, final String userSecret) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId2, scopes, null, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeWithoutRedirectUriStep2", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                try {
                    URI uri = new URI(response.getLocation().toString());
                    assertNotNull(uri.getQuery(), "Query string is null");

                    Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                    assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                    assertNotNull(params.get(AuthorizeResponseParam.SCOPE), "The scope is null");
                    assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                    assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    fail("Response URI is not well formed");
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationCodeWithoutRedirectUriFail(
            final String authorizePath, final String userId, final String userSecret) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, null, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationCodeWithoutRedirectUriFailStep", response, entity);

                assertEquals(response.getStatus(), 400, "Unexpected response code.");
                assertNotNull(entity, "Unexpected result: " + entity);
                try {
                    JSONObject jsonObj = new JSONObject(entity);
                    assertTrue(jsonObj.has("error"), "The error type is null");
                    assertTrue(jsonObj.has("error_description"), "The error description is null");
                    assertEquals(jsonObj.get(AuthorizeResponseParam.STATE), state);
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage() + "\nResponse was: " + entity);
                }
            }
        
    }

    @Parameters({"authorizePath", "userId", "userSecret", "redirectUri"})
    @Test(dependsOnMethods = "dynamicClientRegistration")
    public void requestAuthorizationAccessTokenStep1(
            final String authorizePath, final String userId, final String userSecret,
            final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(
                        ResponseType.TOKEN,
                        ResponseType.ID_TOKEN);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
                String nonce = UUID.randomUUID().toString();

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, nonce);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAuthUsername(userId);
                authorizationRequest.setAuthPassword(userSecret);

                request.header("Authorization", "Basic " + authorizationRequest.getEncodedCredentials());
                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationAccessTokenStep1", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getFragment(), "Fragment is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getFragment());

                        assertNotNull(params.get(AuthorizeResponseParam.ACCESS_TOKEN), "The access token is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertNotNull(params.get(AuthorizeResponseParam.TOKEN_TYPE), "The token type is null");
                        assertNotNull(params.get(AuthorizeResponseParam.EXPIRES_IN), "The expires in value is null");
                        assertNotNull(params.get(AuthorizeResponseParam.SCOPE), "The scope must be null");
                        assertNull(params.get("refresh_token"), "The refresh_token must be null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);

                        accessToken2 = params.get("access_token");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "redirectUri"})
    @Test(dependsOnMethods = "requestAuthorizationAccessTokenStep1")
    public void requestAuthorizationAccessTokenStep2(final String authorizePath,
                                                     final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAccessToken(accessToken2);

                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationAccessTokenStep2", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "The query string is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                        assertNotNull(params.get(AuthorizeResponseParam.CODE), "The code is null");
                        assertNotNull(params.get(AuthorizeResponseParam.SCOPE), "The scope is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            }
        
    }

    @Parameters({"authorizePath", "redirectUri"})
    @Test(dependsOnMethods = "requestAuthorizationAccessTokenStep1")
    public void requestAuthorizationAccessTokenFail(final String authorizePath,
                                                    final String redirectUri) throws Exception {

        final String state = UUID.randomUUID().toString();

        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + authorizePath + "?" + authorizationRequest.getQueryString()).request();

            @Override
            
                

                List<ResponseType> responseTypes = Arrays.asList(ResponseType.CODE);
                List<String> scopes = Arrays.asList("openid", "profile", "address", "email");

                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        responseTypes, clientId1, scopes, redirectUri, null);
                authorizationRequest.setState(state);
                authorizationRequest.getPrompts().add(Prompt.NONE);
                authorizationRequest.setAccessToken("INVALID_ACCESS_TOKEN");

                request.header("Accept", MediaType.TEXT_PLAIN);
                request.setQueryString(authorizationRequest.getQueryString());
            }

            @Override
            Response response = request.get();
                
                String entity = response.readEntity(String.class);
showResponse("requestAuthorizationAccessTokenFail", response, entity);

                assertEquals(response.getStatus(), 302, "Unexpected response code.");
                assertNotNull(response.getLocation(), "Unexpected result: " + response.getLocation());

                if (response.getLocation() != null) {
                    try {
                        URI uri = new URI(response.getLocation().toString());
                        assertNotNull(uri.getQuery(), "The query string is null");

                        Map<String, String> params = QueryStringDecoder.decode(uri.getQuery());

                        assertNotNull(params.get("error"), "The error value is null");
                        assertNotNull(params.get("error_description"), "The errorDescription value is null");
                        assertNotNull(params.get(AuthorizeResponseParam.STATE), "The state is null");
                        assertEquals(params.get(AuthorizeResponseParam.STATE), state);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        fail("Response URI is not well formed");
                    }
                }
            }
        
    }
}