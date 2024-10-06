#! /bin/zsh

# Default values
thread_count=4000;
heap_size=2g

if [ $# -ge 1 ];then
     thread_count=$1
fi

if [ $# -ge 2 ];then
     heap_size=$2
fi

clear 

echo "Running " $thread_count " Virtual Threads with " $heap_size " memory"

# GC options: UseZGC, UseSerialGC, UseG1GC, UseParallelGC UseShenandoahGC

java -cp target/classes \
     -XX:+UseG1GC \
     -Xmx$heap_size \
     -Xms$heap_size \
     -Xlog:gc:./target/gc.log \
     io.jefrajames.loombench.MaxThreads -v -c $thread_count
