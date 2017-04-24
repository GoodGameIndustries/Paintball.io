#!/bin/bash
while true
do
	wget http://web-tokens.com/ggiemmett/server.jar
	java -jar -Xmx1024M -Xms1024M server.jar
	rm server.jar
done