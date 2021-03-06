/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2015, Gluu
 */

package org.xdi.oxauth.ws.rs.fido.u2f;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.xdi.oxauth.model.configuration.AppConfiguration;
import org.xdi.oxauth.model.error.ErrorResponseFactory;
import org.xdi.oxauth.model.fido.u2f.U2fConfiguration;
import org.xdi.oxauth.model.fido.u2f.U2fErrorResponseType;
import org.xdi.oxauth.util.ServerUtil;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * The endpoint at which the requester can obtain FIDO U2F metadata
 * configuration
 *
 * @author Yuriy Movchan Date: 05/13/2015
 */
@Path("/oxauth/fido-u2f-configuration")
@Api(value = "/.well-known/fido-u2f-configuration", description = "The FIDO server endpoint that provides configuration data in a JSON [RFC4627] document that resides in at /.well-known/fido-u2f-configuration directory at its hostmeta [hostmeta] location. The configuration data documents conformance options and endpoints supported by the FIDO U2f server.")
public class U2fConfigurationWS {

	@Inject
	private Logger log;

	@Inject
	private AppConfiguration appConfiguration;

	@Inject
	private ErrorResponseFactory errorResponseFactory;

	@GET
	@Produces({ "application/json" })
	@ApiOperation(value = "Provides configuration data as json document. It contains options and endpoints supported by the FIDO U2F server.", response = U2fConfiguration.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Failed to build FIDO U2F configuration json object.") })
	public Response getConfiguration() {
		try {
			final String baseEndpointUri = appConfiguration.getBaseEndpoint();

			final U2fConfiguration conf = new U2fConfiguration();
			conf.setVersion("2.0");
			conf.setIssuer(appConfiguration.getIssuer());

			conf.setRegistrationEndpoint(baseEndpointUri + "/fido/u2f/registration");
			conf.setAuthenticationEndpoint(baseEndpointUri + "/fido/u2f/authentication");

			// convert manually to avoid possible conflicts between resteasy
			// providers, e.g. jettison, jackson
			final String entity = ServerUtil.asPrettyJson(conf);
			log.trace("FIDO U2F configuration: {}", entity);

			return Response.ok(entity).build();
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
			throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(errorResponseFactory.getUmaJsonErrorResponse(U2fErrorResponseType.SERVER_ERROR)).build());
		}
	}

}
