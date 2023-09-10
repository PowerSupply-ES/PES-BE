package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // join 페이지 가져오기
    @GetMapping("/signup")
    public String getSignUp() {
        return "signup";
    }

    // join 정보 저장
    @PostMapping("/signup")
    public String postSignUp(@RequestBody MemberDTO.MemberSignUpRequest dto) {
        memberService.signUp(dto);

        // 회원가입 완료시 로그인 페이지로 이동
        return "redirect:/signin";
    }

    // login 페이지 가져오기
    @GetMapping("/signin")
    public String getSignIn() {
        return "signin";
    }

    // login 진행
    @PostMapping("/signin")
    public String postSignIn(@RequestBody MemberDTO.MemberSignInRequest dto) {
        memberService.signIn(dto);

        return "index";
    }
}
