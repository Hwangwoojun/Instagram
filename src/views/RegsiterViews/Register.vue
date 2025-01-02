<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="register_container">
    <h1 class="instagram_title">instagram</h1>
    <p class="register_prompt">친구들의 사진과 동영상을 보려면 가 <br />입하세요.</p>

    <div class="input_group">
      <input
        v-model="emailOrPhone"
        type="text"
        class="input_field"
        placeholder="휴대폰 번호 또는 이메일 주소"
        @blur="checkEmailOrPhone"
        @mouseleave="checkEmailOrPhone"
        @keyup.enter="register"
      />
      <p v-if="emailOrPhoneError" class="error_message_2">{{ emailOrPhoneError }}</p>
    </div>

    <div class="input_group">
      <input
        v-model="password"
        type="password"
        class="input_field"
        placeholder="비밀번호"
        @mouseleave="checkPasswordLength"
        @keyup.enter="register"
      />
      <p v-if="passwordLengthError" class="error_message_2">{{ passwordLengthError }}</p>
    </div>

    <div class="input_group">
      <input
        v-model="password_check"
        type="password"
        class="input_field"
        placeholder="비밀번호 확인"
        @mouseleave="checkPasswordMatch"
        @keyup.enter="register"
      />
      <p v-if="passwordError" class="error_message_2">{{ passwordError }}</p>
    </div>

    <div class="input_group">
      <input
        v-model="name"
        type="text"
        class="input_field"
        placeholder="성명"
        @blur="checkName"
        @mouseleave="checkName"
        @keyup.enter="register"
      />
      <p v-if="nameError" class="error_message_2">{{ nameError }}</p>
    </div>

    <div class="input_group">
      <input
        v-model="userName"
        type="text"
        class="input_field"
        placeholder="사용자 이름"
        @blur="validateUserName"
        @mouseleave="validateUserName"
      />
      <p v-if="userNameError" class="error_message_2">{{ userNameError }}</p>
    </div>

    <div class="button">
      <button class="register_button" @click="register">가입</button>
    </div>

    <div class="login_prompt">
      계정이 있으신가요? <RouterLink to="/" class="login">로그인</RouterLink>
    </div>

    <!-- 모달 컴포넌트 추가 -->
    <modal-component
      :title="modalTitle"
      :message="modalMessage"
      :isOpen="isModalOpen"
      @closeModal="closeModal"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import axios from 'axios'
import ModalComponent from '@/components/ModalConponent/Registermodalconponent.vue' // 모달 컴포넌트 가져오기

// 각 입력 값들을 ref로 선언하여 반응형으로 관리
const emailOrPhone = ref('')
const name = ref('')
const userName = ref('')
const password = ref('')
const password_check = ref('')
const emailOrPhoneError = ref('')
const userNameError = ref('')
const passwordError = ref('')
const passwordLengthError = ref('')
const emailOrPhoneChecked = ref(true)
const userNameChecked = ref(true)
const nameError = ref('')
const router = useRouter()

// 모달 상태 관리
const isModalOpen = ref(false) // 모달 열림 상태
const modalTitle = ref('알림') // 모달 제목
const modalMessage = ref('') // 모달 메시지

// 모달 닫기 함수
const closeModal = () => {
  isModalOpen.value = false
}

const checkName = () => {
  const regExp = /[0-9!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]+/

  if (!name.value) {
    nameError.value = '' // 성명 입력이 없을 때 에러 메시지 추가
  } else if (regExp.test(name.value)) {
    nameError.value = '성명에는 숫자나 특수 문자를 사용할 수 없습니다.'
  } else {
    nameError.value = '' // 유효할 경우 에러 초기화
  }
}

// 비밀번호 길이 체크 함수
const checkPasswordLength = () => {
  if (!password.value) {
    passwordLengthError.value = '' // 비밀번호 입력이 없을 때 에러 메시지 초기화
  } else if (password.value.length < 6) {
    passwordLengthError.value = '6자 이상의 비밀번호를 만드세요.' // 비밀번호가 6자 미만일 때 에러 메시지
  } else {
    passwordLengthError.value = '' // 유효할 경우 에러 초기화
  }
}

