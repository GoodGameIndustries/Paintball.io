#!/bin/bash
while true
do
	rm -f server.jar
	wget http://web-tokens.com/ggiemmett/server.jar
	java -jar -Xmx1024M -Xms1024M server.jar
done