package crush.myList.domain.autocomplete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j(topic = "AutocompleteService")
@RequiredArgsConstructor
public class AutocompleteAPI {
    public static String KOREAN = "ko";
    public static String ENGLISH = "en";

    /** XML에서 문장 정보를 파싱하여 리스트로 반환합니다. */
    public static List<String> getList(String xml) throws Exception {
        List<String> list = new ArrayList<>();

        // XML 데이터 파싱을 위한 초기화
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xml));
        Document document = builder.parse(inputSource);

        // 존재하는 모든 결과 문장을 리스트에 담아서 반환
        NodeList nodeList = document.getElementsByTagName("suggestion");
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i).getAttributes().getNamedItem("data").getNodeValue());
        }
        return list;
    }

    /** 구글 자동완성 API */
    public static String getAutocompleteGoogle(String language, String text) {
        try {
            // 변환할 문장을 UTF-8로 인코딩
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            // 구글 자동완성 API 서버로부터 번역된 메시지를 받아옴
            BufferedReader br = getBufferedReader(language, encodedText);

            // 전달받은 메시지를 출력합니다.
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();

        } catch (Exception e) {
            log.error("AutocompleteService.getAutocomplete() : {}", e.getMessage());
            throw new RuntimeException("구글 자동완성 API 서버로부터 응답을 받지 못했습니다.");
        }
    }

    private static BufferedReader getBufferedReader(String language, String encodedText) throws IOException {
        URL url = new URL("https://suggestqueries.google.com/complete/search?output=toolbar&hl=" + language + "&q=" + encodedText);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // 구글 자동완성 API 서버로부터 번역된 메시지를 전달 받습니다.
        int responseCode = con.getResponseCode();
        if (responseCode == 200) { // 정상 응답
            return new BufferedReader(new InputStreamReader(con.getInputStream(), "EUC-KR"));
        } else {  // 에러 응답
            return new BufferedReader(new InputStreamReader(con.getErrorStream(), "EUC-KR"));
        }
    }
}
