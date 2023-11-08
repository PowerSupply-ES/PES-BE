document.addEventListener("DOMContentLoaded", function(event) {
    const storageToken = localStorage.getItem('Authorization');
    const serverUrl = 'http://pes23.com/';

    const problemId = localStorage.getItem('problemId'); // 로컬스토리지에서 problemId불러오기
    console.log("problemId: ", problemId)

    // 상단 사용자 정보 함수
    function fetchUserInfo(storageToken) {
        const userInfoUri = 'api/myuser';
        fetch(serverUrl + userInfoUri, {
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


    // 문제설명
    // 서버로부터 정보를 가져오는 함수
    function Getproblem(url) {
        const uri = `api/problem/${problemId}`;
        fetch(url + uri, {
            method: "GET",
            headers: {
                'Authorization': storageToken
            }
        })
        .then(response => {
            if (!response.ok) {
                console.log("서버응답 : ", response); // 토큰을 콘솔에 출력
                throw new Error("데이터 가져오기 실패: " + response.status + " " + response.statusText);
            }
            return response.json(); // JSON 형식의 응답 데이터를 파싱
        })
        .then(data => {
            //문제id
            document.querySelector(".problem_num").textContent = "문제" + data.problemId;
            //문제제목
            document.querySelector(".problem_title").textContent = data.problemTitle;
            //문제내용
            document.querySelector(".problem_detail").textContent = data.problemContent;

            //-------------------문제점수도 넣기???

        })
        .catch(error => {
            console.error("데이터 가져오기 실패:", error);
        });
    }


    // 문제에 대한 질문들
    function Getquestion(url) {
        const uri = `api/questions/${problemId}`;
        fetch(url + uri, {
            credentials: "include",
            method: "GET",
            headers: {
                'Authorization': storageToken
            }
        })
        .then(response => {
            if (!response.ok) {
                console.log("서버응답 : ", response); // 토큰을 콘솔에 출력
                throw new Error("데이터 가져오기 실패: " + response.status + " " + response.statusText);
            }
            return response.json(); // JSON 형식의 응답 데이터를 파싱
        })
        .then(data => {
            // --------------------난이도별 문제표시
            // 서버 응답 데이터를 처리하여 문제 목록에 추가
            data.forEach((response) => {
                console.log("난이도= " + response.questionDifficulty)

                // content_main 요소 선택
                if (response.questionDifficulty === 0) {
                    contentMain = document.querySelector('.bank_detail_list_easy');
                } else if (response.questionDifficulty === 1) {
                    contentMain = document.querySelector('.bank_detail_list_hard');
                }
                const bankDetailDiv = document.createElement("div");

                if(contentMain){  //hard일때
                    bankDetailDiv.classList.add("bankDetailDiv_hard");
                } else{
                    bankDetailDiv.classList.add("bankDetailDiv_easy");
                }

                // 질문 id를 요소에 추가
                // 질문 제목을 표시하는 요소 만들기
                const problem_num = document.createElement("div");
                problem_num.classList.add("problem_num");
                problem_num.textContent = `질문${response.questionId}`;

                // 질문 표시하는 요소 만들기
                const question = document.createElement("div");
                question.classList.add("question");
                question.textContent = `${response.questionContent}`;
    
                // 만든 요소들을 문제 목록에 추가하기 (신입생이 아닌 경우)
                bankDetailDiv.appendChild(problem_num);
                bankDetailDiv.appendChild(question);
                contentMain.appendChild(bankDetailDiv); // 문제 요소를 content_main에 추가
                
            });
            
        })
        .catch(error => {
            console.error("데이터 가져오기 실패:", error);
        });
    }

    fetchUserInfo(storageToken)
    Getproblem(serverUrl)
    Getquestion(serverUrl)


});