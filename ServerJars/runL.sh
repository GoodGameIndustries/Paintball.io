#!/bin/bash
while true
do
	rm -f load.jar
	wget http://web-tokens.com/ggiemmett/load.jar
	java -jar -Xmx1024M -Xms1024M load.jar
done