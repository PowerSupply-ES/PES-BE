package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.domain.entity.AnswerEntity;
import com.powersupply.PES.domain.entity.CommentEntity;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.AnswerRepository;
import com.powersupply.PES.repository.CommentRepository;
import com.powersupply.PES.repository.MemberRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.secret}")
    private String secretKey;

    // 회원가입
    public String signUp(MemberDTO.MemberSignUpRequest dto) {

        String id = dto.getMemberId();
        String email = dto.getMemberEmail();
        String phone = dto.getMemberPhone();
        String pw = dto.getMemberPw();
        String name = dto.getMemberName();

        // id 및 password 빈칸 체크
        if (id.isBlank() || pw.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "필수 입력 사항을 입력해 주세요.");
        }

        // memberId 중복 체크
        memberRepository.findById(id)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.BAD_REQUEST, "이미 가입된 아이디입니다.");
                });

        // memberEmail 중복 체크
        memberRepository.findByMemberEmail(email)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.BAD_REQUEST, "이미 사용 중인 이메일입니다.");
                });

        // memberPhone 중복 체크
        memberRepository.findByMemberPhone(phone)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.BAD_REQUEST, "이미 사용 중인 전화번호입니다.");
                });

        // MemberEntity 생성
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(id)
                .memberEmail(email)
                .memberName(name)
                .memberPw(encoder.encode(pw)) // 비밀번호는 암호화하여 저장
                .memberGen(dto.getMemberGen())
                .memberMajor(dto.getMemberMajor())
                .memberPhone(phone)
                .memberStatus("신입생")
                .build();
        memberRepository.save(memberEntity);

        return name;
    }

    // 로그인
    public String signIn(MemberDTO.MemberSignInRequest dto) {
        String id = dto.getMemberId();
        String pw = dto.getMemberPw();
        Long expireTimeMs = 1000 * 60 * 60l;

        // id 및 password 빈칸 체크
        if (id.isBlank() || pw.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "필수 입력 사항을 입력해 주세요.");
        }

        // id 없는 경우
        MemberEntity selectedMember = memberRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.INVALID_INPUT, "로그인에 실패했습니다."));

        // password 틀린 경우
        if(!encoder.matches(pw, selectedMember.getMemberPw())){
            throw new AppException(ErrorCode.INVALID_INPUT, "로그인에 실패했습니다.");
        }

        return JwtUtil.createToken(selectedMember.getMemberId(), selectedMember.getMemberStatus(), secretKey, expireTimeMs);
    }

