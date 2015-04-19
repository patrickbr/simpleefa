package simpleefa.server.requestbuilder;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Patrick Brosi
 * 
 */
public class PostData {
	private HashMap<String, String> data;
	private String postString;

	public PostData(HashMap<String, String> data) {
		this.data = data;
		this.postString = generatePostString();
	}

	public String toString() {
		return postString;
	}

	private String generatePostString() {
		Iterator<String> i = data.keySet().iterator();
		String ret = "";

		while (i.hasNext()) {
			String curKey = i.next();
			String curEntry = data.get(curKey);
			ret += curKey + "=" + curEntry + "&";
		}
		return ret;
	}
}
