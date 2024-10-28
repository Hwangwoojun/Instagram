package vue.example.backend.Mapper;

import java.util.HashMap;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Usermapper {
    Integer Token_Token(String emailOrUsername);

    HashMap findByEmailOrUsername(String emailOrUsername); // 이메일 또는 사용자 이름으로 사용자 검색

    HashMap LoginUser(HashMap<String, String> UserCheck); // 변수명 UserCheck로 수정

    // 사용자 중복 체크
    int insertCheck(HashMap<String, String> userInfo);

    int emailCheck(String email);

    int phoneCheck(String phone);

    int userNameCheck(String username);

    void insertUser(HashMap<String, String> userInfo);
}