//    public MemberEntity findByMemberEmail(String memberEmail) {
//        return memberRepository.findByMemberEmail(memberEmail)
//                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND, "해당 학번은 등록되지 않았습니다."));
//    }


    // 마이페이지 가져오기
    public MemberDTO.MemberMyPageResponse getMyPage() {
        MemberEntity memberEntity = memberRepository.findById(JwtUtil.getMemberIdFromToken())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT, "오류가 발생했습니다."));

        return MemberDTO.MemberMyPageResponse.builder()
                .memberId(memberEntity.getMemberId())
                .memberEmail(memberEntity.getMemberEmail())
                .memberName(memberEntity.getMemberName())
                .memberGen(memberEntity.getMemberGen())
                .memberStatus(memberEntity.getMemberStatus())
                .memberMajor(memberEntity.getMemberMajor())
                .memberPhone(memberEntity.getMemberPhone())
                .build();
    }

    public MemberDTO.NameScoreResponse expVar() {
        String id = JwtUtil.getMemberIdFromToken();

        MemberEntity selectedMember = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,"정보가 존재하지 않습니다."));

        Integer totalScore = answerRepository.sumFinalScoreById(id);

        return MemberDTO.NameScoreResponse.builder()
                .memberName(selectedMember.getMemberName())
                .memberStatus(selectedMember.getMemberStatus())
                .memberScore(totalScore != null ? totalScore : 0)
                .memberGen(selectedMember.getMemberGen())
                .build();
    }

    // 마이페이지(내가 푼 문제)
    @Transactional
    public ResponseEntity<?> getMySolve() {
        String id = JwtUtil.getMemberIdFromToken();

        List<AnswerEntity> answerEntityList = answerRepository.findAllByMemberEntity_MemberId(id);

        if (answerEntityList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<MemberDTO.MemberMySolveResponse> mySolveResponseList = new ArrayList<>();

        for (AnswerEntity answerEntity: answerEntityList) {
            MemberDTO.MemberMySolveResponse mySolveResponse = MemberDTO.MemberMySolveResponse.builder()
                    .problemId(answerEntity.getProblemEntity().getProblemId())
                    .problemTitle(answerEntity.getProblemEntity().getProblemTitle())
                    .problemScore(answerEntity.getProblemEntity().getProblemScore())
                    .answerId(answerEntity.getAnswerId())
                    .answerState(answerEntity.getAnswerState())
                    .finalScore(answerEntity.getFinalScore())
                    .build();
            mySolveResponseList.add(mySolveResponse);
        }
        return ResponseEntity.ok().body(mySolveResponseList);
    }

    // 마이페이지(나의 피드백)
    @Transactional
    public ResponseEntity<?> getMyFeedback() {
        String id = JwtUtil.getMemberIdFromToken();

        List<CommentEntity> commentEntityList = commentRepository.findAllByMemberEntity_MemberId(id);

        if (commentEntityList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<MemberDTO.MemberMyFeedbackResponse> memberMyFeedbackResponseList = new ArrayList<>();

        for (CommentEntity commentEntity : commentEntityList) {
            MemberDTO.MemberMyFeedbackResponse myFeedbackResponse = MemberDTO.MemberMyFeedbackResponse.builder()
                    .answerId(commentEntity.getAnswerEntity().getAnswerId())
                    .memberGen(commentEntity.getAnswerEntity().getMemberEntity().getMemberGen())
                    .memberName(commentEntity.getAnswerEntity().getMemberEntity().getMemberName())
                    .commentPassFail(commentEntity.getCommentPassFail())
                    .commentContent(commentEntity.getCommentContent())
                    .build();
            memberMyFeedbackResponseList.add(myFeedbackResponse);
        }
        return ResponseEntity.ok().body(memberMyFeedbackResponseList);
    }

//    public void findUser(MemberDTO.MemberFindPwRequest dto) {
//        String email = dto.getMemberEmail();
//        String name = dto.getMemberName();
//
//        // 학번 및 password 빈칸 체크
//        if (email.isBlank() || name.isBlank()) {
//            throw new AppException(ErrorCode.INVALID_INPUT, "필수 입력 사항을 입력해 주세요.");
//        }
//
//        // 학번과 이름이 DB에 없는 경우
//        MemberEntity selectedMember = memberRepository.findByMemberEmail(email).orElse(null);
//
//        if(selectedMember == null || !selectedMember.getMemberName().equals(name)){
//            throw new AppException(ErrorCode.INVALID_INPUT, "계정 찾기를 실패했습니다.");
//        }
//
//        // 추후 이메일로 임의 수정된 비밀번호 전송 로직 추가
//    }

    // 상단 사용자 정보 불러오기
//    public MemberDTO.NameScoreStatusResponse myUser() {
//        String email = JwtUtil.getMemberEmailFromToken();
//
//        MemberEntity selectedMember = memberRepository.findByMemberEmail(email)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,"정보가 존재하지 않습니다."));
//
//        return MemberDTO.NameScoreStatusResponse.builder()
//                .memberName(selectedMember.getMemberName())
//                .memberScore(selectedMember.getMemberScore())
//                .memberStatus(selectedMember.getMemberStatus())
//                .build();
//    }

//    public List<MemberDTO.NameScoreResponse> memberRank() {
//
//        List<MemberEntity> memberEntityList = memberRepository.findByMemberStatus("신입생");
//        List<MemberDTO.NameScoreResponse> nameScoreResponseList = new ArrayList<>();
//
//        // 리스트 체크
//        if(memberEntityList.isEmpty()) {
//            throw new AppException(ErrorCode.NOT_FOUND,"정보가 존재하지 않습니다.");
//        }
//
//        // 저장
//        for(MemberEntity memberEntity: memberEntityList) {
//            MemberDTO.NameScoreResponse dto = MemberDTO.NameScoreResponse.builder()
//                    .memberName(memberEntity.getMemberName())
//                    .memberScore(memberEntity.getMemberScore())
//                    .build();
//            nameScoreResponseList.add(dto);
//        }
//
//        // memberScore를 기준으로 정렬 (내림차순)
//        nameScoreResponseList.sort((o1, o2) -> Integer.compare(o2.getMemberScore(), o1.getMemberScore()));
//
//        return nameScoreResponseList;
//    }
}
