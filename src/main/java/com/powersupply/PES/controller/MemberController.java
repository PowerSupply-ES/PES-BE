package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.service.MemberService;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 정보 저장
    @PostMapping("/api/signup")
    public ResponseEntity<?> postSignUp(@RequestBody MemberDTO.MemberSignUpRequest dto) {
        String name = memberService.signUp(dto);

        return ResponseUtil.createResponse(name + "님, 가입에 성공했습니다.");
    }

    // 로그인 진행
    @PostMapping("/api/signin")
    public ResponseEntity<?> postSignIn(HttpServletResponse response, @RequestBody MemberDTO.MemberSignInRequest dto) {
        String token = memberService.signIn(dto);

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setMaxAge(60 * 60); // 쿠키 유효 시간 (1시간)
        cookie.setSecure(true); // HTTPS에서만 쿠키 사용
        cookie.setPath("/"); // 도메인 전체에서 사용 가능하도록 설정
        cookie.setHttpOnly(true); // JavaScript에서 쿠키에 접근할 수 없도록 설정
        response.addCookie(cookie);

        return ResponseUtil.successResponse("로그인에 성공했습니다.");
    }

    // 로그아웃 진행
    @PostMapping("/api/logout")
    public ResponseEntity<?> postLogout(HttpServletResponse response) {
        Cookie logoutCookie = new Cookie("Authorization", null);
        logoutCookie.setMaxAge(0); // 쿠키 즉시 만료
        logoutCookie.setSecure(true);
        logoutCookie.setPath("/");
        logoutCookie.setHttpOnly(true);
        response.addCookie(logoutCookie);

        return ResponseUtil.successResponse("로그아웃 되었습니다.");
    }

    // 마이페이지(정보)
    @GetMapping("/api/mypage/information")
    public ResponseEntity<MemberDTO.MemberMyPageResponse> getMyPageInfo() {

        return ResponseEntity.ok().body(memberService.getMyPage());
    }

    // 마이페이지(내가 푼 문제)
    @GetMapping("/api/mypage/mysolve")
    public ResponseEntity<?> getMySolveInfo() {

        return memberService.getMySolve();
    }

    // 마이페이지(나의 피드백)
    @GetMapping("/api/mypage/myfeedback")
    public ResponseEntity<?> getMyFeedbackInfo() {

        return memberService.getMyFeedback();
    }

    // 상단 사용자 정보
    @GetMapping("/api/exp")
    public ResponseEntity<MemberDTO.NameScoreResponse> myUser() {
        return ResponseEntity.ok().body(memberService.expVar());
    }

    // 랭킹 가져오기
    @GetMapping("/api/rank")
    public ResponseEntity<List<MemberDTO.Rank>> getRank(@RequestParam(value = "memberGen", required = false) Integer memberGen) {
        // year이 null이면 현재 현재 기수로 설정
        if (memberGen == null) {
            memberGen = LocalDate.now().getYear() - 1989;
        }

        List<MemberDTO.Rank> rank = memberService.getRank(memberGen);
        if (rank.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(rank);
    }

    // 재학생 랭킹 가져오기
    @GetMapping("/api/senior")
    public ResponseEntity<List<MemberDTO.Rank>> getSeniorRank() {
        List<MemberDTO.Rank> rank = memberService.getSeniorRank();
        if (rank.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(rank);
    }

}


