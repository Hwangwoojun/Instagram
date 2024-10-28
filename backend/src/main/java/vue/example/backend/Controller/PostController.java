package vue.example.backend.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vue.example.backend.Mapper.Postmapper;
import vue.example.backend.Mapper.Usermapper;
import vue.example.backend.Service.FileStorageService;
import vue.example.backend.Util.JwtTokenUtil;

@CrossOrigin(origins = "http://localhost:5173")
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
            @RequestParam("files") List<MultipartFile> files, // 여러 파일을 받을 수 있도록 List로 변경
            @RequestParam(value = "content", required = false) String content, // content 파라미터를 선택적으로 설정
            @RequestHeader("Authorization") String token)
            throws IOException {

        HashMap<String, Object> response = new HashMap<>();

        // 1. JWT 토큰에서 사용자 이메일 또는 사용자 이름 추출
        String extractedEmailOrUsername = jwtTokenUtil.getUsernameFromToken(token.substring(7)); // Bearer 부분 제거 후 해독

        Integer userId = usermapper.Token_Token(extractedEmailOrUsername);
        if (userId == null) {
            response.put("status", "error");
            response.put("message", "사용자 존재하지 않음");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. UUID 생성 (게시물 ID)
        String postId = UUID.randomUUID().toString();

        // 3. 게시물 정보를 DB에 저장 (instagram_post 테이블에 저장)
        HashMap<String, Object> postInfo = new HashMap<>();
        postInfo.put("post_id", postId);
        postInfo.put("user_id", userId);
        postInfo.put("content", content); // content가 null일 경우 그대로 null로 설정
        postmapper.insertPost(postInfo); // 게시물 저장

        // 4. 파일을 서버에 저장하고 각각의 파일 경로를 DB에 저장
        for (MultipartFile file : files) {
            // 파일 저장
            String filePath = fileStorageService.storeFile(file);

            // 이미지 정보를 DB에 저장 (instagram_post_image 테이블에 저장)
            HashMap<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("image_id", null); // AUTO_INCREMENT로 자동 생성되므로 null 설정
            imageInfo.put("post_id", postId); // 해당 게시물 ID와 연결
            imageInfo.put("image_url", filePath); // 이미지 URL 저장
            postmapper.insertPostImage(imageInfo); // 이미지 저장
        }

        // 응답 메시지
        response.put("status", "success");
        response.put("message", "게시물 업로드 완료!");
        return ResponseEntity.ok(response); // 성공 응답 반환
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

}