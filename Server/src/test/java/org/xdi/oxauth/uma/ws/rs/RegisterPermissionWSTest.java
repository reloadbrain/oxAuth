/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.uma.ws.rs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.model.uma.PermissionTicket;
import org.xdi.oxauth.model.uma.ResourceSetResponse;
import org.xdi.oxauth.model.uma.TUma;
import org.xdi.oxauth.model.uma.UmaConstants;
import org.xdi.oxauth.model.uma.UmaPermission;
import org.xdi.oxauth.model.uma.UmaTestUtil;
import org.xdi.oxauth.model.uma.wrapper.Token;
import org.xdi.oxauth.util.ServerUtil;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 15/03/2013
 */

public class RegisterPermissionWSTest extends BaseTest {

	@ArquillianResource
	private URI url;

	private static Token pat;
	private static ResourceSetResponse resourceSet;
	private static String umaRegisterResourcePath;
	private static String umaPermissionPath;

	@Test
	@Parameters({ "authorizePath", "tokenPath", "umaUserId", "umaUserSecret", "umaPatClientId", "umaPatClientSecret",
			"umaRedirectUri", "umaRegisterResourcePath", "umaPermissionPath" })
	public void init_(String authorizePath, String tokenPath, String umaUserId, String umaUserSecret,
			String umaPatClientId, String umaPatClientSecret, String umaRedirectUri, String umaRegisterResourcePath,
			String p_umaPermissionPath) {
		this.umaRegisterResourcePath = umaRegisterResourcePath;
		umaPermissionPath = p_umaPermissionPath;

		pat = TUma.requestPat(url, authorizePath, tokenPath, umaUserId, umaUserSecret, umaPatClientId,
				umaPatClientSecret, umaRedirectUri);
		UmaTestUtil.assert_(pat);
	}

	@Test(dependsOnMethods = { "init_" })
	public void init() {
		resourceSet = TUma.registerResourceSet(url, pat, umaRegisterResourcePath, UmaTestUtil.createResourceSet());
		UmaTestUtil.assert_(resourceSet);
	}

	@Test(dependsOnMethods = { "init" })
	@Parameters({ "umaAmHost", "umaHost" })
	public void testRegisterPermission(final String umaAmHost, String umaHost) throws Exception {
		final UmaPermission r = new UmaPermission();
		r.setResourceSetId(resourceSet.getId());
		r.setScopes(Arrays.asList("http://photoz.example.com/dev/scopes/view"));

		final PermissionTicket ticket = TUma.registerPermission(url, pat, umaAmHost, umaHost, r, umaPermissionPath);
		UmaTestUtil.assert_(ticket);
	}

	@Test(dependsOnMethods = { "testRegisterPermission" })
	@Parameters({ "umaAmHost", "umaHost" })
	public void testRegisterPermissionWithInvalidResourceSet(final String umaAmHost, String umaHost) {
		final String path = umaPermissionPath;
		try {
			Builder request = ResteasyClientBuilder.newClient().target(url.toString() + path).request();
			request.header("Accept", UmaConstants.JSON_MEDIA_TYPE);
			request.header("Authorization", "Bearer " + pat.getAccessToken());
			request.header("Host", umaAmHost);

			String json = null;
			try {
				final UmaPermission r = new UmaPermission();
				r.setResourceSetId(resourceSet.getId() + "x");

				json = ServerUtil.createJsonMapper().writeValueAsString(r);
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}

			Response response = request.post(Entity.json(json));
			String entity = response.readEntity(String.class);

			BaseTest.showResponse("UMA : RegisterPermissionWSTest.testRegisterPermissionWithInvalidResourceSet() : ",
					response, entity);

			assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode(),
					"Unexpected response code.");
			try {
				final PermissionTicket t = ServerUtil.createJsonMapper().readValue(entity, PermissionTicket.class);
				Assert.assertNull(t);
			} catch (Exception e) {
				// it's ok if it fails here, we expect ticket as null.
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	// use normal test instead of @AfterClass because it will not work with
	// ResourceRequestEnvironment seam class which is used
	// behind TUma wrapper.
	@Test(dependsOnMethods = { "testRegisterPermissionWithInvalidResourceSet" })
	public void cleanUp() {
		if (resourceSet != null) {
			TUma.deleteResourceSet(url, pat, umaRegisterResourcePath, resourceSet.getId());
		}
	}
}
