package vue.example.backend.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vue.example.backend.Mapper.Postmapper;
import vue.example.backend.Mapper.Usermapper;
import vue.example.backend.Service.FileStorageService;
import vue.example.backend.Util.JwtTokenUtil;

@CrossOrigin(origins = "*")
@RestController
public class PostController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private Postmapper postmapper;

    @Autowired
    private Usermapper usermapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 파일 업로드 및 게시물 저장 메서드
    @PostMapping("/upload")
    public ResponseEntity<HashMap<String, Object>> uploadPost(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "content", required = false) String content,
            @RequestHeader("Authorization") String token)
            throws IOException {

        HashMap<String, Object> response = new HashMap<>();

        // JWT 토큰에서 사용자 ID 추출
        int userId = jwtTokenUtil.getUserIdFromToken(token.substring(7)); // Bearer 부분 제거 후 해독

        // UUID 생성 (게시물 ID)
        String postId = UUID.randomUUID().toString();

        // 게시물 정보를 DB에 저장
        HashMap<String, Object> postInfo = new HashMap<>();
        postInfo.put("post_id", postId);
        postInfo.put("user_id", userId);
        postInfo.put("content", content);
        postmapper.insertPost(postInfo);

        // 파일 저장 및 DB에 이미지 경로 저장
        for (MultipartFile file : files) {
            String filePath = fileStorageService.storeFile(file);
            HashMap<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("image_id", null);
            imageInfo.put("post_id", postId);
            imageInfo.put("image_url", filePath);
            postmapper.insertPostImage(imageInfo);
        }

        response.put("status", "success");
        response.put("message", "게시물 업로드 완료!");
        return ResponseEntity.ok(response);
    }

    // 게시물 목록 가져오기 메서드
    @PostMapping("/posts")
    public ResponseEntity<HashMap<String, Object>> getPosts(@RequestBody HashMap<String, Integer> request) {
        HashMap<String, Object> response = new HashMap<>();

        int page = request.getOrDefault("page", 0);
        int offset = page * 4; // 페이지당 4개의 게시물

        HashMap<String, Object> params = new HashMap<>();
        params.put("offset", offset); // OFFSET 값 추가

        List<HashMap<String, Object>> posts = postmapper.selectPosts(params);

        // 각 게시물에 대한 이미지 목록 추가
        for (HashMap<String, Object> post : posts) {
            String postId = (String) post.get("post_id");
            List<String> images = postmapper.selectPostImages(postId);
            post.put("images", images); // 게시물에 이미지 추가
        }

        response.put("status", "success");
        response.put("posts", posts);

        // 더 이상 게시물이 없는 경우 empty 배열 반환
        if (posts.isEmpty()) {
            response.put("message", "No more posts"); // 클라이언트에서 감지할 수 있도록 메시지 추가
        }

        return ResponseEntity.ok(response); // 성공 응답 반환
    }

    @GetMapping("/post/count/{postId}")
    public ResponseEntity<Map<String, Integer>> countComments(@PathVariable String postId) {
        int commentCount = postmapper.countComments(postId);

        Map<String, Integer> response = new HashMap<>();
        response.put("count", commentCount);
        return ResponseEntity.ok(response);
    }

    // 게시물 및 이미지 업데이트 메서드
    @PutMapping("/posts/{postId}")
    public ResponseEntity<HashMap<String, Object>> updatePost(
            @PathVariable String postId,
            @RequestBody HashMap<String, Object> requestBody,
            @RequestHeader("Authorization") String token) {

        HashMap<String, Object> response = new HashMap<>();

        // JWT 토큰에서 사용자 ID 추출
        int userId = jwtTokenUtil.getUserIdFromToken(token.substring(7));

        // 게시물 정보 업데이트
        HashMap<String, Object> postInfo = new HashMap<>();
        postInfo.put("post_id", postId);
        postInfo.put("content", requestBody.get("content"));
        postmapper.updatePosts(postInfo);

        // 기존 이미지 삭제 및 새 이미지 추가
        postmapper.deleteImagesPostId(postId);
        List<String> updatedImageUrls = (List<String>) requestBody.get("images");
        for (String imageUrl : updatedImageUrls) {
            HashMap<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("image_id", null);
            imageInfo.put("post_id", postId);
            imageInfo.put("image_url", imageUrl);
            postmapper.insertPostImage(imageInfo);
        }

        response.put("status", "success");
        response.put("message", "게시물 업데이트 완료!");
        return ResponseEntity.ok(response);
    }

    // 게시물 삭제 메서드
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<HashMap<String, Object>> deletePost(@PathVariable String postId) {
        HashMap<String, Object> response = new HashMap<>();

        // 1. 관련 이미지 삭제
        postmapper.deleteImagesByPostId(postId); // 관련 이미지 삭제

        // 2. 게시물 삭제
        postmapper.deletePostId(postId); // 게시물 삭제

        // 3. 응답 메시지 설정
        response.put("status", "success");
        response.put("message", "게시물이 삭제되었습니다.");

        return ResponseEntity.ok(response); // 성공 응답 반환
    }
}