// 비밀번호 확인 입력 필드에서 입력할 때 에러 체크 함수
const checkPasswordMatch = () => {
  if (password.value !== password_check.value) {
    passwordError.value = '비밀번호가 일치하지 않습니다.'
  } else {
    passwordError.value = ''
  }
}

// 이메일 또는 전화번호 중복 체크 함수
const checkEmailOrPhone = () => {
  // 입력 값이 비어있으면 에러 메시지 초기화
  if (!emailOrPhone.value) {
    emailOrPhoneError.value = ''
    emailOrPhoneChecked.value = true
    return
  }

  // 전화번호가 숫자만 포함하고 있는지 체크
  const isPhoneNumber = /^[0-9]+$/.test(emailOrPhone.value)
  if (isPhoneNumber) {
    // 전화번호 길이 체크
    if (emailOrPhone.value.length !== 11) {
      emailOrPhoneError.value =
        '휴대폰 번호가 정확하지 않습니다. 국가 번호를 포함하여 전체 전화번호를 입력해주세요.'
      emailOrPhoneChecked.value = false
      return
    }
  } else if (!emailOrPhone.value.includes('@')) {
    // 이메일이나 전화번호에 필수 기호(@)가 없는 경우 에러 처리
    emailOrPhoneError.value = '올바른 이메일 주소를 입력하세요.'
    emailOrPhoneChecked.value = false
    return
  } else {
    // 이메일 형식 체크
    const isValidEmail = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com)$/.test(emailOrPhone.value)
    if (!isValidEmail) {
      emailOrPhoneError.value = '올바른 이메일 주소를 입력하세요.'
      emailOrPhoneChecked.value = false
      return
    }
  }

  if (emailOrPhone.value.includes('@')) {
    axios
      .get(`/check-email/${emailOrPhone.value}`)
      .then((res) => {
        if (res.data) {
          emailOrPhoneError.value = '이미 사용중인 이메일 입니다.'
          emailOrPhoneChecked.value = false
        } else {
          emailOrPhoneError.value = ''
          emailOrPhoneChecked.value = true
        }
      })
      .catch((error) => {
        console.error('이메일 중복 체크 오류:', error)
      })
  } else {
    const phoneWithoutDash = emailOrPhone.value.replace(/-/g, '') // 하이픈 제거
    axios
      .get(`/check-phone/ ${phoneWithoutDash}`)
      .then((res) => {
        if (res.data) {
          emailOrPhoneError.value = '이미 사용중인 전화번호 입니다.'
          emailOrPhoneChecked.value = false
        } else {
          emailOrPhoneError.value = ''
          emailOrPhoneChecked.value = true
        }
      })
      .catch((error) => {
        console.error('전화번호 중복 체크 오류:', error)
      })
  }
}

