package vue.example.backend.Controller;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vue.example.backend.Mapper.Usermapper;
import vue.example.backend.Service.JwtUserDetailsService;
import vue.example.backend.Util.JwtTokenUtil;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController {

    @Autowired
    private Usermapper usermapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public HashMap<String, Object> signIn(@RequestBody HashMap<String, String> userCheck) {
        String emailOrUsername = userCheck.get("emailOrPhone");
        String password = userCheck.get("password");

        HashMap user = usermapper.LoginUser(userCheck); // 사용자 정보 조회
        HashMap<String, Object> response = new HashMap<>();

        if (user != null) {
            // BCryptPasswordEncoder를 이용해 입력한 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, (String) user.get("password"))) {
                // 토큰 생성
                String token = jwtTokenUtil.generateToken(emailOrUsername);
                response.put("status", "success");
                response.put("token", token); // JWT 토큰
                response.put("user_name", user.get("user_name"));
                response.put("id", user.get("id"));
            } else {
                response.put("status", "error");
                response.put("message", "잘못된 비밀번호입니다. 다시 확인하세요.");
            }
        } else {
            response.put("status", "error");
            response.put("message", "잘못된 비밀번호입니다. 다시 확인하세요.");
        }

        return response;
    }

    // 사용자 이름 중복 체크
    @GetMapping("/check-username/{username}")
    public Boolean checkUserName(@PathVariable String username) {
        try {
            int count = usermapper.userNameCheck(username);
            return count > 0; // 중복 사용자 이름이 있으면 true, 없으면 false 반환
        } catch (Exception e) {
            System.out.println("사용자 이름 체크 실패 : " + e.getMessage());
            return false; // 예외 발생 시 false 반환
        }
    }

    // 이메일 중복 체크
    @GetMapping("/check-email/{email}")
    public Boolean checkEmail(@PathVariable String email) {
        try {
            int count = usermapper.emailCheck(email); // emailCheck 메서드 구현 필요
            return count > 0; // 중복 이메일이 있으면 true, 없으면 false 반환
        } catch (Exception e) {
            System.out.println("이메일 체크 실패 : " + e.getMessage());
            return false; // 예외 발생 시 false 반환
        }
    }

    // 전화번호 중복 체크
    @GetMapping("/check-phone/{phone}")
    public Boolean checkPhone(@PathVariable String phone) {
        try {
            int count = usermapper.phoneCheck(phone); // phoneCheck 메서드 구현 필요
            return count > 0; // 중복 전화번호가 있으면 true, 없으면 false 반환
        } catch (Exception e) {
            System.out.println("전화번호 체크 실패 : " + e.getMessage());
            return false; // 예외 발생 시 false 반환
        }
    }

    // 회원가입 요청 처리
    @PostMapping("/User")
    public Boolean register(@RequestBody HashMap<String, String> userInfo) {
        try {
            // 사용자 비밀번호를 암호화하여 저장
            String rawPassword = userInfo.get("password");
            String encodedPassword = passwordEncoder.encode(rawPassword);
            userInfo.put("password", encodedPassword); // 암호화된 비밀번호로 대체
            System.out.println("암호화 된 값 : " + encodedPassword);

            usermapper.insertUser(userInfo);
            return true; // 성공 시 true 반환
        } catch (Exception e) {
            System.out.println("회원가입 실패 : " + e.getMessage());
            return false; // 예외 발생 시 false 반환
        }
    }

}
