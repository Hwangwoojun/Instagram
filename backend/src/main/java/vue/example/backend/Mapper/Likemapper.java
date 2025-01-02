package vue.example.backend.Mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface Likemapper {

    // 좋아요 추가 메서드
    void insertLike(Map<String, Object> params); // 사용자 ID는 params에서 가져옴

    // 좋아요 삭제 메서드
    void deleteLike(Map<String, Object> params); // 사용자 ID는 params에서 가져옴

    // 좋아요 여부 확인 메서드
    int isLiked(Map<String, Object> params); // 사용자 ID는 params에서 가져옴

    // 특정 게시물의 좋아요 수 조회 메서드
    int getLikeCount(String postId);
}
