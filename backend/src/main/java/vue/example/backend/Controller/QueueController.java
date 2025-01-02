package vue.example.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.HashMap;

import vue.example.backend.Mapper.Queuemapper;
import vue.example.backend.Util.JwtTokenUtil;

@CrossOrigin(origins = "*")
@RestController
public class QueueController {

    private final Queue<String> queue = new LinkedList<>();
    private final Random random = new Random();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Queuemapper queuemapper;

    // 초기화: 50명의 사용자 대기열 생성
    public QueueController() {
        for (int i = 1; i <= 100; i++) {
            queue.add("user-" + i); // 예: "user-1", "user-2" 형태로 사용자 키 추가
        }
    }

    // 대기열에서 사용자 처리
    @GetMapping("/process")
    public HashMap<String, Object> processQueue() {
        HashMap<String, Object> response = new HashMap<>();

        String userKeyUUID;
        synchronized (queue) {
            userKeyUUID = queue.poll(); // 대기열에서 사용자 가져오기
        }

        if (userKeyUUID == null) {
            response.put("status", "error");
            response.put("message", "대기열이 비어 있습니다.");
            return response;
        }

        // Redis에서 토큰 가져오기
        String token = redisTemplate.opsForValue().get(userKeyUUID);
        if (token == null) {
            response.put("status", "error");
            response.put("message", "유효하지 않은 userKeyUUID입니다.");
            return response;
        }

        // JWT 토큰 해석 및 사용자 ID 추출
        try {
            int userId = jwtTokenUtil.getUserIdFromToken(token); // 토큰에서 ID 추출

            // DB에서 사용자 존재 여부 확인
            HashMap user = queuemapper.UserId(userId);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "해당 사용자 ID가 DB에 존재하지 않습니다.");
                return response;
            }

            // 사용자 처리 완료
            response.put("status", "success");
            response.put("message", "요청이 처리되었습니다.");
            response.put("userId", userId); // 사용자 ID 반환

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "토큰 검증 실패: " + e.getMessage());
        }

        return response;
    }

    // 대기열 상태 조회 (남은 대기열 수)
    @GetMapping("/queue/status")
    public HashMap<String, Object> getQueueStatus() {
        HashMap<String, Object> response = new HashMap<>();
        synchronized (queue) {
            if (!queue.isEmpty()) {
                queue.poll(); // 대기열에서 한 명을 처리하여 감소
            }
            response.put("queueCount", queue.size()); // 대기열의 현재 인원 수를 반환
        }
        return response;
    }
}
