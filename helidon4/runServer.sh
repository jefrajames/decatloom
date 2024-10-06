#! /bin/zsh

# GC options: UseZGC, UseSerialGC, UseG1GC, UseParallelGC UseShenandoahGC
# -Djdk.virtualThreadScheduler.parallelism=32 \
# -XX:StartFlightRecording,settings=../bin/jfrconf.jfc,name=helidon4vt,filename=./target/helidon4vt.jfr \
# -XX:NativeMemoryTracking=summary \
# -Xlog:gc:./target/gc.log \

# export DB_POOL_SIZE=100
# export SERVER_EXECUTOR_SERVICE_CORE_POOL_SIZE=1600
# export SERVER_EXECUTOR_SERVICE_MAX_POOL_SIZE=1600
# export SERVER_EXECUTOR_SERVICE_VIRTUAL_THREADS=false

. ./setEnv.sh

java -jar  \
     -Djdk.tracePinnedThreads=short \
     -XX:+UseG1GC \
     -Xmx4g \
     -Xms4g \
     -XX:StartFlightRecording,settings=../jfrconf.jfc,name=serverdemo,filename=./target/serverdemo.jfr \
     ./target/helidon4.jar
