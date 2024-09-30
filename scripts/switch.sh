#!/bin/bash

# Crawl current connected port of WAS
CURRENT_PORT=$(cat /home/ec2-user/service_url.inc  | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Nginx currently proxies to ${CURRENT_PORT}."

# Toggle port number
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

# nginx를 통한 포트 전환
echo "> Switching nginx to point to ${TARGET_CONTAINER}"
echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | tee /home/ec2-user/service_url.inc

echo "> Reloading nginx"
sudo service nginx reload

# 이전 컨테이너 종료
echo "> Stopping current container: ${CURRENT_CONTAINER}"
docker-compose -f /home/ec2-user/docker-compose.yml stop ${CURRENT_CONTAINER}

echo "> Deployment to ${TARGET_PORT} complete"
