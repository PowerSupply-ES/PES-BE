document.addEventListener("DOMContentLoaded", function(event) {
    // localStorage에서 토큰 가져오기
    const storageToken = localStorage.getItem('Authorization');
    const serverUrl = 'http://3.34.28.73:8080/';



    // 사용자 정보 가져오기
    fetchUserInfo(storageToken);
    
    // 현재 페이지 URL에서 query string을 가져옵니다.
    const queryString = window.location.search;

    // query string을 객체로 파싱합니다.
    const params = new URLSearchParams(queryString);

    // "std" 변수의 값을 추출하고, 문자열로 변환하여 "memberStuNum" 변수에 저장합니다.
    const memberStuNum = params.get("std");

    // 결과를 확인합니다.
    console.log("memberStuNum:", memberStuNum);

    // 문제 목록 가져오기
    if (memberStuNum) {
        fetchProblemList(storageToken, memberStuNum);
    }
    //fetchData();

    
    // 서버로부터 사용자 정보를 가져오는 함수
    function fetchUserInfo(storageToken) {
        const userInfoUri = 'api/myuser';

        fetch(serverUrl + userInfoUri, {
            credentials: "include",
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
        })
        .catch(error => {
            console.error('사용자 정보 가져오기 오류:', error);
        });
    }

    // 서버로부터 문제 목록 동적으로 가져오는 함수
    function fetchProblemList(storageToken, memberStuNum) {
    const problemListUri = `api/problemlist?std=${memberStuNum}`;


        fetch(serverUrl + problemListUri, {
            credentials: "include",
            method: 'GET',
            headers: {
                'Cookie': storageToken
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('문제 목록 가져오기 실패');
            }
            return response.json();
        })

        .then(data => {
        // 서버 응답 데이터를 처리하여 문제 목록에 추가
        // data.forEach(...) 사용하여 서버 응답 데이터를 반복하면서 문제 목록을 동적으로 생성
        data.forEach((response, index) => {
            // 서버 응답 데이터를 반복하며 문제 목록 생성 및 화면에 추가하기

            // 새로운 문제를 나타내는 요소를 만들기
            const questionDiv = document.createElement("div");
            questionDiv.classList.add("question"); // 각 요소를 클래스에 추가하고 텍스트내용채워넣음
    
            // 문제 제목을 표시하는 요소 만들기
            const problemTitleDiv = document.createElement("div"); // 새로운 div요소생성
            problemTitleDiv.classList.add("problemTitle"); // 생성한 요소에 class 추가-> css요소적용위해
            problemTitleDiv.textContent = `${index + 1}. ${response.problemTitle}`; // 방금 생성한 <div> 요소의 텍스트 내용을 설정
    
            // 문제 점수를 표시하는 요소 만들기
            const problemScoreDiv = document.createElement("div");
            problemScoreDiv.classList.add("problemScore");
            problemScoreDiv.textContent = `점수: ${response.problemScore}`;
    
            // 문제 상태를 표시하는 요소 만들기
            const problemStateDiv = document.createElement("div");
            problemStateDiv.classList.add("problemState");
            problemStateDiv.textContent = `상태: ${response.problemState}`;
    
            // 풀이 보기 버튼을 담은 폼 요소 만들기
            const goToSolutionForm = document.createElement("form");
            goToSolutionForm.action = "new_solution.html";
            const goToSolutionButton = document.createElement("button");
            goToSolutionButton.type = "submit";
            goToSolutionButton.classList.add("btn_goto_solution"); //스타일클래스 추가
            goToSolutionButton.textContent = "풀이 보기";
            goToSolutionForm.appendChild(goToSolutionButton); //버튼을 폼에 추가
    
            // 만든 요소들을 문제 목록에 추가하기
            const questionListDiv = document.querySelector(".question_list"); // 문제 목록 요소 가져오기
            questionDiv.appendChild(problemTitleDiv); // 문제 제목 요소를 문제 요소에 추가
            questionDiv.appendChild(problemScoreDiv); // 문제 점수 요소를 문제 요소에 추가
            questionDiv.appendChild(problemStateDiv); // 문제 상태 요소를 문제 요소에 추가
            questionDiv.appendChild(goToSolutionForm); // 풀이 보기 폼 요소를 문제 요소에 추가
            questionListDiv.appendChild(questionDiv); // 문제 요소를 문제 목록에 추가 
            });
        })
        .catch(error => {
        console.error("데이터를 가져오는 중 오류 발생:", error);
        });
    }
    
    // function fetchProblemList() {
    //     .then(data => {
    //         // 문제 목록을 화면에 표시
    //         const problemListContainer = document.querySelector('.problem-list-container');
    //         problemListContainer.innerHTML = ''; // 기존 내용 지우기

    //         for (const problem of data) {
    //         // 서버에서 가져온 data 배열을 순환하면서 각 문제에 대한 정보를 가져옴
    //             const problemItem = document.createElement('div');
    //             problemItem.classList.add('problem-item');
    //             problemItem.textContent = problem.title;
    //             problemListContainer.appendChild(problemItem);
    //         }
    //     })  
    // }
     
    // 쿠키에서 JWT 토큰 가져오기
    // function getCookie(name) {
    //     // ...
    // }

    // 쿠키에서 JWT 토큰 가져오기
    // function getCookie(name) {
    //     const cookieString = document.cookie;
    //     const cookies = cookieString.split('; '); //세미콜론과 공백으로 분할

    //     for (const cookie of cookies) { // 배열에 있는 각 쿠키에 대해 반복
    //         // 쿠키의 이름과 값으로 분리하고 그 결과를 name과 value 변수에 할당
    //         const [cookieName, cookieValue] = cookie.split('=');
    //         if (cookieName === name) {
    //             return cookieValue;
    //         }
    //     }
    //     return null; // JWT 토큰이 없을 경우 null 반환
    // }

    // function getCookie(name) {
    //     const cookieString = document.cookie;
    //     const cookies = cookieString.split('; ');
      
    //     for (const cookie of cookies) {
    //       const [cookieName, cookieValue] = cookie.split('=');
    //       if (decodeURIComponent(cookieName) === name) {
    //         return decodeURIComponent(cookieValue);
    //       }
    //     }
    //     return null;
    //  }
});