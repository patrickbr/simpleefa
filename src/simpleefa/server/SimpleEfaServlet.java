package simpleefa.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQSequence;

import org.json.JSONObject;
import org.json.XML;

import simpleefa.server.requestbuilder.PostData;
import simpleefa.server.requestbuilder.RequestBuilder;
import simpleefa.server.xquerybuilder.XQueryBuilder;
import net.sf.saxon.xqj.SaxonXQDataSource;

/**
 * @author Patrick Brosi
 * 
 */
public abstract class SimpleEfaServlet extends HttpServlet {

	private static final long serialVersionUID = -4356636877078339046L;
	private final XQDataSource dataSource = new SaxonXQDataSource();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String efa_url = getEfaUrl(request);
		if (!checkAccess(request, response))
			return;

		XQConnection conn;
		XQPreparedExpression pEx;
		InputStream input;
		
		// output stream the xquery response will be written to
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			conn = dataSource.getConnection();

			XQueryBuilder b = new XQueryBuilder(conn, getXqueryPath());
			pEx = b.getExpr();

			PostData data = createPostData(request, pEx);
			RequestBuilder rb = new RequestBuilder(new URL(efa_url));

			rb.prepareRequest(getEfaService(), data);
			input = rb.fireRequest();
			b.bind(input);

			out.write("<?xml version=\"1.0\"?>".getBytes());

			XQSequence result = pEx.executeQuery();
			result.writeSequence(out, null);

			result.close();
			pEx.close();
			conn.close();
			out.close();
			input.close();
			rb.disconnect();
		} catch (FileNotFoundException e) {
			out.write("<?xml version=\"1.0\"?>".getBytes());
			out.write(("<error>Could not connect to " + efa_url + "</error>").getBytes());
		} catch (Exception e) {
			out.write("<?xml version=\"1.0\"?>".getBytes());
			out.write("<error>Could process server response.</error>".getBytes());
		}
		
		String returnString = out.toString("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Server", "simpleEFA");
		
		if (request.getParameter("format") != null &&
				request.getParameter("format").toLowerCase().equals("json")) {
			// convert output to JSON if requested
			response.setContentType("text/javascript");
			
			Xml2Json xml2json = new Xml2Json();
								
			// define xpaths for arrays in JSON
			xml2json.addPathRule("/request/connections/connection", null, true, false);
			xml2json.addPathRule("/request/next_departures/departure", null, true, false);
			xml2json.addPathRule("/request/connections/connection/connection_parts/part", null, true, false);
			xml2json.addPathRule("/nearby_stations/station", null, true, false);
			xml2json.addPathRule("/possible_stations/station", null, true, false);			
		
			try {
				returnString = xml2json.xml2json(returnString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// wrap with optional callback string
			if (request.getParameter("callback") != null &&
					!request.getParameter("callback").isEmpty()) {
				returnString = request.getParameter("callback") + '(' + returnString + ')';
			}
		} else {
			// by default, output XML
			response.setContentType("text/xml");
		}
		
		response.getOutputStream().write(returnString.getBytes());
	}

    /**
     * @return the path to the xquery query file
     */
	protected File getXqueryPath() throws URISyntaxException {
		return null;
	}

    /**
     * @return the name of the EFA service the class has to uses (e.g. XML_TRIP_REQUEST2)
     */
	protected String getEfaService() {
		return "";
	}

    /**
     * @return post data send to the efa instance
     */
	protected PostData createPostData(HttpServletRequest request,
			XQPreparedExpression pEx) throws UnsupportedEncodingException,
			XQException {
		return null;
	}

    /**
     * @return the URL of the EFA instance, either from cfg file or from URL
     */
	protected String getEfaUrl(HttpServletRequest request) {
		String efa_url = SimpleEfa.PROPERTIES.getProperty("common.efa_url");

		if (request.getParameter("efa_url") != null) {
			efa_url = request.getParameter("efa_url");
		}

		if (efa_url.charAt(efa_url.length() - 1) != '/') {
			efa_url = efa_url + '/';
		}

		return efa_url;
	}

    /**
     * wrap number with leading zero
     */
	protected String leadingZero(int i) {
		if (i < 10)
			return '0' + Integer.toString(i);
		else
			return Integer.toString(i);
	}

    /**
     * ensure an integer from a string, return def if conversion
     * not possible
     */
	protected int ensureInt(String cand, int def) {
		try {
			return Integer.parseInt(cand);
		} catch (Exception e) {
			return def;
		}
	}

    /**
     * ensure double from a string, return def if conversion
     * not possible
     */
	protected double ensureDouble(String cand, double def) {
		try {
			return Double.parseDouble(cand);
		} catch (Exception e) {
			return def;
		}
	}

    /**
     * @return timestamp from string
     */
	protected long getTime(String t) {
		try {
			return Long.parseLong(t);
		} catch (Exception e) {
			return System.currentTimeMillis();
		}
	}
	
    /**
     * @return a coordinate string expected by EFA
     */
	protected String getCoordinateStrFromLatLng(double lat, double lng) {
		return Double.toString(lng) + ':' + Double.toString(lat) + ":WGS84";
	}

    /**
     * 
     */
	protected String getRequestPostData(HttpServletRequest request,
			String post, String def) throws UnsupportedEncodingException {
		String ret = request.getParameter(post) != null ? request
				.getParameter(post) : def;
		if (SimpleEfa.PROPERTIES.getProperty("common.isoencoderequests", "1")
				.equals("1")) {
			ret = URLEncoder.encode(ret, "iso-8859-15");
		}
		return ret;
	}

    /**
     * 
     */
	protected String getRequestPostData(HttpServletRequest request, String post)
			throws UnsupportedEncodingException {
		return getRequestPostData(request, post, "");
	}

    /**
     * 
     */
	protected boolean checkAccess(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ServletContext c = this.getServletContext();

		if (!SimpleEfa.checkLimit(request, c)) {
			SimpleEfa.limitReached(request, response);
			return false;
		}
		return true;
	}

    /**
     * 
     */
	protected HashMap<String, String> getMotString(String filterTypes) {
		String[] types = filterTypes.split("!");
		HashMap<String, String> ret = new HashMap<String, String>();

		for (int i = 0; i < 12; i++) {
			if (Arrays.asList(types).contains("" + i)) {
				ret.put("inclMOT_" + i, "1");
			} else {
				ret.put("inclMOT_" + i + "_disabled", "1");
			}
		}
		return ret;
	}
}
