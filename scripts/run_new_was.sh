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
fi

# 도커 이미지 빌드
echo "> Building Docker images..."
docker-compose build

echo "> Starting new container: ${TARGET_CONTAINER} on port ${TARGET_PORT}"
docker-compose up -d ${TARGET_CONTAINER}

# nginx를 통한 포트 전환
echo "> Switching nginx to point to ${TARGET_CONTAINER}"
echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee /etc/nginx/conf.d/service_url.inc

echo "> Reloading nginx"
sudo service nginx reload

# 이전 컨테이너 종료
echo "> Stopping current container: ${CURRENT_CONTAINER}"
docker-compose stop ${CURRENT_CONTAINER}

echo "> Deployment to ${TARGET_PORT} complete"
