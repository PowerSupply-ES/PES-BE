import serverConfig from './config.js';

document.addEventListener("DOMContentLoaded", async function(event) {
    
    // localStorage에서 토큰 가져오기
    const storageToken = localStorage.getItem('storageToken');
    const memStuNum = localStorage.getItem('stuNum');

    const serverUrl = serverConfig.serverUrl; // serverUrl을 정의
    
    // 회원 상태 전역 변수로 선언
    let memstate;
    let problemId;

    // 상단 사용자 정보 함수
    async function fetchUserInfo(storageToken) {
        const userInfoUri = 'api/myuser';
        try {
            const response = await fetch(serverUrl + userInfoUri, {
                method: 'GET',
                headers: {
                    'Authorization': storageToken
                }
            });

            if (!response.ok) {
                throw new Error('사용자 정보 가져오기 실패');
            }
            

            const data = await response.json();
            // 사용자 정보를 화면에 표시
            document.querySelector(".memberName").textContent = "이름: " + data.memberName;
            document.querySelector(".memberScore").textContent = "획득한 점수: " + data.memberScore;
            document.querySelector(".memberStatus").textContent = "상태(신입생/재학생/관리자): " + data.memberStatus;
            
            // memberStatus 값을 memstate에 저장
            memstate = data.memberStatus;

        } catch (error) {
            console.error('사용자 정보 가져오기 오류:', error);
        }
    }

    // 랭킹 함수
    async function fetchRankInfo() {
        const rankInfoUri = 'api/rank';
        try {
            const response = await fetch(serverUrl + rankInfoUri, {
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error('랭킹 정보 가져오기 실패');
            }

            const data = await response.json();
            // 랭킹 정보를 화면에 표시
    
            // ranking_main div 요소 선택
            const rankingMainDiv = document.querySelector(".ranking_main");
    
            // data 배열을 순환하면서 각 사용자 정보를 동적으로 생성
            data.forEach((userInfo) => {
                // 사용자 정보를 나타내는 요소를 만들기
                const userDiv = document.createElement("div");
                userDiv.classList.add("user");
    
                // 사용자 이름을 표시하는 요소 만들기
                const memberNameDiv = document.createElement("div");
                memberNameDiv.classList.add("memberName");
                memberNameDiv.textContent = `${userInfo.memberName}`;
    
                // 사용자 점수를 표시하는 요소 만들기
                const memberScoreDiv = document.createElement("div");
                memberScoreDiv.classList.add("memberScore");
                memberScoreDiv.textContent = `점수: ${userInfo.memberScore}`;
    
                // 만든 요소들을 ranking_main div에 추가
                userDiv.appendChild(memberNameDiv);
                userDiv.appendChild(memberScoreDiv);
                rankingMainDiv.appendChild(userDiv);
            });
        } catch (error) {
            console.error('랭킹 정보 가져오기 오류:', error);
        }
    }

    // 서버로부터 문제 목록 동적으로 가져오는 함수
    async function fetchProblemList() {
        const problemListUri = 'api/problemlist';
        try {
            const response = await fetch(serverUrl + problemListUri, {
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error('문제 목록 가져오기 실패');
            }

            const data = await response.json();
      
            // 서버 응답 데이터를 처리하여 문제 목록에 추가
            data.forEach((response, index) => {
                // 서버 응답 데이터를 반복하며 문제 목록 생성 및 화면에 추가하기

                // content_main 요소 선택
                const contentMain = document.querySelector(".question_list");

                // 새로운 문제를 나타내는 요소 만들기
                const questionDiv = document.createElement("div");
                questionDiv.classList.add("question"); // 각 요소를 클래스에 추가하고 텍스트 내용 채우기

                // 문제 id를 요소에 추가
                problemId = response.problemId;
                
                // 문제 제목을 표시하는 요소 만들기
                const problemTitleDiv = document.createElement("div");
                problemTitleDiv.classList.add("problemTitle");
                problemTitleDiv.textContent = `${index + 1}. ${response.problemTitle}`;
        
                // 문제 점수를 표시하는 요소 만들기
                const problemScoreDiv = document.createElement("div");
                problemScoreDiv.classList.add("problemScore");
                problemScoreDiv.textContent = `점수: ${response.problemScore}`;
        
                // 문제 상태 표시하는 버튼 만들기
                // 신입생일때만 표시
                console.log(response);
                      
                // 풀이 보기 버튼 만들기
                const btn_goto_solution = document.createElement("button");
                btn_goto_solution.classList.add("btn_goto_solution");
                btn_goto_solution.textContent = "풀이";
                btn_goto_solution.addEventListener("click", () => {
                    // 클릭 시 페이지 이동
                    localStorage.setItem('problemId', response.problemId); //로컬스토리지에 저장
                    window.location.href = `${serverUrl}solve/${response.problemId}`;
                });

                if (memstate === '신입생'){
                    const btn_goto_question = document.createElement("button");

                    // answerState에 따라 다른 버튼 생성
                    if(response.answerState === '완료'){
                        btn_goto_question.classList.add("btn_clear");
                        btn_goto_question.textContent = `${response.answerState}`;  
                    }else if(response.answerState === '실패'){
                        btn_goto_question.classList.add("btn_fail");
                        btn_goto_question.textContent = `${response.answerState}`;
                    }else if(response.answerState === '대기'){
                        btn_goto_question.classList.add("btn_solution_waiting");
                        btn_goto_question.textContent = `${response.answerState}`; 
                    }else{
                    // null값일때_문제풀러가기
                        btn_goto_question.classList.add("btn_goto_question");
                        btn_goto_question.textContent = `문제풀기`;  
                    }
                    

                    btn_goto_question.addEventListener("click", () => {
                        // ------------------클릭 시 페이지 이동(url수정하기)
                        window.location.href = serverUrl + `problem/${response.problemId}/${memStuNum}`;
                    });
                    // 만든 요소들을 문제 목록에 추가하기
                    questionDiv.appendChild(problemTitleDiv);
                    questionDiv.appendChild(problemScoreDiv);
                    questionDiv.appendChild(btn_goto_question); // 문제풀러가기 버튼 추가
                    questionDiv.appendChild(btn_goto_solution);
                } else {
                    // 만든 요소들을 문제 목록에 추가하기 (신입생이 아닌 경우)
                    questionDiv.appendChild(problemTitleDiv);
                    questionDiv.appendChild(problemScoreDiv);
                    questionDiv.appendChild(btn_goto_solution);
                }
                
                contentMain.appendChild(questionDiv); // 문제 요소를 content_main에 추가
            });
        } catch (error) {
            console.error("데이터를 가져오는 중 오류 발생:", error);
        }
    }

    await fetchUserInfo(storageToken);
    await fetchProblemList();
    await fetchRankInfo();

    document.getElementById('btn_logout').addEventListener('click', function() {
        // 쿠키 제거
        document.cookie = "userToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        // 로컬 스토리지 클리어
        localStorage.clear();
        alert('로그아웃되었습니다.');
        window.location.href = serverUrl + 'signin';
    });

    // 마이페이지 버튼 클릭 시 페이지 이동(url수정하기)
    const btn_mypage = document.getElementsByClassName("btn_mypage")[0]; // 마이페이지 버튼 요소를 가져오기
    btn_mypage.addEventListener("click", () => {
        console.log("클릭");
        console.log(serverUrl + 'mypage');
        window.location.href = serverUrl + 'mypage';
    });
});

