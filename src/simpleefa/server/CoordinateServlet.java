package simpleefa.server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import simpleefa.server.requestbuilder.PostData;

/**
 * @author Patrick Brosi
 * 
 */
public class CoordinateServlet extends SimpleEfaServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -371521138469209370L;

	protected File getXqueryPath() throws URISyntaxException {
		return new File(getClass().getResource(
				"/simpleefa/server/xquery/coordinate.qry").toURI());
	}

	protected String getEfaService() {
		return "XML_COORD_REQUEST";
	}

	protected PostData createPostData(HttpServletRequest request,
			XQPreparedExpression pEx) throws UnsupportedEncodingException,
			XQException {
		HashMap<String, String> ret = new HashMap<String, String>();

		double lat = ensureDouble(request.getParameter("lat"), 0);
		double lng = ensureDouble(request.getParameter("lng"), 0);

		long radius = ensureInt(request.getParameter("radius"), 0);

		pEx.bindDouble(new QName("x_cor"), 0.000001, null);
		pEx.bindDouble(new QName("y_cor"), 0.000001, null);

		ret.put("coordOutputFormat", "WGS84");
		ret.put("coord", lng + ":" + lat + ":WGS84");
		ret.put("inclDrawClasses_1", "1:2:6");
		ret.put("max", "-1");
		ret.put("purpose", "");
		ret.put("inclFilter", "1");
		ret.put("radius_1", "" + radius);
		ret.put("type_1", "STOP");

		return new PostData(ret);
	}
}
