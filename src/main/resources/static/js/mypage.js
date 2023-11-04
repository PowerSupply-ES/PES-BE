document.addEventListener("DOMContentLoaded", function(event) {      
        
    // 서버 URL 및 URI
    //const serverUrl = 'http://3.34.28.73:8080/';
    const serverUrl = 'http://localhost:8080/';

    const uri = 'api/mypage';
        
    // 서버로부터 정보를 가져오는 함수
    function sendGetRequest(url) {

        // localStorage에서 토큰 가져오기
        const storageToken = localStorage.getItem('Authorization');
                
        // fetch API를 사용하여 데이터 가져오기
        fetch(url, {
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
                //response.status = 응답상태코드
            }
            return response.json(); // JSON 형식의 응답 데이터를 파싱
        })
        .then(data => {
            // 데이터를 화면에 표시
            document.querySelector(".memberStuNum").textContent = data.memberStuNum;
            document.querySelector(".memberName").textContent = data.memberName;
            document.querySelector(".memberGen").textContent = data.memberGen;
            document.querySelector(".memberMajor").textContent = data.memberMajor;
            document.querySelector(".memberPhone").textContent =  data.memberPhone;
            document.querySelector(".memberStatus").textContent = data.memberStatus;
            document.querySelector(".memberEmail").textContent = data.memberEmail;
            document.querySelector(".memberScore").textContent = data.memberScore;
            document.querySelector(".memberGitUrl").textContent = data.memberGitUrl;


             // 바로가기 버튼
            const buttonDiv = document.querySelector(".btn_goto_questions");
            const stateForm = document.createElement("form"); // <form>요소 생성

            function createButton(classname, text, url) {
                const button = document.createElement("button");
                button.classList.add(classname);
                button.textContent = text;
                button.addEventListener("click", () => {
                    // 클릭 시 페이지 이동
                    window.location.href = serverUrl + url;
                });

                buttonDiv.appendChild(button);
            }

            
            if (data.memberStatus === '재학생') {
                createButton("btn_mycomment", "내가 쓴 댓글 보기", "mycomment");
                createButton("btn_question_bank", "문제 은행", "problembank");
                createButton("btn_manage_student", "학생 관리", "management");
            } else if (data.memberStatus === '신입생') { // -----------------url 수정하기 !!!!
                createButton("btn_goto1", "내가 푼 문제 보기", "url4");
                createButton("btn_goto2", "내가 답한 문제 보기", "url5");
            }
        })
        .catch(error => {
            console.error("데이터 가져오기 실패:", error);
        });
    }
    
    // 페이지가 로드될 때 데이터 가져오기 함수 호출
    sendGetRequest(serverUrl + uri);
});