package simpleefa.server;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * repeatable & keypath : 'tagname' : { 'key' : {...}, 'key' : {...}, ...}
 * repeatable : 'tagname' : [ {...}, {...}, ... ]
 * keypath : 'key' : { ... }
 * arrayPath : [[{...}], [{...}]]
 * 
 * @author a-kimura
 * @author Patrick Brosi
 * 
 */
public class Xml2Json {
	
	private String CONTENT_ATTR_NAME = "name";
	
	private List keyPaths = new ArrayList();

	private Map pathMaps = new HashMap();

	private List repeatables = new ArrayList();

	private List singles = new ArrayList();

	private List skips = new ArrayList();

	private List arrays = new ArrayList();

	private Map namespaceResolvers = new HashMap();

	private String basePath;
	
	public interface Xml2JsonListener {
		public String text(String text) throws Exception;
	}

	private Xml2JsonListener listner = new NoOpListner();

	class NoOpListner implements Xml2JsonListener {
		public String text(String text) throws Exception {
			return text;
		}
	}

	public void addPathRule(String xpath, String keyAttrName,
			boolean isRepeatable, boolean isSingle) {
		if (keyAttrName != null) {
			keyPaths.add(xpath);
			pathMaps.put(xpath, keyAttrName);
		}
		if (isRepeatable) {
			repeatables.add(xpath);
		}
		if (isSingle) {
			singles.add(xpath);
		}
	}

	public void addSkipRule(String xpath) {
		skips.add(xpath);
	}

	public void addArrayPath(String xpath) {
		arrays.add(xpath);
	}

	public void addNamespaceResolver(String prefix, String uri) {
		namespaceResolvers.put(uri, prefix);
	}

	public JSONObject xml2jsonObj(NodeList nodes) throws Exception {
		this.basePath = null;
		if (nodes == null || nodes.getLength() == 0)
			return null;
		Node baseNode = nodes.item(0).getParentNode();
		if (baseNode == null)
			return null;
		this.basePath = getXPath((Element) baseNode);
		Map map = new HashMap();
		nodelist2json(map, nodes);
		return new JSONObject(map);
	}

	public JSONObject xml2jsonObj(Element element) throws Exception {
		this.basePath = null;
		Node baseNode = element.getParentNode();
		if (baseNode != null && baseNode.getNodeType() == Node.ELEMENT_NODE)
			this.basePath = getXPath((Element) baseNode);
		JSONObject obj = (JSONObject) node2json(element);
		return obj;
	}

	public String xml2json(NodeList nodes) throws Exception {
		JSONObject obj = xml2jsonObj(nodes);
		if (obj == null)
			return "";
		return obj.toString(1);
	}

	public String xml2json(Element element) throws Exception {
		JSONObject obj = xml2jsonObj(element);
		return obj.toString(1);
	}

	public String xml2json(String xml) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		Element root = doc.getDocumentElement();
		return xml2json(root);
	}

	private Object node2json(Element element) throws Exception {
		Map map = new HashMap();
		String xpath = getXPath(element);
		if (singles.contains(xpath)) {
			if (element.getFirstChild() != null)
				return listner.text(element.getFirstChild().getNodeValue());
			else
				return "";
		}
		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			String name = attr.getNodeName();
			String value = attr.getNodeValue();
			map.put(name, listner.text(value));
		}
		NodeList childs = element.getChildNodes();
		nodelist2json(map, childs);
		if (map.size() > 1) {
			return new JSONObject(map);
		} else if (map.size() == 1){
			return map.values().toArray()[0];
		} else {
			return null;
		}
	}

	private void nodelist2json(Map map, NodeList nodes) throws Exception {
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			switch (node.getNodeType()) {
				case Node.TEXT_NODE:
				case Node.CDATA_SECTION_NODE:
					String text = node.getNodeValue().trim();
					if (text.length() > 0)
						map.put(CONTENT_ATTR_NAME, listner.text(node.getNodeValue()));
					break;
				case Node.ELEMENT_NODE:
					Element childElm = (Element) node;
					String childXPath = getXPath(childElm);
					if (skips.contains(childXPath)) {
						nodelist2json(map, childElm.getChildNodes());
					} else if (arrays.contains(childXPath)) {
						JSONArray obj = (JSONArray) map.get(childElm.getNodeName());
						if (obj == null) {
							obj = new JSONArray();
							map.put(childElm.getNodeName(), obj);
						}
						JSONArray array = new JSONArray();
						NodeList childNodes = childElm.getChildNodes();
						for (int j = 0; j < childNodes.getLength(); j++) {
							Node child = childNodes.item(j);
							//TODO need to support the other node types.
							if (child.getNodeType() != Node.ELEMENT_NODE)
								continue;
							array.put(node2json((Element) child));
						}
						obj.put(array);
					} else {
						String childName = childElm.getNodeName();
						boolean isRepeatable = repeatables.contains(childXPath);
						boolean hasKey = keyPaths.contains(childXPath);
						if (isRepeatable && hasKey) {
							JSONObject obj = (JSONObject) map.get(childName);
							if (obj == null) {
								obj = new JSONObject();
								map.put(childName, obj);
							}
							String attrName = (String) pathMaps.get(childXPath);
							String attrValue = childElm.getAttribute(attrName);
							obj.put(attrValue, node2json(childElm));
						} else if (isRepeatable && !hasKey) {
							JSONArray obj = (JSONArray) map.get(childName);
							if (obj == null) {
								obj = new JSONArray();
								map.put(childName, obj);
							}
							obj.put(node2json(childElm));
						} else if (hasKey) {
							String attrName = (String) pathMaps.get(childXPath);
							String attrValue = childElm.getAttribute(attrName);
							map.put(attrValue, node2json(childElm));
						} else {
							map.put(childName, node2json(childElm));
						}
					}
					break;
				default:
					break;
			}
		}
	}

	private String getXPath(Element element) {
		if (element == null)
			return null;
		StringBuffer xpath = new StringBuffer();
		xpath.append("/");
		String uri = element.getNamespaceURI();
		String prefix = (String) namespaceResolvers.get(uri);
		if (prefix != null)
			xpath.append(prefix).append(":");
		xpath.append(getTagName(element));
		Element parent = element;
		try {
			while (true) {
				parent = (Element) parent.getParentNode();
				if (parent == null)
					break;
				xpath.insert(0, getTagName(parent));
				uri = parent.getNamespaceURI();
				prefix = (String) namespaceResolvers.get(uri);
				if (prefix != null)
					xpath.insert(0, prefix + ":");
				xpath.insert(0, "/");
			}
		} catch (ClassCastException e) {
		}
		String xpathStr = xpath.toString();
		if (this.basePath != null)
			xpathStr = xpathStr.replaceFirst("^" + this.basePath, "");
		return xpathStr;
	}

	private String getTagName(Element elem) {
		String name = elem.getLocalName();
		if (name == null)
			name = elem.getNodeName();
		return name;
	}
}