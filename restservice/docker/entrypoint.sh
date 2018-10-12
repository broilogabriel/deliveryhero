#!/bin/sh

JAR_FILE=$1
MAIN_CLASS=$2

java $JVM_OPTS \
    -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \
    -jar ${JAR_FILE} \
    ${MAIN_CLASS} \
    $3