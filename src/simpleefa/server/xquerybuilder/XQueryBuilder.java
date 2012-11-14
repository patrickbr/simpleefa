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

public class XQueryBuilder {

	XQConnection con;
	XQPreparedExpression pEx;
	InputStream qry;
	InputStream input;

	public XQueryBuilder(XQConnection con, String qry, InputStream input) throws XQException {				
		this(con, new ByteArrayInputStream(qry.getBytes()),input);
	}

	public XQueryBuilder(XQConnection con, File qry, InputStream input) throws XQException, FileNotFoundException {
		this(con, new FileInputStream(qry), input);
	}
	
	public XQueryBuilder(XQConnection con, InputStream qry, InputStream input) throws XQException {		
		this.con = con;		
		this.qry = qry;
		this.input = input;
		pEx = con.prepareExpression(qry);
		pEx.bindDocument(new QName("doc"), input, null,null);
	}

	public XQPreparedExpression getExpr() {
		return pEx;
	}

}
