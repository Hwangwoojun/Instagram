package vue.example.backend.Mapper;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Postmapper {
    // 게시물 추가 메서드
    void insertPost(HashMap<String, Object> postInfo);

    // 게시물 추가 이미지 메서드
    void insertPostImage(HashMap<String, Object> imageInfo);

    // 게시물 목록 가져오기 메서드
    List<HashMap<String, Object>> selectPosts(HashMap<String, Object> params);

    // 특정 post_id에 대한 댓글 수 카운트 메서드
    int countComments(String postId);

    // 특정 post_id에 대한 이미지 URL 목록 가져오기 메서드
    List<String> selectPostImages(String postId);

    // 게시물 업데이트 메서드
    void updatePosts(HashMap<String, Object> postInfo);

    // 게시물 업데이트할 때 이미지 삭제 메서드
    void deleteImagesPostId(String postId);

    // 이미지 업데이트 메서드
    void updateImages(HashMap<String, Object> imageInfo);

    // 게시물 삭제 메서드
    void deletePostId(String postId);

    // 이미지 삭제 메서드
    void deleteImagesByPostId(String postId);

}
