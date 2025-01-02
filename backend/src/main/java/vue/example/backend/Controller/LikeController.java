package vue.example.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vue.example.backend.Util.JwtTokenUtil;
import vue.example.backend.Mapper.Likemapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private Likemapper likemapper;

    @Autowired
    private RedisTemplate<String, String> redisString;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Redis에 데이터 저장
    public void saveData(String key, String data) {
        redisString.opsForValue().set(key, data);
    }

    // Redis에서 데이터 가져오기
    public String getData(String key) {
        return redisString.opsForValue().get(key);
    }

    // 좋아요 토글
    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody Map<String, Object> request) {
        String postId = (String) request.get("post_id");
        String userKeyUUID = (String) request.get("userKeyUUID");

        String token = getData(userKeyUUID);
        if (token == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "유효하지 않은 사용자입니다."));
        }

        // JWT 토큰에서 사용자 ID 추출
        int userId = jwtTokenUtil.getUserIdFromToken(token); // 사용자 ID 직접 추출

        Map<String, Object> params = new HashMap<>();
        params.put("post_id", postId);
        params.put("user_id", userId);

        boolean isLiked = likemapper.isLiked(params) > 0;

        if (isLiked) {
            likemapper.deleteLike(params);
        } else {
            likemapper.insertLike(params);
        }

        int likeCount = likemapper.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", isLiked ? "좋아요가 삭제되었습니다." : "좋아요가 추가되었습니다.");
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    // 좋아요 삭제
    @DeleteMapping("/like")
    public ResponseEntity<Map<String, Object>> deleteLike(@RequestBody Map<String, Object> request) {
        String postId = (String) request.get("post_id");
        String userKeyUUID = (String) request.get("userKeyUUID");

        String token = getData(userKeyUUID);
        if (token == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "유효하지 않은 사용자입니다."));
        }

        // JWT 토큰에서 사용자 ID 추출
        int userId = jwtTokenUtil.getUserIdFromToken(token);

        Map<String, Object> params = new HashMap<>();
        params.put("post_id", postId);
        params.put("user_id", userId);

        boolean isLiked = likemapper.isLiked(params) > 0;

        if (isLiked) {
            likemapper.deleteLike(params);
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "좋아요가 존재하지 않습니다."));
        }

        int likeCount = likemapper.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "좋아요가 삭제되었습니다.");
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    // 좋아요 상태 확인
    @PostMapping("/like/check")
    public boolean isLiked(@RequestBody Map<String, Object> request) {
        String postId = (String) request.get("post_id");
        String userKeyUUID = (String) request.get("userKeyUUID");

        String token = getData(userKeyUUID);
        if (token == null) {
            return false;
        }

        int userId = jwtTokenUtil.getUserIdFromToken(token);

        Map<String, Object> params = new HashMap<>();
        params.put("post_id", postId);
        params.put("user_id", userId);

        return likemapper.isLiked(params) > 0;
    }

    // 좋아요 수 조회
    @GetMapping("/count/{postId}")
    public int getLikeCount(@PathVariable String postId) {
        return likemapper.getLikeCount(postId);
    }
}
