document.addEventListener("DOMContentLoaded", function(event) {
    // localStorage에서 토큰 가져오기
    const storageToken = localStorage.getItem('Authorization');
    const serverUrl = 'http://localhost:8080/';

    // 내가쓴댓글 동적생성 함수
    function fetchComments() {
        const problemListUri = 'api/comment/mycomment';
        fetch(serverUrl + problemListUri, {
            method: 'GET',
            headers: {
                'Authorization': storageToken
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('목록 가져오기 실패');
            }
            return response.json();
        })
        
        .then(data => {
            // 서버 응답 데이터를 처리하여 문제 목록에 추가
            data.forEach((response) => {
                // 서버 응답 데이터를 반복하며 문제 목록 생성 및 화면에 추가하기
        
                // content_main 요소 선택
                const commentDiv = document.querySelector(".comment_list");
        
                // 새로운 comment 나타내는 요소 만들기
                const questionDiv = document.createElement("div");
                questionDiv.classList.add("commentDiv");
                const comment_left = document.createElement("div");
                comment_left.classList.add("comment_left");

                // 문제 id를 요소에 추가
                questionDiv.dataset.problemId = response.id;
                
                // 문제 제목을표시하는 요소 만들기
                const problem_num = document.createElement("div");
                problem_num.classList.add("problem_num");
                problem_num.textContent = `문제${response.problemId}`;
        
                // 풀이 학생 표시하는 요소 만들기
                const stu_name = document.createElement("div");
                stu_name.classList.add("stu_name");
                stu_name.textContent = `${response.memberName}`;
        

                // 내가 쓴 댓글표시 요소 만들기
                const comment = document.createElement("div");
                comment.classList.add("comment");
                comment.textContent = `${response.commentContent}`;

                        
                // 보러가기 버튼 만들기
                const btn_goto_comment = document.createElement("button");
                btn_goto_comment.type = "submit";
                btn_goto_comment.classList.add("btn_goto_comment");
                btn_goto_comment.textContent = "보러가기";
                btn_goto_comment.addEventListener("click", () => {
                    // 클릭 시 페이지 이동(url수정하기)
                    window.location.href = serverUrl + '';
                });

                // 만든 요소들을 리스트에 추가하기
                comment_left.appendChild(problem_num);
                comment_left.appendChild(stu_name);
                comment_left.appendChild(comment);
                commentDiv.appendChild(comment_left); // 요소들을 commentDiv에 추가
                commentDiv.appendChild(btn_goto_comment);
            });
        })
        .catch(error => {
        console.error("데이터를 가져오는 중 오류 발생:", error);
        });
    }
    fetchComments();


});