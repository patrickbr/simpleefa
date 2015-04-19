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
public class StationNameServlet extends SimpleEfaServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8114130077969761004L;

	protected File getXqueryPath() throws URISyntaxException {
		return new File(getClass().getResource(
				"/simpleefa/server/xquery/stationname.qry").toURI());
	}

	protected String getEfaService() {
		return "XML_DM_REQUEST";
	}

	protected PostData createPostData(HttpServletRequest request,
			XQPreparedExpression pEx) throws UnsupportedEncodingException,
			XQException {
		HashMap<String, String> ret = new HashMap<String, String>();

		int limit = request.getParameter("maxResults") != null ? Integer
				.parseInt(request.getParameter("maxResults")) : 20;
		String station = getRequestPostData(request, "station");

		pEx.bindString(new QName("input"), station, null);
		pEx.bindInt(new QName("limit"), limit, null);

		ret.put("cookieOptions", "");
		ret.put("itdLPxx_routeType", "");
		ret.put("excludedMeans", "checkbox");
		ret.put("itdLPxx_bikeTakeAlong", "");
		ret.put("anySigWhenPerfectNoOtherMatches", "1");
		ret.put("convertAddressesITKernel2LocationServer", "1");
		ret.put("convertCoord2LocationServer", "1");
		ret.put("convertCrossingsITKernel2LocationServer", "1");
		ret.put("convertPOIsITKernel2LocationServer", "1");
		ret.put("convertStopsPTKernel2LocationServer", "1");
		ret.put("deleteAssignedStops_dm", "0");
		ret.put("itOptionsActive", "1");
		ret.put("itdLPxx_advancedOptionsNoJavaScript", "true");
		ret.put("itdLPxx_dest", "");
		ret.put("itdLPxx_script", "false");
		ret.put("language", "de");
		ret.put("limit", "1");
		ret.put("locationServerActive", "1");
		ret.put("maxAssignedStops", "100");
		ret.put("mode", "direct");
		ret.put("nameInfo_dm", "invalid");
		ret.put("nameState_dm", "empty");
		ret.put("name_dm", station);
		ret.put("placeInfo_dm", "invalid");
		ret.put("placeState_dm", "empty");
		ret.put("place_dm", "");
		ret.put("ptOptionsActive", "1");
		ret.put("reducedAnyPostcodeObjFilter_dm", "64");
		ret.put("reducedAnyTooManyObjFilter_dm", "2");
		ret.put("reducedAnyWithoutAddressObjFilter_dm", "103");
		ret.put("requestID", "0");
		ret.put("sessionID", "0");
		ret.put("stateless", "1");
		ret.put("typeInfo_dm", "invalid");
		ret.put("type_dm", "stop");
		ret.put("useAllStops", "0");
		ret.put("useHouseNumberList_dm", "1");
		ret.put("useProxFootSearch", "0");
		ret.put("coordOutputFormat", "WGS84");

		return new PostData(ret);
	}
}
