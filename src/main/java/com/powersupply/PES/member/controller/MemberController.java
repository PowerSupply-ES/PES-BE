package com.powersupply.PES.member.controller;

import com.powersupply.PES.member.dto.MemberDTO;
import com.powersupply.PES.member.service.MemberService;
import com.powersupply.PES.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입 정보 저장", description = "회원가입 정보를 저장하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "서버에 해당 이메일,번호, 아이디가 겹치는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/signup")
    public ResponseEntity<?> postSignUp(@RequestBody MemberDTO.MemberSignUpRequest dto) {
        String name = memberService.signUp(dto);

        return ResponseUtil.createResponse(name + "님, 가입에 성공했습니다.");
    }

    @Operation(summary = "로그인 진행", description = "로그인 시 jwt 토큰을 반환해 로그인을 승인하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "로그아웃 진행", description = "로그아웃을 진행하는 메서드입니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
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

    @Operation(summary = "마이페이지 정보 조회", description = "클라이언트가 요청한 마이페이지 정보를 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보가 존재하여 반환에 성공"),
            @ApiResponse(responseCode = "401", description = "회원 정보가 존재하지 않는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/mypage/information")
    public ResponseEntity<MemberDTO.MemberMyPageResponse> getMyPageInfo() {
        return ResponseEntity.ok().body(memberService.getMyPage());
    }

    @Operation(summary = "마이페이지 내가 푼 문제 조회", description = "클라이언트가 요청한 내가 푼 문제 정보를 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "204", description = "리스트가 비어있는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/mypage/mysolve")
    public ResponseEntity<?> getMySolveInfo() {
        return memberService.getMySolve();
    }

    @Operation(summary = "마이페이지 나의 피드백 조회", description = "클라이언트가 요청한 나의 피드백 정보를 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "204", description = "리스트가 비어있는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/mypage/myfeedback")
    public ResponseEntity<?> getMyFeedbackInfo() {
        return memberService.getMyFeedback();
    }

    @Operation(summary = "상단 사용자 정보 조회", description = "상단 사용자 정보를 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "204", description = "리스트가 비어있는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/exp")
    public ResponseEntity<MemberDTO.NameScoreResponse> myUser() {
        return ResponseEntity.ok().body(memberService.expVar());
    }

    @Operation(summary = "주니어 랭킹 조회", description = "주니어 회원의 랭킹을 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "204", description = "리스트가 비어있는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/rank/junior")
    public ResponseEntity<List<MemberDTO.Rank>> getRank(@RequestParam(value = "memberGen", required = false) Integer memberGen) {
        if (memberGen == null) {
            memberGen = LocalDate.now().getYear() - 1989;
        }

        List<MemberDTO.Rank> rank = memberService.getRank(memberGen);
        if (rank.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(rank);
    }

    @Operation(summary = "시니어 랭킹 조회", description = "시니어 회원의 랭킹을 조회하는 메서드입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "204", description = "리스트가 비어있는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/rank/senior")
    public ResponseEntity<List<MemberDTO.Rank>> getSeniorRank() {
        List<MemberDTO.Rank> rank = memberService.getSeniorRank();
        if (rank.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(rank);
    }
}
