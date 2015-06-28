package simpleefa.server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
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
public class NextDeparturesServlet extends SimpleEfaServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8114130077969761004L;

	@Override
	protected File getXqueryPath() throws URISyntaxException {
		return new File(getClass().getResource(
				"/simpleefa/server/xquery/nextdepartures.qry").toURI());
	}

	@Override
	protected String getEfaService() {
		return "XML_DM_REQUEST";
	}

	@Override
	protected PostData createPostData(HttpServletRequest request,
			XQPreparedExpression pEx) throws UnsupportedEncodingException,
			XQException {
		HashMap<String, String> ret = new HashMap<String, String>();

		String station = getRequestPostData(request, "station");
		int woAssignedStops = ensureInt(
				request.getParameter("withoutNearStops"), 0);
		long time = (getTime(request.getParameter("time")));
		int maxResults = ensureInt(request.getParameter("maxResults"), 30);
		String filterTypes = getRequestPostData(request, "onlyTypes",
				"0!1!2!3!4!5!6!7!8!9!10!11");

		Date d = new Date(time);
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		String date = (c.get(Calendar.YEAR)) + ""
				+ leadingZero(c.get(Calendar.MONTH) + 1) + ""
				+ leadingZero(c.get(Calendar.DATE));
		HashMap<String, String> filterMap = getMotString(filterTypes);

		pEx.bindDouble(new QName("x_cor"), 0.000001, null);
		pEx.bindDouble(new QName("y_cor"), 0.000001, null);

		ret.put("anyObjFilter_dm", "126");
		ret.put("anySigWhenPerfectNoOtherMatches", "1");
		ret.put("convertAddressesITKernel2LocationServer", "1");
		ret.put("convertCoord2LocationServer", "1");
		ret.put("convertCrossingsITKernel2LocationServer", "1");
		ret.put("convertPOIsITKernel2LocationServer", "1");
		ret.put("convertStopsPTKernel2LocationServer", "1");
		ret.put("locationServerActive", "1");
		ret.put("deleteAssignedStops_dm", "" + woAssignedStops);
		ret.put("itOptionsActive", "1");
		ret.put("itdDate", date);
		ret.put("itdLPxx_advancedOptionsNoJavaScript", "true");
		ret.put("itdLPxx_dest", "");
		ret.put("includeCompleteStopSeq", "1");
		ret.put("itdLPxx_script", "false");
		ret.put("itdTimeHour", "" + c.get(Calendar.HOUR_OF_DAY));
		ret.put("itdTimeMinute", "" + c.get(Calendar.MINUTE));
		ret.put("language", "de");
		ret.put("limit", "" + maxResults);
		ret.put("locationServerActive", "1");
		ret.put("maxAssignedStops", "5");
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
		ret.put("useRealtime", "1");
		ret.put("includedMeans", "1");
		ret.put("coordOutputFormat", "WGS84");

		ret.putAll(filterMap);

		return new PostData(ret);
	}
}
