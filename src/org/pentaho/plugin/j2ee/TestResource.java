package org.pentaho.plugin.j2ee;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Path("/j2ee/api/test")
public class TestResource {

	private static final Log log = LogFactory.getLog(TestResource.class);

	class TestJson {
		private String a = "asdf";

		private String b = "bsdf";

		public TestJson() {};

		public String getA() {
			return a;
		}

		public String getB() {
			return b;
		}

	}

	@GET
	public String reply() throws Exception {
		return "JAXRS Adapter Plugin works!";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/json")
	public TestJson getJson() throws Exception {
		return new TestJson();
	}


}
