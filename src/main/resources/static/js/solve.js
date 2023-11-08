document.addEventListener("DOMContentLoaded", function(event) {
    const storageToken = localStorage.getItem('Authorization');
    const serverUrl = 'http://localhost:8080/';
    const problemId = localStorage.getItem('problemId'); // 로컬스토리지에서 problemId불러오기
    console.log("problemId = " + problemId)

    let stu_num;

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

    // 문제 가져오기 함수
    function getProblemlist(url){
        const uri = `api/problem/${problemId}/simple`;
        fetch(url + uri,{
            methode : "GET"
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
            //-------------------문제점수도 넣기???
        })
        .catch(error => {
            console.error("데이터 가져오기 실패:", error);
        });
    }

    // 풀이 가져오기 함수
    function getSolvelist(url){
        const uri=`api/solvelist/${problemId}`
        fetch(url + uri,{
            methode : "GET",
            headers : {
                'Authorization' : storageToken
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
           // 서버 응답 데이터를 처리하여 문제 목록에 추가
           data.forEach((response) => {
            console.log(response.commentContent);

            // content_main 요소 선택
            const commentDiv = document.querySelector(".solving_list");
    
            // 새로운 stu_list 나타내는 요소 만들기
            const stu_list = document.createElement("div");
            stu_list.classList.add("stu_list");


            //----------------------------pass/fail(1,0)으로 받아서 표시(수정)

            const stu = document.createElement("div");
            stu.classList.add("stu");
            // -----------신입생 학번(저장하기!)
            stu_num = response.memberStuNum;

            // 신입생 이름 표시하는 요소 만들기
            const stu_name = document.createElement("div");
            stu_name.classList.add("stu_name");
            stu_name.textContent = `${response.memberName}`;
    
            // 코멘트 표시하는 요소 만들기
            const process_num = document.createElement("div");
            process_num.classList.add("process_num");
            process_num.textContent = `${response.commentCount}/3`;
    
            //문제상태 표시 요소 만들기
            const process_clear = document.createElement("div");
            process_clear.classList.add("process_clear");
            process_clear.textContent = `${response.answerState}`;

                    
            // 보러가기 버튼 만들기
            // ---------- memberStuNum 버튼에 링크걸기
            const btn_goto_solving = document.createElement("button");
            btn_goto_solving.classList.add("btn_goto_solving");
            btn_goto_solving.textContent = "보러가기";
            btn_goto_solving.addEventListener("click", () => {
                //-----------클릭 시 페이지 이동(url수정하기)
                window.location.href = `${serverUrl}problem/${response.memberStuNum}/${response.problemId}`;
            });

            // 만든 요소들을 리스트에 추가하기
            stu_list.appendChild(stu_name);
            stu.appendChild(process_num);
            stu.appendChild(process_clear);
            stu.appendChild(btn_goto_solving);
            stu_list.appendChild(stu);
            commentDiv.appendChild(stu_list);
        });




        })

        .catch(error => {
            console.error("데이터 가져오기 실패:", error);
        });
    }
    fetchUserInfo(storageToken);
    getProblemlist(serverUrl);
    getSolvelist(serverUrl);

});