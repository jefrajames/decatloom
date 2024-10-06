#! /bin/zsh
clear

. ./setEnv.sh

wrk -c$WRK_C -d60 --latency http://localhost:8080/loombench/$SERVER_THREAD

echo ""

echo "Server Memory and CPU usage"
serverpid=$(jps -v |grep serverdemo | awk '$2=="quarkus-run.jar" {print $1}')
ps -o rss,time -p $serverpid

echo "Server JFR Virtual Threads recording"

# Dump JFR metrics in the file
jcmd $serverpid JFR.dump name=serverdemo filename=./target/serverdemo.jfr > /dev/null

# Print Virtual Threads Metrics
jfr summary target/serverdemo.jfr | grep -i VirtualThread
