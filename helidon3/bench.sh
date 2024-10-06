#! /bin/zsh

clear

. ./setEnv.sh

# Standard traffic
wrk -c$WRK_C -d60 --latency http://localhost:8080/loombench

echo ""

echo "Server Memory and CPU usage"
serverpid=$(jps | awk '$2=="helidon3.jar" {print $1}')
ps -o rss,time -p $serverpid

echo ""

echo "Server JFR Virtual Threads recording"

# Dump JFR metrics in the file
jcmd $serverpid JFR.dump name=serverdemo filename=./target/serverdemo.jfr > /dev/null

# Print Virtual Threads Metrics
jfr summary target/serverdemo.jfr | grep -i VirtualThread
