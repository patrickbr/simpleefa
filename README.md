# SimpleEFA #

A simplefied wrapper around EFA's XML interface

## Requirements ##

* ant
* tomcat installation
* servlet-api.jar has to be in your CLASSPATH

## Installation ##

Before building this, make sure you point EFA_URL in src/simpleefa/server/Simpleefa.java to a valid SimpleEFA XML interface, e.g. like this:

    protected static String EFA_URL = "http://www.efa-bw.de/nvbw/XML_TRIP_REQUEST2";

This is a Java-Servlet which can for example be run in Tomcat. To create a distributable WAR-File (which can also be uploaded to any Tomcat-Server) type

    ant war

and upload the generated build/simpleefa.war to a live Tomcat server.

## What does it do? ##

TODO
