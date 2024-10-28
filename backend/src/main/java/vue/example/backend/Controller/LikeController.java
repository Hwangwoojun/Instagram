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

import vue.example.backend.Mapper.Likemapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class LikeController {

    @Autowired
    private Likemapper likemapper;

    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody Map<String, Object> request) {
        String postId = (String) request.get("post_id");
        int userId = (int) request.get("user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("post_id", postId);
        params.put("user_id", userId);

        // 좋아요 여부 확인
        boolean isLiked = likemapper.isLiked(params) > 0;

        if (isLiked) {
            // 이미 좋아요가 있는 경우, 삭제
            likemapper.deleteLike(params);
        } else {
            // 좋아요가 없는 경우, 추가
            likemapper.insertLike(params);
        }

        // 새로운 좋아요 수 조회
        int likeCount = likemapper.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", isLiked ? "좋아요가 삭제되었습니다." : "좋아요가 추가되었습니다.");
        response.put("likeCount", likeCount); // 새로운 좋아요 수 추가

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/like")
    public ResponseEntity<Map<String, String>> removeLike(@RequestBody Map<String, Object> request) {
        // request에서 post_id와 user_id 가져옴
        String postId = (String) request.get("post_id");
        int userId = (int) request.get("user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("post_id", postId);
        params.put("user_id", userId);

        // 좋아요 삭제
        likemapper.deleteLike(params);

        // JSON 형식으로 응답
        Map<String, String> response = new HashMap<>();
        response.put("message", "좋아요가 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 좋아요 상태 확인
    @PostMapping("/like/check")
    public boolean isLiked(@RequestBody Map<String, Object> request) {
        // request에서 post_id와 user_id 가져옴
        String postId = (String) request.get("post_id");
        int userId = (int) request.get("user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("post_id", postId);
        params.put("user_id", userId);

        // 좋아요 여부 확인 (이미 좋아요를 눌렀는지 여부 체크)
        return likemapper.isLiked(params) > 0; // true 또는 false 반환  
    }

    // 좋아요 수 조회
    @GetMapping("/count/{postId}")
    public int getLikeCount(@PathVariable String postId) {
        // 특정 게시물의 좋아요 수 조회
        return likemapper.getLikeCount(postId);
    }
}
