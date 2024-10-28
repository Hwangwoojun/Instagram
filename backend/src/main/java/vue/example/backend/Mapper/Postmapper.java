package vue.example.backend.Mapper;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Postmapper {
    void insertPost(HashMap<String, Object> postInfo);

    void insertPostImage(HashMap<String, Object> imageInfo);

    List<HashMap<String, Object>> selectPosts(HashMap<String, Object> params);

    // 특정 post_id에 대한 댓글 수 카운트 메서드
    int countComments(String postId);

    // 특정 post_id에 대한 이미지 URL 목록 가져오기
    List<String> selectPostImages(String postId);
}
