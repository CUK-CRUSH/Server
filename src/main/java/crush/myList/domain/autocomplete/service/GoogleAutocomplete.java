package crush.myList.domain.autocomplete.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

@Service
@Slf4j(topic = "AutocompleteService")
@RequiredArgsConstructor
public class GoogleAutocomplete implements Autocomplete {
    public static final String KOREAN = "ko";
    public static final String ENGLISH = "en";

    /** XML에서 문장 정보를 파싱하여 리스트로 반환합니다. */
    public List<String> getList(String xml, int maxSize) throws Exception {
        List<String> list = new ArrayList<>();

        // XML 데이터 파싱을 위한 초기화
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xml));
        Document document = builder.parse(inputSource);

        // 존재하는 모든 결과 문장을 리스트에 담아서 반환
        NodeList nodeList = document.getElementsByTagName("suggestion");
        for (int i = 0; i < nodeList.getLength() && i < maxSize; i++) {
            list.add(nodeList.item(i).getAttributes().getNamedItem("data").getNodeValue());
        }
        return list;
    }

    /** 구글 자동완성 API */
    @Override
    public List<String> getAutocomplete(String language, String text, int maxSize) throws ResponseStatusException {
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
            return getList(response.toString(), maxSize);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "구글 자동완성 API 서버와 통신에 실패했습니다. " + e.getMessage());
        }
    }

    private BufferedReader getBufferedReader(String language, String encodedText) throws IOException {
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
