#!/usr/bin/env bash

# 레포지토리 경로 설정
REPOSITORY=/home/ubuntu/PES-BE
cd $REPOSITORY

# gradle-wrapper.jar가 존재하는지 확인 후 백업
WRAPPER_PATH="$REPOSITORY/gradle/wrapper/gradle-wrapper.jar"
if [ -f "$WRAPPER_PATH" ]; then
    echo "gradle-wrapper.jar 파일이 이미 존재합니다. 파일을 백업하고 새로운 파일로 교체합니다."
    # 파일 백업
    mv "$WRAPPER_PATH" "$WRAPPER_PATH.bak"
fi

# 빌드 권한 설정
chmod +x gradlew

# gradlew를 사용한 클린 빌드
./gradlew clean build

# JAR 파일 경로 설정
JAR_PATH=$REPOSITORY/build/libs/PES-0.0.1-SNAPSHOT.jar

# 현재 실행중인 Java 애플리케이션 PID 확인
CURRENT_PID=$(pgrep -fl $JAR_PATH | awk '{print $1}')

# 실행 중인 애플리케이션이 있을 경우 종료
if [ -z "$CURRENT_PID" ]
then
    echo "> 종료할 애플리케이션이 없습니다."
else
    echo "> 종료할 애플리케이션 PID: $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 10
    if pgrep -f $JAR_PATH > /dev/null; then
        echo "> 애플리케이션 종료 실패, 강제 종료합니다."
        kill -9 $CURRENT_PID
    fi
fi

# screen 세션 생성 및 Java 애플리케이션 배포
SESSION_NAME="PES_BE_DEPLOY"
screen -dmS $SESSION_NAME
screen -S $SESSION_NAME -X stuff "java -jar $JAR_PATH\n"
echo "> Deployed in screen session $SESSION_NAME"
