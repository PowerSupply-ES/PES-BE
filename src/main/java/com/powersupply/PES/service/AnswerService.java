package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.AnswerDTO;
import com.powersupply.PES.domain.dto.CommentDTO;
import com.powersupply.PES.domain.entity.*;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.*;
import com.powersupply.PES.utils.JwtUtil;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final QuestionRepository questionRepository;
    private final CommentRepository commentRepository;

    // answer 만들기
    @Transactional
    public AnswerDTO.GetAnswerId createAnswer(String id, Long problemId) {

        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "멤버 정보를 찾을 수 없습니다."));

        // DB에 해당 id의 problemId의 answer이 있는지 확인
        if (answerRepository.findByMemberEntity_MemberIdAndProblemEntity_ProblemId(id, problemId).isPresent()) {
            throw new AppException(ErrorCode.BAD_REQUEST,"해당 내용은 이미 있습니다.");
        }

        // 무작위 2개의 질문 선택
        List<QuestionEntity> questions = questionRepository.findByProblemEntity_ProblemId(problemId);

        // 질문이 2개보다 적은 경우
        if (questions.size() < 2) {
            log.error("문제에 대한 충분한 질문이 없습니다.");
            throw new AppException(ErrorCode.INVALID_INPUT,"문제에 대한 충분한 질문이 없습니다.");
        }
        Collections.shuffle(questions);
        List<QuestionEntity> selectedQuestions = questions.subList(0, 2);

        // 문제 연결을 위한 Entity 찾기
        Optional<ProblemEntity> problemEntityOptional = problemRepository.findById(problemId);
        if (problemEntityOptional.isEmpty()) {
            log.error("문제 정보를 찾을 수 없습니다.");
            throw new AppException(ErrorCode.NOT_FOUND, "문제 정보를 찾을 수 없습니다.");
        }
        ProblemEntity problemEntity = problemEntityOptional.get();

        // DB에 없을 경우 answer 생성 후 뮨제 상태 Grading으로 수정
        AnswerEntity answerEntity = AnswerEntity.builder()
                .memberEntity(memberEntity)
                .problemEntity(problemEntity)
                .answerState("test")
                .questionFst(selectedQuestions.get(0))
                .questionSec(selectedQuestions.get(1))
                .build();
        Long answerId = answerRepository.save(answerEntity).getAnswerId();

        return AnswerDTO.GetAnswerId.builder()
                .answerId(answerId)
                .build();
    }

    // 질문 답변 가져오기
    public AnswerDTO.GetAnswer getAnswer(Long answerId) {
        AnswerEntity answerEntity = answerRepository.findById(answerId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 answerId가 없음"));

        return AnswerDTO.GetAnswer.builder()
                .questionContentFst(answerEntity.getQuestionFst().getQuestionContent())
                .questionContentSec(answerEntity.getQuestionSec().getQuestionContent())
                .answerFst(answerEntity.getAnswerFst())
                .answerSec(answerEntity.getAnswerSec())
                .answerState(answerEntity.getAnswerState())
                .build();
    }

    // 답변 하기
    public void postAnswer(Long answerId, AnswerDTO.AnswerContent dto) {
        String id = JwtUtil.getMemberIdFromToken();

        AnswerEntity answerEntity = answerRepository.findById(answerId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 answerId가 없음"));

        if(!id.equals(answerEntity.getMemberEntity().getMemberId())) {
            throw new AppException(ErrorCode.FORBIDDEN,"아이디가 다름");
        }

        if (dto.getAnswerFst() == null || dto.getAnswerFst().isEmpty() ||
                dto.getAnswerSec() == null || dto.getAnswerSec().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "답변 내용 중 하나 또는 둘 다 비어 있음");
        }

        Optional<List<CommentEntity>> commentEntitiesOptional = commentRepository.findByAnswerEntity_AnswerId(answerId);

        // 댓글이 있는 경우
        if (commentEntitiesOptional.isPresent() && !commentEntitiesOptional.get().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "이미 댓글이 있어 수정 불가능");
        }

        answerEntity.setAnswerFst(dto.getAnswerFst());
        answerEntity.setAnswerSec(dto.getAnswerSec());
        answerEntity.setAnswerState("comment");

        answerRepository.save(answerEntity);
    }

    // answerList 가져오기
    @Transactional
    public ResponseEntity<?> getAnswerList(Long problemId) {
        // problemId 조회
        ProblemEntity problemEntity = problemRepository.findById(problemId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND,"problemId가 잘못 됨"));

        // answerEntity 가져오기
        List<AnswerEntity> answerEntityList= answerRepository.findAllByProblemEntity_ProblemId(problemId);

        if(answerEntityList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<AnswerDTO.GetAnswerList> answerLists = new ArrayList<>();

        for(AnswerEntity answerEntity: answerEntityList) {
            AnswerDTO.GetAnswerList answerList = AnswerDTO.GetAnswerList.builder()
                    .answerId(answerEntity.getAnswerId())
                    .memberGen(answerEntity.getMemberEntity().getMemberGen())
                    .memberName(answerEntity.getMemberEntity().getMemberName())
                    .commentCount(answerEntity.getCommentEntities().size())
                    .answerState(answerEntity.getAnswerState())
                    .build();
            answerLists.add(answerList);
        }
        return ResponseEntity.ok(answerLists);
    }
