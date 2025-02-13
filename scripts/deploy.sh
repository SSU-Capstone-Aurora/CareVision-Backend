#!/bin/bash
set -e  # 스크립트 실행 중 오류 발생 시 즉시 종료

# 변수 설정
COMPOSE_PATH="/home/ubuntu/docker-compose.override.yml"
NGINX_CONF_DIR="/home/ubuntu/nginx"
DELAY=10
NGINX_CONTAINER="nginx"

# 현재 실행 중인 블루 컨테이너 확인
BLUE_API_CONTAINER="$(docker ps --filter "name=carevision-blue" --filter "status=running" | grep -v "CONTAINER ID")"

# 컨테이너 환경 스위칭
if [[ -n "$BLUE_API_CONTAINER" ]]; then
    echo "-----------------------------"
    echo "전환: BLUE => GREEN"
    echo "-----------------------------"

    CURRENT_API_ENV='carevision-blue'
    NEW_API_ENV='carevision-green'

    CURRENT_NGINX_CONF='carevision.blue.conf'
    NEW_NGINX_CONF='carevision.green.conf'
else
    echo "-----------------------------"
    echo "전환: GREEN => BLUE"
    echo "-----------------------------"

    CURRENT_API_ENV='carevision-green'
    NEW_API_ENV='carevision-blue'

    CURRENT_NGINX_CONF='carevision.green.conf'
    NEW_NGINX_CONF='carevision.blue.conf'
fi

# 새로운 이미지 빌드 (캐시 무시)
echo
echo "-----------------------------"
echo "새로운 환경 이미지 빌드 중: $NEW_API_ENV (캐시 무시)"
sudo docker-compose -f "$COMPOSE_PATH" build --no-cache $NEW_API_ENV || { echo "❌ 이미지 빌드 실패"; exit 1; }
echo "✅ 이미지 빌드 완료"
echo "-----------------------------"
echo

# 새로운 컨테이너 시작
echo
echo "-----------------------------"
echo "새로운 환경 시작 중: $NEW_API_ENV"
sudo docker-compose -f "$COMPOSE_PATH" up -d --no-deps $NEW_API_ENV || { echo "❌ 새로운 환경 시작 실패"; exit 1; }
echo "✅ 새로운 환경 시작 완료"
echo "-----------------------------"
echo

# 컨테이너 시작 대기
sleep $DELAY

# 컨테이너 정상 실행 여부 확인
if ! docker ps --filter "name=$NEW_API_ENV" --filter "status=running" | grep -q "$NEW_API_ENV"; then
    echo "❌ 새로운 환경($NEW_API_ENV) 컨테이너가 정상 실행되지 않음"
    exit 1
fi

# Nginx 설정 파일 업데이트
echo
echo "-----------------------------"
echo "Nginx 설정 파일 업데이트 중..."
docker cp "$NGINX_CONF_DIR/$NEW_NGINX_CONF" "$NGINX_CONTAINER:/etc/nginx/conf.d/"

# 복사된 파일 확인
docker exec "$NGINX_CONTAINER" ls -l /etc/nginx/conf.d/

# 현재 설정 파일 삭제
docker exec "$NGINX_CONTAINER" rm -f "/etc/nginx/conf.d/$CURRENT_NGINX_CONF"
echo "-----------------------------"
echo


# Nginx 설정 테스트 및 리로드
echo
echo "-----------------------------"
echo "Nginx 설정 테스트 중..."
docker exec "$NGINX_CONTAINER" nginx -t || { echo "❌ Nginx 설정 오류"; exit 1; }
echo "✅ Nginx 설정 문제 없음"

echo "Nginx 리로드 중..."
sudo docker-compose -f "$COMPOSE_PATH" exec "$NGINX_CONTAINER" nginx -s reload || { echo "❌ Nginx 리로드 실패"; exit 1; }
echo "✅ Nginx 리로드 완료"
echo "-----------------------------"
echo

# 이전 환경 중지 및 제거
echo
echo "-----------------------------"
echo "이전 환경 중지 및 제거 중: $CURRENT_API_ENV"
sudo docker-compose -f "$COMPOSE_PATH" stop "$CURRENT_API_ENV" || { echo "❌ 이전 환경 중지 실패"; exit 1; }
sudo docker-compose -f "$COMPOSE_PATH" rm -f "$CURRENT_API_ENV" || { echo "❌ 이전 환경 제거 실패"; exit 1; }
echo "✅ 이전 환경 중지 및 제거 완료"
echo "-----------------------------"
echo

# 배포 완료 메시지 (여기까지 오면 성공한 것)
echo
echo "-----------------------------"
echo "✅ 배포가 완료되었습니다!"
echo "-----------------------------"
