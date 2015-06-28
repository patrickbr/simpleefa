package simpleefa.server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xquery.XQPreparedExpression;

import simpleefa.server.requestbuilder.PostData;

/**
 * @author Patrick Brosi
 * 
 */
public class ConnectionServlet extends SimpleEfaServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8114130077969761004L;

	protected File getXqueryPath() throws URISyntaxException {
		return new File(getClass().getResource(
				"/simpleefa/server/xquery/connection.qry").toURI());
	}

	protected String getEfaService() {
		return "XML_TRIP_REQUEST2";
	}

	protected PostData createPostData(HttpServletRequest request,
			XQPreparedExpression pEx) throws UnsupportedEncodingException {
		HashMap<String, String> ret = new HashMap<String, String>();
		
		String station_enc = getRequestPostData(request, "from");
		String station_type = "stop";
		String station_to_enc = getRequestPostData(request, "to");
		String station_to_type = "stop";
		String station_via_enc = getRequestPostData(request, "via");
		String station_via_type = "stop";
		
		if (!getRequestPostData(request, "from_lng").isEmpty() &&
				!getRequestPostData(request, "from_lat").isEmpty()) {
			double lng = ensureDouble(request.getParameter("from_lng"), 0);
			double lat = ensureDouble(request.getParameter("from_lat"), 0);
			station_enc = getCoordinateStrFromLatLng(lat, lng);
			station_type = "coord";
		}
		
		if (!getRequestPostData(request, "to_lng").isEmpty() &&
				!getRequestPostData(request, "to_lat").isEmpty()) {
			double lng = ensureDouble(request.getParameter("to_lng"), 0);
			double lat = ensureDouble(request.getParameter("to_lat"), 0);
			station_to_enc = getCoordinateStrFromLatLng(lat, lng);
			station_to_type = "coord";
		}
		
		if (!getRequestPostData(request, "via_lng").isEmpty() &&
				!getRequestPostData(request, "via_lat").isEmpty()) {
			double lng = ensureDouble(request.getParameter("via_lng"), 0);
			double lat = ensureDouble(request.getParameter("via_lat"), 0);
			station_via_enc = getCoordinateStrFromLatLng(lat, lng);
			station_via_type = "coord";
		}
		
		String depArr = (request.getParameter("timetype") != null && request
				.getParameter("timetype").toLowerCase().equals("arr")) ? request
				.getParameter("timetype") : "dep";
		long time = getTime(request.getParameter("time"));
		int maxResults = ensureInt(request.getParameter("maxResults"), 4);
		String filterTypes = request.getParameter("onlyTypes") != null ? request
				.getParameter("onlyTypes") : "0!1!2!3!4!5!6!7!8!9!10!11";

		Date d = new Date(time);
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		String date = (c.get(Calendar.YEAR)) + ""
				+ leadingZero(c.get(Calendar.MONTH) + 1) + ""
				+ leadingZero(c.get(Calendar.DATE));
		HashMap<String, String> filterMap = getMotString(filterTypes);

		ret.put("anySigWhenPerfectNoOtherMatches", "1");
		ret.put("convertAddressesITKernel2LocationServer", "1");
		ret.put("convertCoord2LocationServer", "1");
		ret.put("convertCrossingsITKernel2LocationServer", "1");
		ret.put("convertPOIsITKernel2LocationServer", "1");
		ret.put("convertStopsPTKernel2LocationServer", "1");
		ret.put("locationServerActive", "1");
		ret.put("itdTimeHour", "" + c.get(Calendar.HOUR_OF_DAY));
		ret.put("itdTimeMinute", "" + c.get(Calendar.MINUTE));
		ret.put("itdDate", date);
		ret.put("language", "de");
		ret.put("includedMeans", "checkbox");
		ret.put("name_origin", station_enc);
		ret.put("name_destination", station_to_enc);
		ret.put("name_via", station_via_enc);
		ret.put("type_origin", station_type);
		ret.put("type_destination", station_to_type);
		ret.put("type_via", (station_via_enc.equals("") ? "" : station_via_type));
		ret.put("place_origin", "");
		ret.put("place_destination", "");
		ret.put("placeInfo_origin", "invalid");
		ret.put("placeState_origin", "empty");
		ret.put("placeInfo_destination", "invalid");
		ret.put("ptOptionsActive", "1");
		ret.put("itOptionsActive", "1");
		ret.put("placeState_destination", "empty");
		ret.put("itdTripDateTimeDepArr", depArr);
		ret.put("reducedAnyPostcodeObjFilter_origin", "64");
		ret.put("reducedAnyTooManyObjFilter_origin", "2");
		ret.put("reducedAnyWithoutAddressObjFilter_origin", "103");
		ret.put("reducedAnyPostcodeObjFilter_destination", "64");
		ret.put("reducedAnyTooManyObjFilter_destination", "2");
		ret.put("reducedAnyWithoutAddressObjFilter_destination", "103");
		ret.put("anyObjFilter_origin", "126");
		ret.put("anyObjFilter_destination", "126");
		ret.put("requestID", "0");
		ret.put("sessionID", "0");
		ret.put("routeType", "LEASTTIME");
		ret.put("execInst", "normresponseal");
		ret.put("calculateDistance", "0");
		ret.put("changeSpeed", "normal");
		ret.put("useAllStops", "0");
		ret.put("useHouseNumberList_dm", "1");
		ret.put("useProxFootSearch", "1");
		ret.put("useRealtime", "1");
		ret.put("calculateDistance", "1");
		ret.put("calcNumberOfTrips", "" + maxResults);

		ret.putAll(filterMap);

		return new PostData(ret);
	}
}
