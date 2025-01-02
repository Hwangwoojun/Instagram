# 베이스 이미지 설정
FROM node:lts AS build

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 설치
COPY ../package*.json ./
RUN npm install

# 애플리케이션 파일 추가
COPY ../ ./

# 애플리케이션 빌드
RUN npm run build

# 최종 이미지 설정
FROM nginx:alpine

# 빌드된 Vue.js 파일 복사
COPY --from=build /app/dist /usr/share/nginx/html

# 7.Nginx 설정 파일 복사
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
        
# 포트 80 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
