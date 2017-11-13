[![Build Status](https://travis-ci.org/patrickbr/simpleefa.svg?branch=master)](https://travis-ci.org/patrickbr/simpleefa)

# SimpleEFA #

A simplefied wrapper around EFA's XML interface

## Requirements ##

* ant
* tomcat installation
* servlet-api.jar has to be in your CLASSPATH

## Installation ##

This is a Java-Servlet which can for example be run in Tomcat. A pre-built WAR can be found in the releases.

To create a distributable WAR-File yourself checkout this repo and type

    ant war

and upload the generated build/simpleefa.war to a live Tomcat server.

Configuration is done in in war/WEB-INF/simpleefa.properties. *Important*: you have to adjust common.simpleefaurl in the properties file to a valid SimpleEFA backend URL, for example like this:

    common.efa_url = http://www.efa-bw.de/nvbw/

## Request Restrictions ##

A poor-man's security mechanism to prevent mass-requests is built-in and can be configured via common.ipwhitelist and common.maxconnectionperhour.

## ISO-Encoding ##

Most EFA-Installation expect request strings to be ISO-encoded. However, there are some newer installations that expect UTF8-encoding. To disable the default ISO-encoding of request parameters, set common.isoencoderequests = 0 in the properties file.

## Documentation ##

http://patrickbrosi.de/de/projects/simpleefa/

## JSON-Output ##

The default output format is XML directly rendered from the result of an XQuery query. However, you can enforce JSON format by setting the GET parameter `format=JSON`, for example like this:

    http://localhost:10080/simpleefa/stationname?station=stuttgart%20hauptbahnhof&format=JSON
    
An optional GET parameter `callback` will wrap the outputted JSON to make it into JSONP:

    http://localhost:10080/simpleefa/stationname?station=stuttgart%20hauptbahnhof&format=JSON&callback=mycallback

## License ##

SimpleEFA is published under GPL v2, see LICENSE. This software uses 3rd party libraries under MPL v2 (Saxon-HE 9.5) and other, see LICENSE-3RD-PARTY.