/*
    // 채점 서버로 요청 전송 하기
    private void sendCode(Long answerId, String gitUrl) throws IOException {
        String requestURL = "http://www.pes23.com:5000/api/v2/submit";

        URL url = new URL(requestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            // POST 요청을 위한 설정
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // JSON 바디 데이터
            String jsonInputString = "{\"answerId\": " + answerId + ", \"answerUrl\": \"" + gitUrl + "\"}";

            // 요청 바디에 데이터 쓰기
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인 및 응답 내용 읽기
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 요청 성공
                // 응답 내용을 처리하는 로직을 여기에 작성하세요
                System.out.println("Response Code : " + responseCode);
                System.out.println("Response Message : " + conn.getResponseMessage());
            } else {
                // 요청 실패
                System.out.println("Request did not work: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    // 채점 결과 받기
    public void returnSubmit(AnswerDTO.returnSubmit dto) {
        // 채점 결과
        AnswerEntity answerEntity = answerRepository.findById(dto.getAnswerId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 Id가 없다."));

        // 채점 결과에 따라
        if(dto.getAnswerState() == 1) {
            // 성공 시 로직
            answerEntity.setAnswerState("Answerme");
        } else {
            // 실패 시 로직
            // 추후 시도 횟수 추가
//            try {
//                // 숫자에 1 더해서 저장
//                int currentState = Integer.parseInt(answerEntity.getAnswerState());
//                currentState += 1;
//                answerEntity.setAnswerState(String.valueOf(currentState));
//            } catch (NumberFormatException e) {
//                // 숫자가 아닌 문자열이 저장되어 있을 경우
//                throw new AppException(ErrorCode.INVALID_INPUT, "answerState 값이 유효한 숫자 형식이 아닙니다.");
//            }
            answerEntity.setAnswerState("1");
        }
        answerRepository.save(answerEntity);
    }


    public void saveAnswer(Long problemId, String memberStuNum, AnswerDTO.answerRequest dto) {
        if(!JwtUtil.getMemberStuNumFromToken().equals(memberStuNum)) {
            throw new AppException(ErrorCode.INVALID_INPUT,"답변할 권한이 없는 유저 입니다.");
        }

        AnswerEntity answerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum,problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "아직 채점되지 않았습니다."));

        // answerFst와 answerSec가 null이 아닌 경우에 AppException 발생
        if(answerEntity.getAnswerFst() != null || answerEntity.getAnswerSec() != null) {
            throw new AppException(ErrorCode.INVALID_INPUT, "이미 답변이 있는 경우 수정해 주세요.");
        }

        answerEntity.setAnswerState("UnderReview");
        answerEntity.setAnswerFst(dto.getAnswerFst());
        answerEntity.setAnswerSec(dto.getAnswerSec());
        answerRepository.save(answerEntity);
    }

    @Transactional
    public void patchAnswer(Long problemId, String memberStuNum, AnswerDTO.answerRequest dto) {
        if(!JwtUtil.getMemberStuNumFromToken().equals(memberStuNum)) {
            throw new AppException(ErrorCode.INVALID_INPUT,"수정 권한이 없는 유저 입니다.");
        }

        AnswerEntity answerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum,problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "아직 채점되지 않았습니다."));

        // commentEntities가 존재하는 경우에 AppException 발생
        if(!answerEntity.getCommentEntities().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "이미 코멘트가 있는 답변은 수정할 수 없습니다.");
        }

        answerEntity.setAnswerFst(dto.getAnswerFst());
        answerEntity.setAnswerSec(dto.getAnswerSec());
        answerRepository.save(answerEntity);
    }

    // (재)풀이 목록 보기
    @Transactional
    public List<AnswerDTO.SolveList> getSolveList(Long problemId) {
        List<AnswerEntity> answerEntityList = answerRepository.findAllByProblemEntity_ProblemId(problemId);
        List<AnswerDTO.SolveList> solveLists = new ArrayList<>();
        for(AnswerEntity answerEntity: answerEntityList) {
            int commentCount = 0;
            // AnswerEntity와 CommentEntity가 연관 관계에 있다면
            if(answerEntity.getCommentEntities() != null) {
                commentCount = answerEntity.getCommentEntities().size();
            }

            AnswerDTO.SolveList solveList = AnswerDTO.SolveList.builder()
                    .memberStuNum(answerEntity.getMemberEntity().getMemberStuNum())
                    .memberName(answerEntity.getMemberEntity().getMemberName())
                    .commentCount(commentCount)
                    .answerState(answerEntity.getAnswerState())
                    .build();
            solveLists.add(solveList);
        }
        return solveLists;
    }


 */
}
