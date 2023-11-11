import serverConfig from './config.js';

document.getElementById('signup-form').addEventListener('submit', function(event) {
  event.preventDefault(); // 기본 폼 제출 동작을 막음
   
    // 입력필드에서 값 가져오기
    const memberName = document.getElementById('username').value;
    const memberStuNum = document.getElementById('student_id').value;
    const memberMajor = document.getElementById('major').value;
    const memberPhone = document.getElementById('phone_number').value;
    const memberEmail = document.getElementById('email').value;
    const memberGitUrl = document.getElementById('git_address').value;
    const memberPw = document.getElementById('password').value;
    const memberGen = document.getElementById('generation').value;
    

    // POST 요청에 보낼 데이터를 JavaScript 객체로 준비
    const formData = {
      //객체속성이름 : 객체속성값
      memberName: memberName,
      memberStuNum: memberStuNum,
      memberMajor: memberMajor,
      memberPhone: memberPhone,
      memberEmail: memberEmail,
      memberGitUrl : memberGitUrl,
      memberPw: memberPw,
      memberGen : memberGen
    };

    // 서버 URL 및 URI
    const serverUrl = serverConfig.serverUrl; // serverUrl을 정의
    const uri = 'api/signup'; 

    // POST 요청을 보내는 함수를 정의
    // fetch 함수를 사용하여 HTTP POST 요청을 생성
    // url뒷부분은 요청에 대한 옵션을 설정하는 객체
    function sendPostRequest(url, data) {
      fetch(url, {
        method: 'POST',
        // 요청이 JSON 형태의 데이터를 포함하고 있음을 서버에게 알리는 역할
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data) // 데이터를 JSON 문자열로 변환하여 보냄
      })

      // fetch 함수가 반환하는 Promise 객체를 사용하여 서버 응답을 처리
      // 비동기요청 응답처리
        .then((response) => {

          // 응답 성공적이지 않을 경우
          if (!response.ok) {
            console.log(response);
            window.alert('네트워크 응답이 실패했습니다.');
            throw new Error('네트워크 응답이 실패했습니다.'); //에러 강제적으로 발생시키고 catch에서 처리되도록함
          }
          return response.json(); // JSON 응답 데이터를 파싱하여 .then 블록으로 전달
        })

        .then((responseData) => {
          // 성공적으로 응답을 받았을 때 실행
          // 응답 데이터처리
          const resultMessage = '회원가입 성공: ' + responseData.message; // 서버 응답에서 메시지 추출
          displayResult(resultMessage);

          // 페이지 이동
          window.location.href = serverUrl + 'signin'; 
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
      alert(message); // 결과 팝업 창으로 표시
    }

    // POST 요청
    // serverUrl과 uri를 조합하여 완전한 요청 URL을 생성하고, formData를 POST 요청으로 보냄
    sendPostRequest(serverUrl + uri, formData); 
  });
