package vue.example.backend.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUpload {
    public static void main(String[] args) {
        try {
            // 경로를 인코딩 처리
            String uploadDir = URLEncoder.encode("/usr/share/nginx/html/upload",

                    StandardCharsets.UTF_8.toString());
            uploadDir = java.net.URLDecoder.decode(uploadDir, StandardCharsets.UTF_8.toString());

            File uploadDirectory = new File(uploadDir);

            // 업로드 디렉토리가 존재하지 않으면 생성
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs(); // 디렉토리 생성
            }

            // 파일 작업 예시
            File file = new File(uploadDirectory, "test.txt");
            Files.write(file.toPath(), "안녕하세요!".getBytes());
            System.out.println("파일 생성 성공: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
