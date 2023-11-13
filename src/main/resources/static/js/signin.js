import serverConfig from './config.js';

document.getElementById('signin-form').addEventListener('submit', function(event) {
    event.preventDefault();

    // 입력필드에서 값 가져오기
    const memberStuNum = document.getElementById('student_id').value;
    const memberPw = document.getElementById('password').value;

    // POST 요청에 보낼 데이터를 JavaScript 객체로 준비
    const formData = {
        memberStuNum: memberStuNum,
        memberPw: memberPw
    };

    // 서버 URL 및 URI
    const serverUrl = serverConfig.serverUrl; // serverUrl을 정의

    const uri = 'api/signin';

    function sendPostRequest(url, data) {
        fetch(url, {
            credentials: "include",
            method: 'POST',
            headers: {
                'Content-Type': 'application/json', // 데이터 타입을 JSON으로 설정
            },
            body: JSON.stringify(data)
        })

        // 서버 응답을 처리
        // 네트워크 오류가 발생한 경우
        .then((response) => {
            console.log(response.status);
            if (!response.ok) { // HTTP 응답의 상태 코드가 성공적인(200-299 범위) 경우 true를 반환
                if (response.status === 404) {
                    alert("일치하지 않습니다");
                }
                throw new Error('네트워크 응답이 실패했습니다.'); // 에러 강제적으로 발생시키고 catch에서 처리되도록 함
            }
            return response.json(); // JSON 응답 데이터를 파싱하여 .then 블록으로 전달
        })

        .then((responseData) => {
            // 성공적으로 응답을 받았을 때 실행
            if (responseData.message) {
                localStorage.setItem('stuNum', memberStuNum);
                const resultMessage = responseData.message;
                displayResult(resultMessage);
            } else {
                // 메시지 속성이 없는 경우 예외 처리
                displayResult('로그인 성공: 메시지 없음');
            }
           
            // 서버에서 설정한 토큰 값 가져오기
            const actualToken = document.cookie.replace(/(?:(?:^|.*;\s*)Authorization\s*=\s*([^;]*).*$)|^.*$/, "$1");
            
            // 토큰정보 localStorage에 저장
            localStorage.setItem("storageToken", actualToken);
            const storageToken = actualToken;

            // 페이지 이동
            window.location.href = serverUrl + 'main';
        })
        // 요청 또는 응답처리 중에 오류가 발생한 경우
        .catch((error) => {
            // 오류 발생 시 실행되는 코드
            // const errorMessage = '오류 발생: ' + error.message;
            const errorMessage = '로그인 실패';

            displayResult(errorMessage);
        });
    }

    // 결과 표시 함수
    function displayResult(message) {
        alert(message);
    }


    // POST 요청
    sendPostRequest(serverUrl + uri, formData);
});







