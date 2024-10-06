#! /bin/zsh

# GC options: UseZGC, UseSerialGC, UseG1GC, UseParallelGC UseShenandoahGC
# -Djdk.virtualThreadScheduler.parallelism=32 \
# -XX:StartFlightRecording,settings=../bin/jfrconf.jfc,name=helidon4vt,filename=./target/helidon4vt.jfr \
# -XX:NativeMemoryTracking=summary \
# -Xlog:gc:./target/gc.log \

clear

. ./setEnv.sh

#     -Dapp.call.remote.ping=true \
java \
     -Dname=serverdemo \
     -Djdk.tracePinnedThreads=short \
     -XX:+UseG1GC \
     -Xmx4g \
     -Xms4g \
     -cp target/classes \
     -XX:StartFlightRecording,settings=../jfrconf.jfc,name=serverdemo,filename=./target/serverdemo.jfr \
     -jar ./target/quarkus-app/quarkus-run.jar 
