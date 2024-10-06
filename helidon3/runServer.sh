#! /bin/zsh

# GC options: UseZGC, UseSerialGC, UseG1GC, UseParallelGC UseShenandoahGC
# -Djdk.virtualThreadScheduler.parallelism=32 \
# -XX:StartFlightRecording,settings=../bin/jfrconf.jfc,name=helidon3vt,filename=./target/helidon3vt.jfr \
# -XX:NativeMemoryTracking=summary \
# -Xlog:gc:./target/gc.log \

clear

. ./setEnv.sh

java -jar  \
     -Djdk.tracePinnedThreads=short \
     -XX:+UseG1GC \
     -Xmx4g \
     -Xms4g \
     -XX:StartFlightRecording,settings=../jfrconf.jfc,name=serverdemo,filename=./target/serverdemo.jfr \
     ./target/helidon3.jar
