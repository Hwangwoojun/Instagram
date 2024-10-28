package vue.example.backend.Mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface Likemapper {

    // 좋아요 추가 메서드
    void insertLike(Map<String, Object> params);

    // 좋아요 삭제 메서드
    void deleteLike(Map<String, Object> params);

    int isLiked(Map<String, Object> params);

    int getLikeCount(String postId);
}
