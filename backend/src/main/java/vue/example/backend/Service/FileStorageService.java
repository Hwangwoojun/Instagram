package vue.example.backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 파일을 서버에 저장하는 메서드
    public String storeFile(MultipartFile file) {
        try {
            // 파일 이름을 UUID로 변경하여 중복 방지
            String originalFileName = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

            Path targetLocation = Paths.get(uploadDir).resolve(fileName);

            // 파일을 지정된 경로에 저장
            Files.copy(file.getInputStream(), targetLocation);

            System.out.println("파일이 저장되었습니다: " + targetLocation.toString());

            // 저장된 파일 경로를 반환
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장이 안됨 " + file.getOriginalFilename() + " 다시 하셈 ", e);
        }
    }
}
