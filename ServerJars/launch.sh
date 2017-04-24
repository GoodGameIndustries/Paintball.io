#!/bin/bash
sudo yum -y update
sudo yum -y install java-1.7.0-openjdk
sudo wget http://web-tokens.com/ggiemmett/run.sh
sudo chmod 777 ./run.sh
sudo screen -A -m -d -S "server" "./run.sh"
sudo ./run.sh