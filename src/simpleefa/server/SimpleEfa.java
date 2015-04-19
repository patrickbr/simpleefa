package simpleefa.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Patrick Brosi
 * 
 */
public class SimpleEfa implements ServletContextListener {
	// a list of hosts the connection limit is not applied to
	protected static String[] WHITELIST;
	// max connections per hour, -1 means unlimited, 0 means none.
	protected static int MAXCONNPERHOUR;

	public static Properties PROPERTIES = new Properties();

	protected static boolean checkLimit(HttpServletRequest request,
			ServletContext c) {
		String host = request.getRemoteAddr();
		return checkAccess(host, c);
	}

	private static boolean checkAccess(String host, ServletContext c) {
		if (MAXCONNPERHOUR == -1 || Arrays.asList(WHITELIST).contains(host))
			return true;
		if (MAXCONNPERHOUR == 0)
			return false;

		long curTime = (new Date()).getTime();

		if (c.getAttribute("acces-time-" + host) == null) {
			c.setAttribute("acces-time-" + host, curTime);
		}

		if (c.getAttribute("acces-count-" + host) == null) {
			c.setAttribute("acces-count-" + host, 1);
		}

		int accesses = (Integer) c.getAttribute("acces-count-" + host);
		long time = (Long) c.getAttribute("acces-time-" + host);

		if (curTime - time > 3600000) {
			c.setAttribute("acces-count-" + host, 1);
			c.setAttribute("acces-time-" + host, curTime);
			return true;
		}

		if (accesses < MAXCONNPERHOUR) {
			c.setAttribute("acces-count-" + host, accesses + 1);
			return true;
		} else {
			return false;
		}
	}

	protected static void limitReached(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ServletOutputStream out = response.getOutputStream();

		out.println("<?xml version=\"1.0\"?>");
		out.println("<error>Access denied for host " + request.getRemoteAddr()
				+ ", sorry.</error>");
	}

	@Override
	public void contextDestroyed(ServletContextEvent ctx) {

	}

	@Override
	public void contextInitialized(ServletContextEvent ctx) {
		String path = new File(ctx.getServletContext().getRealPath("/WEB-INF"),
				"simpleefa.properties").getAbsolutePath();
		InputStream input = null;

		try {
			input = new FileInputStream(path);
			PROPERTIES.load(input);
			MAXCONNPERHOUR = Integer.parseInt(PROPERTIES.getProperty(
					"common.maxconnectionperhour", "-1"));
			WHITELIST = PROPERTIES.getProperty("common.ipwhitelist",
					"127.0.0.1,192.168.0.1,0.0.0.1,0:0:0:0:0:0:0:1").split(",");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}