// 사용자 이름 검증 함수
const validateUserName = () => {
  // 사용자 이름이 한글이 포함되어 있는지 확인
  const isKorean = /[가-힣]/.test(userName.value)
  const isValidUserName = /^[a-zA-Z0-9._]+$/.test(userName.value) // 영어, 숫자, 밑줄 및 마침표만 허용
  const isOnlyDigits = /^\d+$/.test(userName.value) // 숫자만 포함되어 있는지 확인

  // 사용자 이름이 비어있으면 에러 초기화
  if (!userName.value) {
    userNameError.value = '' // 아무것도 입력하지 않으면 에러 초기화
    userNameChecked.value = false // 입력 값이 없으므로 중복 체크를 false로 설정
    return
  }

  // 사용자 이름이 한글이 포함되어 있는지 또는 유효하지 않은 형식인지 확인
  if (isKorean) {
    userNameError.value = '사용자 이름에는 영어, 숫자, 밑줄 및 마침표만 사용할 수 있습니다.'
    userNameChecked.value = false // 에러가 있으면 중복 체크를 false로 설정
    return
  } else if (!isValidUserName) {
    userNameError.value = '사용자 이름에는 영어, 숫자, 밑줄 및 마침표만 사용할 수 있습니다.'
    userNameChecked.value = false // 유효하지 않은 형식이면 중복 체크를 false로 설정
    return
  } else if (isOnlyDigits) {
    userNameError.value = '사용자 이름에 숫자만 포함할 수는 없습니다.'
    userNameChecked.value = false // 숫자만 포함되어 있으면 중복 체크를 false로 설정
    return
  } else if (userName.value.length < 3) {
    userNameError.value = '사용자 이름은 3자 이상이어야 합니다.'
    userNameChecked.value = false // 에러가 있으면 중복 체크를 false로 설정
    return
  }

  axios
    .get(`/check-username/${userName.value}`)
    .then((res) => {
      if (res.data) {
        userNameError.value = '이미 사용중인 사용자 이름입니다.'
        userNameChecked.value = false // 중복된 사용자 이름이면 체크 false
      } else {
        userNameError.value = '' // 중복이 없으면 에러 초기화
        userNameChecked.value = true // 중복이 없으므로 체크 true
      }
    })
    .catch((error) => {
      console.error('사용자 이름 중복 체크 오류:', error)
    })
}

// 회원가입 처리 함수
const register = () => {
  // 중복 체크 재확인
  checkEmailOrPhone()
  validateUserName()

  // 중복 체크 및 입력 값 검증
  if (
    !emailOrPhone.value ||
    !password.value ||
    !password_check.value ||
    !name.value ||
    !userName.value ||
    emailOrPhoneError.value ||
    userNameError.value ||
    passwordError.value ||
    !emailOrPhoneChecked.value ||
    !userNameChecked.value
  ) {
    modalMessage.value = '입력되지 않은 항목이 있거나 중복된 값이 있습니다.'
    isModalOpen.value = true // 모달 열기
    return
  }

  const userData = {
    email: emailOrPhone.value.includes('@') ? emailOrPhone.value : null,
    phone: !emailOrPhone.value.includes('@') ? emailOrPhone.value.replace(/-/g, '') : null, // 하이픈 제거
    name: name.value,
    user_name: userName.value,
    password: password.value
  }

  axios
    .post('/User', userData)
    .then((res) => {
      if (res.data) {
        modalMessage.value = '회원가입에 성공 하셨습니다!'
        isModalOpen.value = true
        setTimeout(() => {
          router.push('/') // 2초 후 로그인 화면으로 이동
        }, 2000)
      } else {
        modalMessage.value = '회원가입 중 오류가 발생했습니다. 다시 시도해 주세요.'
        isModalOpen.value = true
      }
    })
    .catch((error) => {
      console.error('회원가입 중 오류 발생:', error)
      modalMessage.value = '회원가입 중 오류가 발생했습니다.'
      isModalOpen.value = true
    })
}
</script>

<style scoped>
.register_container {
  width: 520px;
  margin-top: 54px;
  padding: 30px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.instagram_title {
  font-family: 'Billabong', cursive;
  font-size: 48px;
  margin-bottom: 15px;
  text-align: center;
}

.register_prompt {
  text-align: center;
  margin-bottom: 25px;
  font-weight: bold;
  color: gray;
  font-size: 17px;
}

.input_group {
  text-align: center;
  margin-left: 9px;
}

.input_field {
  width: 356px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.button {
  text-align: center;
}

.register_button {
  width: 300px;
  padding: 10px;
  background-color: #0095f6;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: bold;
  font-size: 15px;
  margin-bottom: 10px;
  text-align: center;
}

.register_button:hover {
  background-color: #007bbf;
}

.login_prompt {
  text-align: center;
  margin-top: 15px;
}

.login {
  color: #0095f6;
  font-weight: bold;
  text-decoration: none;
}

.error_message {
  color: red;
  text-align: center;
  margin-top: -30px;
}
.error_message_2 {
  margin-left: 30px;
  color: red;
  text-align: left;
  width: 380px;
  font-size: 15px;
  margin-top: -30px;
}
</style>
