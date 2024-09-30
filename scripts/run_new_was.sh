#!/bin/bash

CURRENT_PORT=$(cat /home/ec2-user/service_url.inc | grep -Po '(?<=:)\d+(?=;)')
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."

if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
  TARGET_CONTAINER="carevision-green"
  CURRENT_CONTAINER="carevision-blue"
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
  TARGET_CONTAINER="carevision-blue"
  CURRENT_CONTAINER="carevision-green"
else
  echo "> No WAS is connected to nginx"
  exit 1
fi

# 도커 이미지 빌드
echo "> Building Docker images..."
docker-compose -f ../../docker-compose.yml build

echo "> Starting new container: ${TARGET_CONTAINER} on port ${TARGET_PORT}"
docker-compose -f ../../docker-compose.yml up -d ${TARGET_CONTAINER}
