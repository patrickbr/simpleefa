package simpleefa.server.xquerybuilder;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;

/**
 * @author Patrick Brosi
 * 
 */
public class XQueryBuilder {
	XQPreparedExpression pEx;

	public XQueryBuilder(XQConnection con, String qry) throws XQException {
		this(con, new ByteArrayInputStream(qry.getBytes()));
	}

	public XQueryBuilder(XQConnection con, File qry) throws XQException,
			FileNotFoundException {
		this(con, new FileInputStream(qry));
	}

	public XQueryBuilder(XQConnection con, InputStream qry) throws XQException {
		pEx = con.prepareExpression(qry);
	}

	public void bind(InputStream input) throws XQException {
		pEx.bindDocument(new QName("doc"), input, null, null);
	}

	public XQPreparedExpression getExpr() {
		return pEx;
	}
}
