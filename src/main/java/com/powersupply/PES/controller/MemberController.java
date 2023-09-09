package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // join 페이지 가져오기
    @GetMapping("/join")
    public String getJoin() {
        return "join";
    }

    // join 정보 저장
    @PostMapping("/join")
    public String postJoin(@RequestBody MemberDTO.MemberJoinRequest dto) {
        memberService.join(dto);

        // 회원가입 완료시 로그인 페이지로 이동
        return "redirect:/member/login/";
    }

    // login 페이지 가져오기
    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    // login 진행
    @PostMapping("/login")
    public String postLogin(@RequestBody MemberDTO.MemberLoginRequest dto) {
        memberService.login(dto);

        return "index";
    }
}
