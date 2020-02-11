package net.dollmar.svc.depcon.filters;

import spark.FilterImpl;
import spark.Request;
import spark.Response;
import spark.utils.SparkUtils;
import spark.utils.StringUtils;

import static spark.Spark.halt;

import javax.xml.bind.DatatypeConverter;

public class BasicAuthenticationFilter extends FilterImpl
{
	private static final String BASIC_AUTHENTICATION_TYPE = "Basic";

	private static final int NUMBER_OF_AUTHENTICATION_FIELDS = 2;

	private static final String ACCEPT_ALL_TYPES = "*";


	public BasicAuthenticationFilter()
	{
		this(SparkUtils.ALL_PATHS);
	}

	public BasicAuthenticationFilter(final String path)
	{
		super(path, ACCEPT_ALL_TYPES);
	}

	@Override
	public void handle(final Request request, final Response response)
	{
		String authHeaderValue = request.headers("Authorization");
		final String encodedHeader = (authHeaderValue == null)
				? null
				: ((authHeaderValue.startsWith(BASIC_AUTHENTICATION_TYPE))
						?  authHeaderValue.substring(BASIC_AUTHENTICATION_TYPE.length())
						: null);
		//final String encodedHeader = StringUtils.substringAfter(request.headers("Authorization"), "Basic");

		if (notAuthenticatedWith(credentialsFrom(encodedHeader)))
		{
			response.header("WWW-Authenticate", BASIC_AUTHENTICATION_TYPE);
			halt(401);
		}
	}

	private String[] credentialsFrom(final String encodedHeader)
	{
		return (encodedHeader != null)
				? decodeHeader(encodedHeader).split(":")
						: null;
	}

	private String decodeHeader(final String encodedHeader)
	{
		return new String(DatatypeConverter.parseBase64Binary(encodedHeader));
	}

	private boolean notAuthenticatedWith(final String[] credentials)
	{
		return !authenticatedWith(credentials);
	}

	private boolean authenticatedWith(final String[] credentials)
	{
		if (credentials != null && credentials.length == NUMBER_OF_AUTHENTICATION_FIELDS) {
			final String submittedUsername = credentials[0];
			final String submittedPassword = credentials[1];

			return !submittedUsername.isEmpty() && !submittedPassword.isEmpty();
			//return StringUtils.equals(submittedUsername, authenticationDetails.username) && StringUtils.equals(submittedPassword, new String(authenticationDetails.password));
		}
		else
		{
			return false;
		}
	}
}
