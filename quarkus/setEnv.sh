#! /bin/zsh

# WRK connections (injector)
export WRK_C=800

# Endpoint: determines the thread type
#export SERVER_THREAD=platform
export SERVER_THREAD=virtual

# Server Quarkus Thread Pool Size (only for Platform Threads)
export QUARKUS_THREAD_POOL_CORE_THREADS=400
export QUARKUS_THREAD_POOL_MAX_THREADS=400
# export QUARKUS_VERTX_WORKER_POOL_SIZE=5

# Server Database Connection Pool Size
export DB_POOL_SIZE=200
#export DB_POOL_SIZE=400
#export DB_POOL_SIZE=800
#export DB_POOL_SIZE=1000
