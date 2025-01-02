package vue.example.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vue.example.backend.Mapper.Usermapper;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private Usermapper usermapper;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        HashMap user = usermapper.findByEmailOrUsername(emailOrUsername); // 이메일 또는 사용자 이름으로 찾음
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + emailOrUsername);
        }

        // 사용자 정보 반환
        return new org.springframework.security.core.userdetails.User(
                (String) user.get("user_name"),
                (String) user.get("password"),
                new ArrayList<>());
    }
}
