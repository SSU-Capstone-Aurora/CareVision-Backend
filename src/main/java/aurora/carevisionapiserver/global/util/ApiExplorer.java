package aurora.carevisionapiserver.global.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.domain.hospital.exception.HospitalException;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;

@Component
public class ApiExplorer {

    @Value("${openapi.secretkey}")
    private String openApiKey;

    public StringBuilder callHospitalAPI(String hospitalName) throws IOException {
        StringBuilder urlBuilder =
                new StringBuilder(
                        "http://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList");
        urlBuilder.append(
                "?"
                        + URLEncoder.encode("ServiceKey", "UTF-8")
                        + "="
                        + URLEncoder.encode(openApiKey, "UTF-8"));
        urlBuilder.append(
                "&"
                        + URLEncoder.encode("yadmNm", "UTF-8")
                        + "="
                        + URLEncoder.encode(hospitalName, "UTF-8"));
        urlBuilder.append("&_type=json");
        return getData(urlBuilder);
    }

    public StringBuilder callDepartmentAPI(String ykiho) throws IOException {
        StringBuilder urlBuilder =
                new StringBuilder(
                        "http://apis.data.go.kr/B551182/MadmDtlInfoService2.7/getDgsbjtInfo2.7");
        urlBuilder.append(
                "?"
                        + URLEncoder.encode("serviceKey", "UTF-8")
                        + "="
                        + URLEncoder.encode(openApiKey, "UTF-8"));
        urlBuilder.append(
                "&"
                        + URLEncoder.encode("ykiho", "UTF-8")
                        + "="
                        + URLEncoder.encode(ykiho, "UTF-8"));
        urlBuilder.append("&_type=json");
        return getData(urlBuilder);
    }

    public StringBuilder getData(StringBuilder urlBuilder) throws IOException {
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        if (conn.getResponseCode() == 400) {
            throw new HospitalException(ErrorStatus.HOSPITAL_NOT_FOUND);
        }

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        return sb;
    }
}
