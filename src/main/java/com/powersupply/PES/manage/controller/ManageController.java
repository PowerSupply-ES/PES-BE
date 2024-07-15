package com.powersupply.PES.manage.controller;

import com.powersupply.PES.manage.dto.ManageDTO;
import com.powersupply.PES.member.entity.MemberEntity;
import com.powersupply.PES.manage.service.ManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Manage", description = "관리 기능 API")
public class ManageController {

    private final ManageService manageService;

    /* ---------- 문제 관리 기능 ---------- */

    @Operation(summary = "전체 문제 리스트 불러오기", description = "전체 문제 리스트를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/problemlist")
    public ResponseEntity<List<ManageDTO.ProblemList>> problemList() {
        List<ManageDTO.ProblemList> problemList = manageService.problemList();
        return ResponseEntity.ok().body(problemList);
    }

    @Operation(summary = "특정 문제 detail 불러오기", description = "특정 문제의 상세 정보를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 problemId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<ManageDTO.ProblemDetail> problemDetail(
            @Parameter(description = "문제 ID", example = "1") @PathVariable Long problemId) {
        return ResponseEntity.ok().body(manageService.problemDetail(problemId));
    }

    @Operation(summary = "문제 등록하기", description = "새로운 문제를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/problem")
    public ResponseEntity<?> postProblem(@RequestBody ManageDTO.ProblemRequestDto requestDto) throws IOException {
        return ResponseEntity.ok(manageService.postProblem(requestDto));
    }

    @Operation(summary = "문제 수정하기", description = "기존 문제를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 problemId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/problem/{problemId}")
    public ResponseEntity<?> patchProblem(
            @Parameter(description = "문제 ID", example = "1") @PathVariable Long problemId,
            @RequestBody ManageDTO.ProblemRequestDto requestDto) {
        return manageService.patchProblem(problemId, requestDto);
    }

    /* ---------- 회원 관리 기능 ---------- */

    @Operation(summary = "전체 멤버 리스트 불러오기", description = "전체 멤버 리스트를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/memberlist")
    public ResponseEntity<List<ManageDTO.MemberList>> list() {
        List<ManageDTO.MemberList> memberList = manageService.list();
        return ResponseEntity.ok().body(memberList);
    }

    @Operation(summary = "특정 멤버 detail 불러오기", description = "특정 멤버의 상세 정보를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 memberId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ManageDTO.MemberDetail> readDetail(
            @Parameter(description = "멤버 ID", example = "user123") @PathVariable String memberId) {
        return ResponseEntity.ok().body(manageService.readDetail(memberId));
    }

    @Operation(summary = "멤버 삭제하기", description = "특정 멤버를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "해당 memberId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<?> deleteMember(
            @Parameter(description = "멤버 ID", example = "user123") @PathVariable String memberId) {
        return manageService.deleteMember(memberId);
    }

    @Operation(summary = "멤버 상태 수정하기", description = "특정 멤버의 상태를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "해당 memberId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/member/{memberId}")
    public ResponseEntity<MemberEntity> updateMemberStatus(
            @Parameter(description = "멤버 ID", example = "user123") @PathVariable String memberId,
            @RequestBody ManageDTO.MemberUpdateRequestDto requestDto) {
        return ResponseEntity.ok(manageService.updateMemberStatus(memberId, requestDto));
    }

    /* ---------- 질문 관리 기능 ---------- */

    @Operation(summary = "문제 별 질문 목록 가져오기", description = "특정 문제에 대한 질문 목록을 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/questionlist/{problemId}")
    public ResponseEntity<List<ManageDTO.QuestionList>> questionList(
            @Parameter(description = "문제 ID", example = "1") @PathVariable Long problemId) {
        List<ManageDTO.QuestionList> questionList = manageService.questionList(problemId);
        return ResponseEntity.ok().body(questionList);
    }

    @Operation(summary = "질문 등록하기", description = "특정 문제에 대해 질문을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "관리자 권한이 없는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/question/{problemId}")
    public ResponseEntity<?> postQuestion(
            @Parameter(description = "문제 ID", example = "1") @PathVariable Long problemId,
            @RequestBody ManageDTO.QuestionRequestDto requestDto) {
        return ResponseEntity.ok(manageService.postQuestion(problemId, requestDto));
    }

    @Operation(summary = "질문 수정하기", description = "기존 질문을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 questionId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/question/{questionId}")
    public ResponseEntity<?> patchQuestion(
            @Parameter(description = "질문 ID", example = "1") @PathVariable Long questionId,
            @RequestBody ManageDTO.QuestionRequestDto requestDto) {
        return ResponseEntity.ok(manageService.updateQuestion(questionId, requestDto));
    }

    @Operation(summary = "질문 삭제하기", description = "특정 질문을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 questionId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<?> deleteQuestion(
            @Parameter(description = "질문 ID", example = "1") @PathVariable Long questionId) {
        return ResponseEntity.ok(manageService.deleteQuestion(questionId));
    }
}
