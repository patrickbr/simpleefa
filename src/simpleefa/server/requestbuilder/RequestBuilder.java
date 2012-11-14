package simpleefa.server.requestbuilder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author patrick
 *
 */
public class RequestBuilder {
	
	private PostData data;
	private String efaService;
	private String efaUrl;
	private HttpURLConnection connection;	
	
	public RequestBuilder(String efaUrl) {
		this.efaUrl=efaUrl;
	}	
	
	public void prepareRequest(String efaService, PostData data) {		
		this.data=data;
		this.efaService=efaService;		
	}
	
	public InputStream fireRequest() throws IOException {
		
		String url_raw = efaUrl + efaService;
		URL url = new URL(url_raw);
		
		connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Host", "www.efa-bw.de");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Ubuntu; X11; Linux i686; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(data.toString().getBytes().length));
		connection.setRequestProperty("Referer", url_raw);

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
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
