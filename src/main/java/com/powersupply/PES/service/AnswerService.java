package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.AnswerDTO;
import com.powersupply.PES.domain.entity.AnswerEntity;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.domain.entity.ProblemEntity;
import com.powersupply.PES.domain.entity.QuestionEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.AnswerRepository;
import com.powersupply.PES.repository.MemberRepository;
import com.powersupply.PES.repository.ProblemRepository;
import com.powersupply.PES.repository.QuestionRepository;
import com.powersupply.PES.utils.JwtUtil;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final QuestionRepository questionRepository;

    // 채점 하기
    @Transactional
    public ResponseEntity<?> submit(Long problemId, String memberStuNum, AnswerDTO.gitUrl dto) {
        if(!JwtUtil.getMemberStuNumFromToken().equals(memberStuNum)) {
            throw new AppException(ErrorCode.INVALID_INPUT,"채점할 권한이 없는 유저 입니다.");
        }

        // 깃 주소 비교 로직
        MemberEntity memberEntity = memberRepository.findByMemberStuNum(memberStuNum)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "멤버 정보를 찾을 수 없습니다."));

        // 깃 주소가 해당 url로 시작 하지 않으면 AppException 실행
        String gitUrl = dto.getAnswerUrl();
        if(!gitUrl.startsWith(memberEntity.getMemberGitUrl())) {
            throw new AppException(ErrorCode.INVALID_INPUT,"제출된 깃 주소가 유저의 깃 주소와 일치하지 않습니다.");
        }

        Optional<AnswerEntity> optionalAnswerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum,problemId);
        AnswerEntity answerEntity;

        // answer table에 저장되어 있는지 판단
        if (optionalAnswerEntity.isPresent()) {
            // DB에 있을 경우
            answerEntity = optionalAnswerEntity.get();

            // 문제가 이미 채점 중일 경우 오류 전송
            if(answerEntity.getAnswerState().equals("Grading")) {
                return ResponseUtil.noContentResponse("이미 채점 중입니다.");
            }

            answerEntity.setAnswerUrl(gitUrl);
            answerEntity.setAnswerState("Grading");
        } else {
            // 무작위 2개의 질문 선택
            Pageable pageable = PageRequest.of(0, 2, Sort.unsorted());
            List<QuestionEntity> questions = questionRepository.findByProblemEntity_ProblemId(problemId, pageable);

            if (questions.size() < 2) {
                throw new AppException(ErrorCode.INVALID_INPUT,"문제에 대한 충분한 질문이 없습니다.");
            }

            Optional<ProblemEntity> problemEntityOptional = problemRepository.findById(problemId);
            if (problemEntityOptional.isEmpty()) {
                throw new AppException(ErrorCode.NOT_FOUND, "문제 정보를 찾을 수 없습니다.");
            }
            ProblemEntity problemEntity = problemEntityOptional.get();

            // DB에 없을 경우 answer 생성 후 뮨제 상태 Grading으로 수정
            answerEntity = AnswerEntity.builder()
                    .memberEntity(memberEntity)
                    .problemEntity(problemEntity)
                    .answerState("Grading")
                    .answerUrl(gitUrl)
                    .questionFst(questions.get(0))
                    .questionSec(questions.get(1))
                    .build();
        }

        try {
            Long answerId = answerRepository.save(answerEntity).getAnswerId();

            // 채점 서버로 요청 전송
            sendCode(answerId, gitUrl);

            // 성공 응답 반환
            return ResponseEntity.ok().body("채점 요청을 성공적으로 보냈습니다.");
        } catch (Exception e) {
            // 예외 발생 시 에러 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("채점 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

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
}
