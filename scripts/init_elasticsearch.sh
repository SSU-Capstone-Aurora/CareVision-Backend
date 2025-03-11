#!/bin/bash
# ⚠️ 인덱스 삭제 후 다시 생성 ⚠️

# Elasticsearch 서버 정보
ES_HOST="http://localhost:9200"

# 인덱스 목록
INDICES=("patient" "nurse")

# 매핑 파일 경로
RESOURCE_DIR="../src/main/resources/elasticsearch"
PATIENT_MAPPING_FILE="$RESOURCE_DIR/patient-mapping.json"
NURSE_MAPPING_FILE="$RESOURCE_DIR/nurse-mapping.json"

# 인덱스 삭제 함수
delete_index() {
  local index_name=$1
  echo "🗑 인덱스 삭제 중: $index_name"
  curl -X DELETE "$ES_HOST/$index_name" -s -o /dev/null
  echo "✅ $index_name 인덱스가 삭제되었습니다."
}

# 인덱스 생성 함수
create_index() {
  local index_name=$1
  local mapping_file=$2

  if [ ! -f "$mapping_file" ]; then
    echo "❌ 매핑 파일을 찾을 수 없습니다: $mapping_file"
    exit 1
  fi

  local mapping=$(cat "$mapping_file")

  echo "📌 인덱스 생성 중: $index_name"
  curl -X PUT "$ES_HOST/$index_name" -H "Content-Type: application/json" -d "$mapping"
  echo -e "\n✅ $index_name 인덱스 생성되었습니다."
}

# 메인 로직
for index in "${INDICES[@]}"; do
  delete_index "$index"
  if [ "$index" == "patient" ]; then
    create_index "$index" "$PATIENT_MAPPING_FILE"
  elif [ "$index" == "nurse" ]; then
    create_index "$index" "$NURSE_MAPPING_FILE"
  fi
done