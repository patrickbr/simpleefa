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
 * @author patrick
 *
 */
public class CoordinateServlet extends SimpleEfaServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -371521138469209370L;

	protected File getXqueryPath() throws URISyntaxException {
		return new File(getClass().getResource("/simpleefa/server/xquery/coordinate.qry").toURI());
	}

	protected String getEfaService() {
		return "XML_COORD_REQUEST";
	}
	
	protected PostData createPostData(HttpServletRequest request) throws UnsupportedEncodingException {
		
		HashMap<String,String> ret = new HashMap<String,String>();
		
		long x = ensureInt(request.getParameter("x"),0)-Simpleefa.COORD_X_CORRECTION;
		long y = Simpleefa.COORD_Y_CORRECTION - ensureInt(request.getParameter("y"),0);
		long radius = ensureInt(request.getParameter("radius"),0);
		
		ret.put("coord", x+":"+y+":NBWT");
		ret.put("inclDrawClasses_1", "1:2:6");
		ret.put("max", "-1");
		ret.put("purpose", "");
		ret.put("inclFilter", "1");
		ret.put("radius_1", ""+radius);
		ret.put("type_1", "STOP");
		
		return new PostData(ret);
		
	}
	
	protected void bindVars(XQPreparedExpression pEx) throws XQException {
		
		pEx.bindObject(new QName("x_cor"),new Long(Simpleefa.COORD_X_CORRECTION), null);
		pEx.bindObject(new QName("y_cor"),new Long(Simpleefa.COORD_Y_CORRECTION), null);		
		
	}

}
