<!-- eslint-disable vue/multi-word-component-names -->
<!-- eslint-disable vue/no-deprecated-v-on-native-modifier -->
<template>
  <div>
    <!-- 로그인 및 회원가입 페이지가 아닐 때만 레이아웃을 렌더링 -->
    <div v-if="!Login_Register_Page">
      <span class="container">
        <span class="left_panel">   
          <h2 class="title">
            <RouterLink to="/home" class="link" @click.native="refreshPage">Instagram</RouterLink>
          </h2>

          <RouterLink class="img" to="/home">
            <img src="../../../public/assets/house-solid.svg" @click.native="refreshPage" />
          </RouterLink>
          <h2 class="title_2">
            <RouterLink to="/home" class="link" @click.native="refreshPage">홈</RouterLink>
          </h2>

          <RouterLink class="img_4" to="/chat">
            <img src="../../../public/assets/meme.png" id="tt" />
          </RouterLink>
          <h2 class="title_5">
            <RouterLink to="/chat" class="link">메세지</RouterLink>
          </h2>

          <div class="img_2" @click="openModal">  
            <img src="../../../public/assets/square-plus-regular.svg" />
          </div>
          <h2 class="title_3" @click="openModal">
            <div class="link">만들기</div>
          </h2>

          <RouterLink class="img_3" to="/profile">
            <img id="dd" src="../../../public/assets/유메노아카링.png" />
          </RouterLink>
          <h2 class="title_4">
            <RouterLink to="/profile" class="link">프로필</RouterLink>
          </h2>
        </span>
        <span class="right_panel">
          <RouterView />
          <!-- 오른쪽 패널에 라우터 뷰 -->
        </span>
      </span>
      <!-- 모달 컴포넌트 추가 -->
      <Modal :isOpen="isModalOpen" @close="closeModal" />
    </div>

    <!-- 로그인 및 회원가입 페이지일 때는 RouterView만 표시 -->
    <div v-else>
      <RouterView />
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Modal from '../../components/HomeViews/Modal.vue'

const route = useRoute()
const router = useRouter()
const isModalOpen = ref(false)

// 모달 열림 상태 관리
const openModal = () => {
  isModalOpen.value = true
  console.log(isModalOpen.value) // 로그 확인
}

const closeModal = () => {
  isModalOpen.value = false
}

// 페이지 새로고침 및 home 경로로 이동
const refreshPage = () => {
  router.push({ path: '/home' }).then(() => {
    window.location.reload() // 새로고침을 포함하여 home으로 이동
  })
}

// 로그인, 회원가입, 대기열 페이지 여부를 동적으로 확인
const Login_Register_Page = ref(false)

watchEffect(() => {
  // 로그인, 회원가입, 대기열 페이지를 판별
  Login_Register_Page.value =
    route.path === '/' ||
    route.path === '/login' ||
    route.path === '/register' ||
    route.path === '/queue'
})

// 모달이 열리고 닫힐 때 body 스크롤 상태 변경
watchEffect(() => {
  const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth // 스크롤바의 너비 계산
  if (isModalOpen.value) {
    document.body.style.overflow = 'hidden' // 모달이 열릴 때 스크롤 막기
    document.body.style.paddingRight = `${scrollbarWidth}px` // 스크롤바 너비만큼 padding 추가
  } else {
    document.body.style.overflow = '' // 모달이 닫힐 때 원래 상태로 복구
    document.body.style.paddingRight = '' // padding 복구
  }
})
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
}
#tt {
  width: 45px;
}
#dd {
  width: 45px;
  border: 1px solid black;
  border-radius: 50%;
}
.img {
  grid-column: 1/1;
  grid-row: 3/3;
  width: 45px;
  margin-top: -6px;
}
.img_2 {
  cursor: pointer;
  grid-column: 1/1;
  grid-row: 12/13;
  width: 45px;
  margin-top: -8px;
}
.img_3 {
  grid-column: 1/1;
  grid-row: 14/14;
  width: 45px;
  margin-top: -37px;
  margin-left: -1px;
}
.img_4 {
  grid-column: 1/1;
  grid-row: 12/13;
  width: 45px;
  margin-top: -95px;
}
.title {
  margin: 25px;
  margin-left: 10px;
  font-family: 'Billabong', cursive;
  font-size: 30px;
  grid-column: 1/6;
  grid-row: 1/2;
  margin-top: 36px;
}
.title_2 {
  grid-column: 2/6;
  grid-row: 3/4;
  margin-left: 10px;
}
.title_3 {
  grid-column: 2/6;
  grid-row: 12/13;
  margin-left: 10px;
}
.title_4 {
  grid-column: 2/6;
  grid-row: 14/14;
  margin-left: 10px;
  margin-top: -30px;
}
.title_5 {
  grid-column: 2/6;
  grid-row: 12/13;
  margin-left: 10px;
  margin-top: -95px;
}
.container {
  display: grid;
  grid-template-columns: 310px 1fr;
  height: 90vh; /* 높이를 100vh로 설정 */
  width: 80vw;
  margin-left: -320px;
}

.left_panel {
  position: fixed; /* 사이드바를 고정 */
  top: 0; /* 상단 고정 */
  left: 20px;
  width: 310px; /* 사이드바 너비 */
  height: 100vh; /* 전체 높이 */
  border-right: 1px solid rgb(207, 205, 205);
  background-color: white; /* 배경색 추가 */
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  grid-template-rows: repeat(15, minmax(0, 1fr));
}

.link {
  cursor: pointer;
  text-decoration: none;
  color: black;
}
</style>
