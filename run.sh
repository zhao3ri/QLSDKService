#!/usr/bin/env bash
#docker run  -d -p 8082:8080 -v H:/webproject/release-sdk-service:/app -v H:/javalog:/logs --name sdk-service resin4:latest
#-d代表在后台运行
#–name代表镜像启动的名称
#-v代表把war包挂载到resin上
#-p代表端口，8081是外部端口
#resin:lastest是镜像的名称
docker run -d --name sdk-service -v /out/artifacts/release_service_main_war/release_service_main_war.war -p 8082:8080 resin4:latest