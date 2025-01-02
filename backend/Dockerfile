# FROM은 Dockerfile에서 사용하는 기본 이미지를 설정하는 명령어 Docker 이미지 생성의 시작점이라고 보면됨 
# Docker가 이미지를 만들 때 어떤 환경을 기반으로 시작할지 결정하는 것이 FROM임
# AS는 이미지 빌드의 각 단계에서 필요한 파일을 손쉽게 가져올 수 있음 
# 이름을 붙이면 Dockerfile의 단계에서 --from=build 처럼 참조할 수 있음 여러 단계를 효율적으로 관리가능
FROM maven:3.8.2-openjdk-17-slim AS build

# WORKDIR 명령어는 Docker 컨테이너 내에서 현재 작업 디렉토리를 지정하는 명령어
# 그리고 컨테이너 안에서 사용할 디렉토리 경로를 /build로 설정
# 이렇게 하면 이후 실행되는 명령어들(COPY, RUN)은 /build 폴더 안에서 실행됨
WORKDIR /build

# COPY 명령어는 로컬 파일을 Docker 컨테이너에 복사하는 명령어로 보면됨
# 현재 디렉토리에 있는 pom.xml 파일을 컨테이너의 현재 작업 디렉토리 WORKDIR에 복사됨r
# src 폴더 전체를 컨테이너의 /build/src 경로로 복사함
COPY ./pom.xml .
COPY ./src /build/src

# RUN 명령어는 Dockerfile 내에서 컨테이너가 빌드되는 동안 특정 명령을 실행할 수 있게 해줌
# mvn clean은 maven 프로젝트의 target을 지우고 그 안에 있던 이전 빌드 파일들을 삭제함
# verify는 프로젝트가 테스트를 포함해 필요한 기준을 충족하는지 검증하는 단계임
# 이 과정에서 발생하는 오류가 없으면 프로젝트가 정해진 품질 기준을 충족한다고 보면됨
RUN mvn clean verify

# install은 maven의 clean 다음 단계로 프로젝트를 빌드하고 로컬 maven 저장소에 결과를 설치함 .jar
# -DskipTests는 maven에서 제공하는 옵션 중 하나로 테스트를 건너뛰도록 설정가능함
# -DskipTests를 사용하면 빌드 과정에서 컴파일이나 패키징은 그대로 수행하지만 테스트 단계는 실행되지 않음
# -DskipTests에 목적은 테스트를 건너뜀으로써 빌드 속도를 높일 수 있음 Docker 이미지 빌드 중 자주 사용됨
RUN mvn install -DskipTests

# Dockerfile에서는 FROM 명령어를 여러 번 사용하는 것이 가능
# 멀티스테이지 빌드 라는 기능을 활용하는 것으로 Dockerfile에서 특정 작업을 수행하기 위해 여러 단계로 빌드 환경을 나눌 수 있음
# 멀티스테이지를 사용하는 이유는 FROM 이후에 또 다른 FROM을 사용하면 앞에 스테이지에서 사용했던 환경과 파일들이
# 새로운 스테이지에 자동으로 복사되거나 연결되지 않기 때문에 각 FROM은 서로 독립된 새로운 환경을 생성함
# 때문에 두 번째 FROM에서 새로 설정하여 최종 실행 단계에서는 빌드 도구 없이 가볍고 깨끗한 실행 환경만을 제공함
FROM openjdk:17.0-jdk

# 이 명령어는 Docker 컨테이너의 시간대를 설정하는 데 사용됨 
# 컨테이너가 한국 시간을 기준으로 동작됨
# ln은 파일을 연결하는 명령어
# -s 은 심볼릭 링크를 만들기 위해 사용됨 심볼릭 링크는 특정 파일이나 디렉토리를 가리키는 별칭 같은 느낌임
# -f 은 기존 파일이 있을 경우 강제로 덮어쓰기 위해 사용됨
# /usr/share/zoneinfo/Asia/Seoul은 서울 시간을 의미하는 파일 경로
# 서울 시간 파일을 /etc/localtime과 연결하여 컨테이너의 표준 시간대가 서울 시간으로 설정되게 함
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# 처음에 했던 작업과 똑같이 컨테이너 내부에서 애플리케이션이 실행될 기본 작업 디렉토리를 설정
# 똑같이 경로를 /app으로 지정해주면 그 이후에 작성된 모든 명령(COPY, RUN, CMD)은 /app 디렉토리 내에서 실행됨
WORKDIR /app

# EXPOSE 명령어는 Dockerfile에서 컨테이너가 사용할 포트를 선언하는 명령어임
# 하지만 EXPOSE 명령어는 포트를 외부에 공개하거나 연결하는 것이 아닌 그냥 컨테이너가 사용하는 포트를 알려주는 것 뿐임
# 그래서 docker run -p 명령어로 포트를 매핑해 주어야 실제 외부에서 접속이 가능함
EXPOSE 8080

# 멀티스테이지 빌드를 사용하여 빌드된 JAR 파일을 최종 이미지에 복사하는 작업을 수행
# 먼저 --from=build는 앞에서 정의한 빌드 단계의 이름을 사용하여 JAR 파일을 최종 이미지로 가져오는 작업을 함
# /build/target/*.jar는 빌드 과정에서 생성된 모든 JAR 파일을 지정함
# app.jar는 최종 Docker 이미지에서 파일을 저장할 위치를 지정함 즉 컨테이너 내에서 jar 파일을 app.jar라는 이름으로 저장한다는거임
# app.jar이라는 이름은 나중에 CMD 명령어에서 애플리케이션을 실행하게 됨
COPY --from=build /build/target/*.jar app.jar

# Doker 컨테이너 내부에서 직접 지정해주는게 아니라 환경변수 설정을 통해서 자동으로 한국시간으로 들어가지게 끔
ENV TZ=Asia/Seoul

# CMD는 Dockerfile에서 컨테이너가 시작될 때 실행할 기본 명령을 지정하는 명령어
# CMD로 지정된 명령은 컨테이너가 실행될 때 자동으로 실행되고 명령이 종료되면 컨테이너도 같이 종료됨
# java 명령어를 사용하여 컨테이너 내에서 Java로 만든 프로그램을 실행함
# -Xmx2048m: JVM이 사용할 수 있는 최대 힙 메모리 크기를 설정
# -Xms1024m: JVM의 초기 힙 메모리 크기를 설정
# -jar 옵션은 JAR 파일을 실행할 때 사용하는 Java 옵션
# app.jar 파일에는 Java 애플리케이션의 실행 코드와 필요한 라이브러리들이 모두 포함되어있음 그리고 이를 실행시킴
CMD ["java", "-Xmx2048m", "-Xms1024m", "-jar", "app.jar"]
