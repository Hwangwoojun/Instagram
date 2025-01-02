package vue.example.backend.Mapper;

import java.util.HashMap;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Queuemapper {

    // UserId로 사용자 조회
    HashMap UserId(int UserId);
}
