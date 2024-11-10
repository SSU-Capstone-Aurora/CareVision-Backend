package aurora.carevisionapiserver.global.util;

import java.util.HashMap;
import java.util.Map;

public class ParseUtil {
    public static Map<String, Long> parseBedInfo(String bedInfo) {
        String[] parts = bedInfo.split(" ");

        // parts 길이가 2 이상 3 이하일 때만 처리
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("잘못된 bedInfo 형식입니다.");
        }

        // "동"이 있을 경우만 숫자로 변환
        Long inpatientWardNumber = null;
        if (parts[0].endsWith("동")) {
            inpatientWardNumber = Long.parseLong(parts[0].replace("동", ""));
        }

        // "호"가 없으면 예외 발생
        if (!parts[1].endsWith("호")) {
            throw new IllegalArgumentException("잘못된 환자실 정보입니다. '호'가 포함되어야 합니다.");
        }
        Long patientRoomNumber = Long.parseLong(parts[1].replace("호", ""));

        // "번"이 없으면 예외 발생
        if (!parts[2].endsWith("번")) {
            throw new IllegalArgumentException("잘못된 베드 번호입니다. '번'이 포함되어야 합니다.");
        }
        Long bedNumber = Long.parseLong(parts[2].replace("번", ""));

        Map<String, Long> bedInfoMap = new HashMap<>();
        bedInfoMap.put("inpatientWardNumber", inpatientWardNumber);
        bedInfoMap.put("patientRoomNumber", patientRoomNumber);
        bedInfoMap.put("bedNumber", bedNumber);

        return bedInfoMap;
    }
}
