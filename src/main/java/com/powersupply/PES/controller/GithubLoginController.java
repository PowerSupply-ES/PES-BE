package com.powersupply.PES.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Controller
public class GithubLoginController {

    @Value("${spring.security.oauth2.client.registration.github.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.clientSecret}")
    private String clientSecret;

    // 로그인 시 Code 받아오기
    @GetMapping("/login/oauth2/code/github")
    public String getCode(@RequestParam String code, RedirectAttributes redirectAttributes) throws IOException {
        // RedirectAttributes : 리다이렉트 시에 데이터를 전달하는 데 사용되는 객체

        URL url = new URL("https://github.com/login/oauth/access_token");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            bufferedWriter.write("client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code);
            bufferedWriter.flush();
        }

        int responseCode = connection.getResponseCode();

        String responseData = getResponse(connection, responseCode);

        connection.disconnect();

        System.out.println("responseCode = " + responseCode + " responseData = " + responseData);

        // access 함수를 호출하여 응답 데이터를 처리, 리다이렉트 시 필요한 데이터를 redirctAttributes에 추가
        access(responseData, redirectAttributes);
        return "redirect:/userdata";
    }

    // 응답 읽기
    private String getResponse(HttpURLConnection connection, int responseCode) throws IOException {

        // 응답 데이터 저장
        StringBuilder stringBuilder = new StringBuilder();
        if(responseCode == 200) {
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                    stringBuilder.append(line);
                }
            }
        }
        return stringBuilder.toString();
    }

    // access 토큰으로 정보 받아오기
    private void access(String response, RedirectAttributes redirectAttributes) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.readValue(response, Map.class);
        String access_token = map.get("access_token");

        URL url = new URL("https://api.github.com/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "token " + access_token);

        int responseCode = connection.getResponseCode();

        String result = getResponse(connection, responseCode);

        connection.disconnect();

        System.out.println("result = " + result);
        redirectAttributes.addFlashAttribute("result", result); // 응답 데이터를 리다이렉트 시에 전달하기위해 속성을 추가
    }

    // 정보 출력 창 연결
    @GetMapping("/userdata")
    private String userData(HttpServletRequest request, Model model) throws JsonProcessingException {

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        String response = null;
        if (inputFlashMap != null) {
            response = (String) inputFlashMap.get("result");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> result = objectMapper.readValue(response, Map.class);

        System.out.println("result2 = " + result);
        model.addAttribute("result", result);
        return "userdata";
    }
}
