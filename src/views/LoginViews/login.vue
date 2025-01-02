<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="login_container">
    <img :src="image" class="login_image" :class="Class" />
    <div class="login_box">
      <h1 class="instagram_title">Instagram</h1>
      <div class="input_group">
        <input
          v-model="emailOrPhone"
          type="text"
          class="input_field"
          placeholder="전화번호, 사용자 이름 또는 이메일"
          @keyup.enter="checkLogin"
        />
      </div>
      <div class="input_group">
        <input
          v-model="password"
          type="password"
          class="input_field"
          placeholder="비밀번호"
          @keyup.enter="checkLogin"
        />
      </div>

      <button class="login_button" @click="checkLogin">로그인</button>

      <!-- 오류 메시지 표시 부분 -->
      <div v-if="errorMessage" class="error_message">{{ errorMessage }}</div>

      <div class="divider">
        <hr class="line" />
        <span class="or_text">또는</span>
        <hr class="line" />
      </div>

      <RouterLink to="/login_password" class="login_password">비밀번호를 잊으셨나요?</RouterLink>
      <div class="regsiter_prompt">
        계정이 없으신가요?
        <RouterLink to="/register" class="regsiter">가입하기</RouterLink>
      </div>
    </div>

    <!-- 모달 컴포넌트 추가 -->
    <LoginModalComponent
      :title="'로그인 '"
      :message="'정말 ' + userName + ' 으로 로그인 하시겠습니까?'"
      :isOpen="isModalOpen"
      @closeModal="isModalOpen = false"
      @confirmLogin="confirmLogin"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import axios from 'axios'
import Cookies from 'js-cookie'
import LoginModalComponent from '../../components/ModalConponent/loginmodalconponent.vue' // 모달 컴포넌트 임포트

const images = [
  import('/public/assets/aspas.webp'),
  import('/public/assets/meiy.webp'),
  import('/public/assets/woot.webp'),
  import('/public/assets/karon.webp'),
  import('/public/assets/유메노아카링.png')
]

const image = ref('')
let index = 0
const Class = ref('') // 현재 애니메이션 클래스 상태를 저장

const changeImage = () => {
  // 현재 이미지가 사라지는 애니메이션 추가
  Class.value = 'fading-out'

  // 이미지 변경 후 잠시 대기
  setTimeout(() => {
    // 현재 이미지의 경로를 가져오는 부분
    index = (index + 1) % images.length // 인덱스 증가
    images[index].then((imgModule) => {
      image.value = imgModule.default // default export 사용
      // 이미지가 변경된 후 다시 나타나는 애니메이션 추가
      Class.value = 'fading-in'
    })
  }, 1000) // 페이드 아웃 시간이 1초인 경우에 맞추어 대기 시간 설정
}

// 컴포넌트가 마운트될 때 interval 설정
onMounted(() => {
  // 초기 이미지 설정
  images[index].then((imgModule) => {
    image.value = `${imgModule.default}?t=${Date.now()}` // URL에 타임스탬프 추가
  })

  setInterval(() => {
    changeImage() // 8초마다 이미지 변경
  }, 8000)
})

onBeforeUnmount(() => {
  clearInterval() // 인터벌 정리
})

// 로그인 로직
const emailOrPhone = ref('') // 이메일 또는 전화번호를 위한 반응형 변수
const password = ref('') // 비밀번호를 위한 반응형 변수
const router = useRouter() // Vue Router를 사용하여 페이지 이동
const errorMessage = ref('') // 오류 메시지를 저장할 변수
const isModalOpen = ref(false) // 모달 열림 상태 관리
const userName = ref('') // 사용자 이름을 저장할 변수

const checkLogin = () => {
  errorMessage.value = '' // 로그인 시 오류 메시지를 초기화
  if (emailOrPhone.value.trim() === '' || password.value.trim() === '') {
    alert('전화번호 또는 이메일, 비밀번호를 입력하세요.') // 입력이 비어있을 경우 경고 표시
  } else {
    const loginData = {
      emailOrPhone: emailOrPhone.value, // 사용자가 입력한 전화번호 또는 이메일
      password: password.value // 사용자가 입력한 비밀번호
    }

    // 로그인 요청을 보내는 부분
    axios
      .post('/login', loginData) // Spring Boot 백엔드 로그인 URL에 POST 요청
      .then((res) => {
        console.log(res.data) // 서버로부터 받은 응답 데이터를 콘솔에 출력

        // 서버로부터 받은 응답 데이터가 있꼬, status가 'error'가 아니 경우 로그인 성고 처리
        if (res.data && res.data.status !== 'error') {
          Cookies.set('userName', res.data.user_name) // 사용자 이름을 쿠키에 저장
          Cookies.set('id', res.data.id) // 사용자 ID를 쿠키에 저장
          userName.value = res.data.user_name // 성공 시 사용자 이름을 Vuex 스토어에 저장
          isModalOpen.value = true // 모달 열기
          localStorage.setItem('token', res.data.token) // 로컬 스토리지에 JWT 토큰 저장
          localStorage.setItem('userKeyUUID', res.data.userKeyUUID) // 로컬 스토리지에 userKeyUUID 저장
          console.log('성공인가?' + res.data.token) // 토큰이 잘 저장되었는지 확인을 위한 콘솔 출력
        } else {
          errorMessage.value = res.data.message // 서버 응답에 오류가 있는 경우 오류 메시지 표시
        }
      })
      .catch((error) => {
        console.error('로그인 요청 중 오류 발생:', error) // 오류 내용을 콘솔에 출력
        errorMessage.value = '서버 오류가 발생했습니다. 다시 시도해주세요.' // 일반 오류 메시지
      })
  }
}

// 모달에서 확인 버튼 클릭 시 로그인 처리
const confirmLogin = () => {
  // 서버로부터 받은 JWT 토큰을 로컬 스토리지에 저장

  // 로그인에 성공하면 대기열 화면으로 이동한다
  router.push('/queue')
}
</script>

<style scoped></style>
