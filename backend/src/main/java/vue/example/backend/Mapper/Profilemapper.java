package vue.example.backend.Mapper;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Profilemapper {

    // 사용자 정보 및 게시물 개수 가져오기 메서드
    HashMap<String, Object> ProfilePosts(HashMap<String, Object> params);

    // 사용자가 작성한 게시물들의 첫 번째 이미지만 가져오기 메서드
    List<HashMap<String, Object>> ProfileImgPosts(HashMap<String, Object> params);

    // 프로필 사진 삽입 메서드
    void insertProfile(HashMap<String, Object> imageInfo);

    // 프로필 사진 삭제 메서드
    void deleteProfile(HashMap<String, Object> params);
}
