import serverConfig from './config.js';

document.addEventListener("DOMContentLoaded", function(event) {
    // localStorage에서 토큰 가져오기
    const storageToken = localStorage.getItem('Authorization');
    
    const serverUrl = serverConfig.serverUrl; // serverUrl을 정의

    // 상단 사용자 정보 함수
    function fetchUserInfo(storageToken) {
        const userInfoUri = 'api/myuser';
        fetch(serverUrl + userInfoUri, {
            //credentials: "include",
            method: 'GET',
            headers: {
                'Authorization': storageToken
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('사용자 정보 가져오기 실패');
            }
            return response.json();
        })
        .then(data => {
            // 사용자 정보를 화면에 표시
            document.querySelector(".memberName").textContent = "이름: " + data.memberName;
            document.querySelector(".memberScore").textContent = "획득한 점수: " + data.memberScore;
            document.querySelector(".memberStatus").textContent = "상태(신입생/재학생/관리자): " + data.memberStatus;
            
            // memberStatus 값을 memstate에 저장
            memstate = data.memberStatus;
        })
        .catch(error => {
            console.error('사용자 정보 가져오기 오류:', error);
        });
    }

    // 서버로부터 문제 목록 동적으로 가져오는 함수
    function fetchProblemList() {
        const problemListUri = 'api/problemlist';
            fetch(serverUrl + problemListUri, {
                method: 'GET'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('문제 목록 가져오기 실패');
                }
                return response.json();
            })
          
            .then(data => {
                // 서버 응답 데이터를 처리하여 문제 목록에 추가
                data.forEach((response) => {
                    // console.log(response)

                    // content_main 요소 선택
                    const contentMain = document.querySelector(".bank_list");
            
                    // 새로운 문제를 나타내는 요소 만들기
                    const bankDiv = document.createElement("div");
                    bankDiv.classList.add("bankDiv"); // 각 요소를 클래스에 추가하고 텍스트 내용 채우기
    
                    // 문제 id를 요소에 추가
                    // 문제 제목을 표시하는 요소 만들기
                    // <--------------- 문제 상태 표시하는 버튼 만들기
                    // <--------------- 신입생일때만 표시
                    const problem_num = document.createElement("div");
                    problem_num.classList.add("problem_num");
                    problem_num.textContent = `문제${response.problemId}`;

                    // 문제 제목 표시하는 요소 만들기
                    const question = document.createElement("div");
                    question.classList.add("question");
                    question.textContent = `${response.problemTitle}`;
            
                    // 풀이 보기 버튼 만들기
                    const btn_goto_solution = document.createElement("button");
                    btn_goto_solution.type = "submit";
                    btn_goto_solution.classList.add("btn_goto_solution");
                    btn_goto_solution.textContent = "풀이 보기";
                    btn_goto_solution.addEventListener("click", () => {
                        // -------- 클릭 시 페이지 이동(url수정하기)
                        localStorage.setItem('problemId', response.problemId); //로컬스토리지에 저장

                        console.log(`${serverUrl}problembank/${response.problemId}`);
                        window.location.href = `${serverUrl}problembank/${response.problemId}`;
                    });

                    // 만든 요소들을 문제 목록에 추가하기 (신입생이 아닌 경우)
                    bankDiv.appendChild(problem_num);
                    bankDiv.appendChild(question);
                    bankDiv.appendChild(btn_goto_solution);
                    contentMain.appendChild(bankDiv); // 문제 요소를 content_main에 추가

                });
            })
    
            .catch(error => {
            console.error("데이터를 가져오는 중 오류 발생:", error);
            });
    }
    
    fetchUserInfo(storageToken)
    fetchProblemList()

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
})