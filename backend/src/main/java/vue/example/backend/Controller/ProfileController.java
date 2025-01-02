package vue.example.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vue.example.backend.Mapper.Profilemapper;
import vue.example.backend.Util.JwtTokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
public class ProfileController {

    @Autowired
    private Profilemapper profilemapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 프로필 기본 정보 가져오기
    @GetMapping("/profile/{token}")
    public ResponseEntity<?> getProfileInfo(@PathVariable("token") String token) {
        int userId;
        try {
            userId = jwtTokenUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid token: " + e.getMessage());
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("id", userId);

        // 사용자 기본 정보 가져오기
        HashMap<String, Object> profileData = profilemapper.ProfilePosts(params);

        if (profileData == null) {
            return ResponseEntity.badRequest().body("No profile data found for the user.");
        }

        return ResponseEntity.ok(profileData);
    }

    // 프로필 사진 삽입
    @PostMapping("/profile/photo")
    public ResponseEntity<?> insertProfilePhoto(@RequestBody HashMap<String, Object> requestData) {
        if (!requestData.containsKey("userId") || requestData.get("userId") == null) {
            return ResponseEntity.badRequest().body("Invalid request: Missing 'userId'");
        }

        String userId = (String) requestData.get("userId");
        String imageUrl = generateImageUUID(); // UUID 기반 파일 이름 생성

        HashMap<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("userId", userId);
        imageInfo.put("imageUrl", imageUrl);

        try {
            profilemapper.insertProfile(imageInfo);
            return ResponseEntity.ok("Profile photo added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to insert profile photo: " + e.getMessage());
        }
    }

    // 프로필 사진 삭제
    @DeleteMapping("/profile/photo/{userId}")
    public ResponseEntity<?> deleteProfilePhoto(@PathVariable("userId") String userId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        try {
            profilemapper.deleteProfile(params);
            return ResponseEntity.ok("Profile photo deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to delete profile photo: " + e.getMessage());
        }
    }

    // 사용자 게시물 이미지 가져오기
    @GetMapping("/profile/img/{token}")
    public ResponseEntity<?> getProfileImages(@PathVariable("token") String token) {
        int userId;
        try {
            userId = jwtTokenUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid token: " + e.getMessage());
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("id", userId);

        // 사용자가 작성한 게시물의 첫 번째 이미지들 가져오기
        List<HashMap<String, Object>> postImages = profilemapper.ProfileImgPosts(params);

        if (postImages == null || postImages.isEmpty()) {
            return ResponseEntity.badRequest().body("No post images found for the user.");
        }

        return ResponseEntity.ok(postImages);
    }

    // UUID 기반 이미지 파일명 생성 메서드
    private String generateImageUUID() {
        return UUID.randomUUID().toString();
    }
}
