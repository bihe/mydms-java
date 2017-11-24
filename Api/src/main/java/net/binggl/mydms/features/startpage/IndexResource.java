package net.binggl.mydms.features.startpage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;

import io.dropwizard.auth.Auth;
import net.binggl.mydms.application.Globals;
import net.binggl.mydms.application.Mydms403View;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.security.models.User;
import net.binggl.mydms.features.startpage.models.AppInfo;
import net.binggl.mydms.features.startpage.models.AppInfoBuilder;
import net.binggl.mydms.features.startpage.models.UserInfo;
import net.binggl.mydms.features.startpage.models.VersionInfo;

import static net.binggl.commons.util.ExceptionHelper.wrapEx;

@Path("/")
public class IndexResource implements Globals {

	private final MydmsConfiguration configuration;
    private static Properties properties = null;

	@Inject
	public IndexResource(MydmsConfiguration configuration) {
		this.configuration = configuration;
		if(properties == null) {
            properties = new Properties();
            wrapEx(() -> properties.load(this.getClass().getClassLoader().getResourceAsStream("version.properties")));
        }
	}
	
	@GET
	@RolesAllowed("User")
	public Response home() throws URISyntaxException {
		Response response = Response.temporaryRedirect(new URI(configuration.getApplication().getApplicationStartUrl())).build();
		return response;
	}
	
	@GET
	@Path("403")
	@Produces(MediaType.TEXT_HTML)
	public Mydms403View show403() {
		Mydms403View view = new Mydms403View();
		view.setLoginUrl(configuration.getApplication().getSecurity().getLoginUrl());
		return view;
	}
	
	@GET
	@Path("api/appinfo")
	@RolesAllowed("User")
	@Produces(MediaType.APPLICATION_JSON)
	public AppInfo getApplicatonInfo(@Auth User user) {

	    UserInfo userInfo = new UserInfo(user);
	    VersionInfo versionInfo = new VersionInfo();
        versionInfo.setArtefactId((String)properties.get("artifactId"));
        versionInfo.setVersion((String)properties.get("version"));
        versionInfo.setBuildNumber((String)properties.get("build.number"));

	    AppInfo info = new AppInfoBuilder()
                .setUserInfo(userInfo)
                .setVersionInfo(versionInfo)
                .build();

		return info;
	}
	
}