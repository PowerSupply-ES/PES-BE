document.addEventListener("DOMContentLoaded", function(event) {      
        
    // 서버 URL 및 URI
    const serverUrl = 'http://3.34.28.73:8080/';


    const uri = 'api/mypage';
        
    // 서버로부터 정보를 가져오는 함수
    function sendGetRequest(url) {
        
        // // 서버에서 설정한 쿠키 값 가져오기
        // const cookieValue = document.cookie.replace(/(?:(?:^|.*;\s*)Authorization\s*=\s*([^;]*).*$)|^.*$/, "$1");

        // // 만약 쿠키 값이 존재한다면 로컬 스토리지에 저장
        // if (cookieValue) {
        // localStorage.setItem('Authorization', cookieValue);
        // }

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
            document.querySelector(".memberStuNum").textContent = "학번: " + data.memberStuNum;
            document.querySelector(".memberName").textContent = "이름: " + data.memberName;
            document.querySelector(".memberGen").textContent = "성별: " + data.memberGen;
            document.querySelector(".memberMajor").textContent = "전공: " + data.memberMajor;
            document.querySelector(".memberPhone").textContent = "전화번호: " + data.memberPhone;
            document.querySelector(".memberStatus").textContent = "상태: " + data.memberStatus;
            document.querySelector(".memberEmail").textContent = "이메일: " + data.memberEmail;
            document.querySelector(".memberGitUrl").textContent = "GitHub URL: " + data.memberGitUrl;
        })
        .catch(error => {
            console.error("데이터 가져오기 실패:", error);
        });
    }

    // 페이지가 로드될 때 데이터 가져오기 함수 호출
    sendGetRequest(serverUrl + uri);

    //쿠키에서 JWT 토큰 가져오기
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
});