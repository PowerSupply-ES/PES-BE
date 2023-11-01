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
    //const serverUrl = 'http://3.34.28.73:8080/';
    const serverUrl = 'http://localhost:8080/';

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
            if (!response.ok) { // HTTP 응답의 상태 코드가 성공적인(200-299 범위) 경우 true를 반환
                window.alert('네트워크 응답이 실패했습니다.');
                throw new Error('네트워크 응답이 실패했습니다.'); // 에러 강제적으로 발생시키고 catch에서 처리되도록 함
            }
            return response.json(); // JSON 응답 데이터를 파싱하여 .then 블록으로 전달
        })

        .then((responseData) => {
            // 성공적으로 응답을 받았을 때 실행
            if (responseData.message) {
                const resultMessage = '로그인 성공: ' + responseData.message;
                displayResult(resultMessage);
            } else {
                // 메시지 속성이 없는 경우 예외 처리
                displayResult('로그인 성공: 메시지 없음');
            }
            // 서버에서 설정한 쿠키 값 가져오기
            const cookieValue = document.cookie.replace(/(?:(?:^|.*;\s*)Authorization\s*=\s*([^;]*).*$)|^.*$/, "$1");

            // 만약 쿠키 값이 존재한다면 로컬 스토리지에 저장
            if (cookieValue) {
            localStorage.setItem('Authorization', cookieValue);
            window.alert("쿠키를 로컬 스토리지에 저장했습니다!");
            }

            // 페이지 이동
            window.location.href = serverUrl + 'main';
        })
        // 요청 또는 응답처리 중에 오류가 발생한 경우
        .catch((error) => {
            // 오류 발생 시 실행되는 코드
            const errorMessage = '오류 발생: ' + error.message;
            displayResult(errorMessage);
        });
    }

    // 결과 표시 함수
    function displayResult(message) {
        alert(message);
    }

    // // 쿠키에 JWT 토큰 저장
    // function getJwtTokenFromCookie() {
    //     const cookies = document.cookie.split(';');
    //     for (const cookie of cookies) {
    //         const [name, value] = cookie.trim().split('=');
    //         if (name === 'Authorization') {
    //             // 'Authorization' 쿠키에서 토큰 값을 반환
    //             return decodeURIComponent(value);
    //         }
    //     }
    //     return null; // JWT 토큰을 찾지 못한 경우
    // }

    // POST 요청
    sendPostRequest(serverUrl + uri, formData);
});







