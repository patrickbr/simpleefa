package simpleefa.server.requestbuilder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Patrick Brosi
 * 
 */
public class RequestBuilder {

	private PostData data;
	private String efaService;
	private URL efaUrl;
	private HttpURLConnection connection;

	public RequestBuilder(URL efaUrl) {
		this.efaUrl = efaUrl;
	}

	public void prepareRequest(String efaService, PostData data) {
		this.data = data;
		this.efaService = efaService;
	}

	public InputStream fireRequest() throws IOException {
		URL url = new URL(efaUrl, efaService);

		connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection
				.setRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(data.toString().getBytes().length));

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(data.toString());
		wr.flush();
		wr.close();

		return connection.getInputStream();
	}

	public void disconnect() {
		connection.disconnect();
		connection = null;
	}
}
