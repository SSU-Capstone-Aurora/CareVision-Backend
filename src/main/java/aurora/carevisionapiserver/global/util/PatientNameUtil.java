package aurora.carevisionapiserver.global.util;

import java.util.Random;

public class PatientNameUtil {
    private static final String[] FIRST_NAMES = {
        "김", "이", "박", "최", "정", "강", "조", "윤", "장", "임",
        "한", "오", "서", "신", "권", "황", "안", "송", "류", "홍",
        "구", "남", "도", "심", "백", "방", "원", "변", "현", "손"
    };

    private static final String[] LAST_NAMES = {
        "민준", "서준", "도윤", "예준", "시우", "주원", "하준", "지호", "준서", "준우",
        "현우", "지훈", "건우", "우진", "선우", "민호", "현준", "지환", "동현", "민규",
        "성현", "승현", "재윤", "은우", "지우", "현성", "정우", "승준", "시현", "민성",
        "태현", "준혁", "재현", "성민", "지훈", "동건", "준영", "민재", "시윤", "현진"
    };

    public static String generateRandomName(String seed) {
        Random random = new Random(seed.hashCode());

        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];

        return firstName + lastName;
    }
}
