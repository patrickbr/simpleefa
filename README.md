# SimpleEFA #

A simplefied wrapper around EFA's XML interface

## Requirements ##

* ant
* tomcat installation
* servlet-api.jar has to be in your CLASSPATH

## Installation ##

This is a Java-Servlet which can for example be run in Tomcat. To create a distributable WAR-File (which can also be uploaded to any Tomcat-Server) type

    ant war

and upload the generated build/simpleefa.war to a live Tomcat server.

Configuration is done in in war/WEB-INF/simpleefa.properties. *Important*: you have to adjust common.simpleefaurl in the properties file to a valid SimpleEFA backend URL, for example like this:

    common.efa_url = http://www.efa-bw.de/nvbw/

## Request Restrictions ##

A poor-man's security mechanism to prevent mass-requests is built-in and can be configured via common.ipwhitelist and common.maxconnectionperhour.

## ISO-Encoding ##

Most EFA-Installation expect request strings to be ISO-encoded. However, there are some newer installations that expect UTF8-encoding. To disable the default ISO-encoding of request parameters, set common.isoencoderequests = 0 in the properties file.

## Documentation ##

http://patrickbrosi.de/#q=simpleefa
