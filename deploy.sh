#!/usr/bin/env bash
gradle war -x test -Dorg.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home
rm -rf /Users/engine/webproject/release-sdk-service/*
unzip -o -d /Users/engine/webproject/release-sdk-service/ build/libs/release_service-1.0.0.war
docker restart sdk-service
