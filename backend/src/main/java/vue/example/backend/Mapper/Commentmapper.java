package vue.example.backend.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Commentmapper {

    // 댓글 등록 메서드
    void insertComment(Map<String, Object> params);

    // 댓글 목록 조회 메서드
    List<HashMap<String, Object>> SelectComment(String postId);

}
