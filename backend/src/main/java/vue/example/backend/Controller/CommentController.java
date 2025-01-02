package vue.example.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vue.example.backend.Mapper.Commentmapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class CommentController {

    @Autowired
    private Commentmapper commentMapper;

    @PostMapping("/comments")
    public ResponseEntity<Map<String, String>> addComment(@RequestBody Map<String, Object> request) {
        // request에서 post_id, user_id, content 가져옴
        String postId = (String) request.get("post_id");
        int userId = (int) request.get("user_id");
        String content = (String) request.get("content");

        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        params.put("userId", userId);
        params.put("content", content);

        // 댓글 등록
        commentMapper.insertComment(params);

        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글이 등록되었습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comment/{postId}")
    public ResponseEntity<Object> getComments(@PathVariable String postId) {
        List<HashMap<String, Object>> comments = commentMapper.SelectComment(postId);

        if (comments.isEmpty()) {
            // 댓글이 없을 경우 메시지를 담은 맵 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "아직 댓글이 없습니다.");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(comments);
    }   

}
