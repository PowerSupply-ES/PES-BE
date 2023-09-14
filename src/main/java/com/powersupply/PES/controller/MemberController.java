package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.service.MemberService;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 페이지 가져오기
    @GetMapping("/signup")
    public String getSignUp() {
        return "signup";
    }

    // 회원가입 정보 저장
    @PostMapping("/signup")
    public ResponseEntity<String> postSignUp(@RequestBody MemberDTO.MemberSignUpRequest dto) {
        String name = memberService.signUp(dto);

        // 회원가입 완료시 로그인 페이지로 이동
        return ResponseUtil.successResponse(name + "님, 가입에 성공했습니다.");
    }

    // login 페이지 가져오기
    @GetMapping("/signin")
    public String getSignIn() {
        return "signin";
    }

    // login 진행
    @PostMapping("/signin")
    public String postSignIn(HttpServletResponse response, @RequestBody MemberDTO.MemberSignInRequest dto) {
        String token = memberService.signIn(dto);

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setMaxAge(60 * 60); // 쿠키 유효 시간 (예: 1시간)
        cookie.setSecure(true); // HTTPS에서만 쿠키 사용
        cookie.setHttpOnly(true); // JavaScript에서 쿠키에 접근할 수 없도록 설정
        response.addCookie(cookie);

        return "index";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String getMyPage(Model model) {
        MemberDTO.MemberMyPageResponse myPageResponse = memberService.getMyPage();

        model.addAttribute("member", myPageResponse);
        return "mypage";
    }
}
