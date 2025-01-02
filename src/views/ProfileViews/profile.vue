<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="profile">
    <!-- 프로필 헤더 -->
    <div class="profile_header">
      <div class="profile_picture">
        <!-- 프로필 이미지 표시 -->
        <img :src="profileImage" alt="프로필 사진" class="profile_img" />
        <!-- 프로필 사진 변경 기능 -->
        <input type="file" @change="handleFileChange" />
      </div>
      <div class="profile_info">
        <div class="uuu">
          <h2 class="username">{{ username }}</h2>
          <button class="edit_profile_button">프로필 편집</button>
        </div>
        <div class="profile_stats">
          <!-- 게시물 수, 팔로워 및 팔로우 정보 -->
          <span>게시물 {{ postCount }}</span>
          <span>팔로워 0</span>
          <span>팔로우 0</span>
        </div>
        <p class="name">{{ name }}</p>
      </div>
    </div>

    <!-- 게시물 갤러리 -->
    <div class="post_gallery">
      <h3>게시물</h3>
      <div class="post_grid">
        <!-- 로딩 상태 -->
        <div v-if="postLoading">게시물 로딩 중...</div>
        <!-- 에러 처리 -->
        <div v-if="postError">{{ postError }}</div>
        <!-- 게시물이 없는 경우 -->
        <div v-if="posts.length === 0 && !postLoading">게시물이 없습니다.</div>
        <!-- 게시물 리스트 -->
        <div v-for="(post, index) in posts" :key="index" class="post">
          <!-- 게시물 이미지 표시 -->
          <div class="post_image_wrapper">
            <img :src="getPostImage(post)" alt="게시물 이미지" class="post_image" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 프로필 상태 변수
const username = ref('') // 사용자 이름
const name = ref('') // 사용자 실제 이름
const profileImage = ref() // 프로필 이미지 (기본 이미지 설정)
const postCount = ref(0) // 게시물 수

// 게시물 상태 변수
const posts = ref([]) // 게시물 리스트
const postLoading = ref(true) // 게시물 로딩 상태
const postError = ref('') // 게시물 로딩 에러 메시지

// JWT 토큰 가져오기
const token = localStorage.getItem('token')

// 프로필 정보 가져오기
onMounted(() => {
  getProfileInfo() // 프로필 정보 가져오기
  getProfileImages() // 게시물 이미지 정보 가져오기
})

// 프로필 정보 API 호출
const getProfileInfo = () => {
  axios
    .get(`/profile/${token}`) // 서버로부터 프로필 데이터를 요청
    .then((res) => {
      const data = res.data
      console.log('응답 데이터:', data) // 데이터 구조 확인
      username.value = data.user_name // 사용자 이름 설정
      name.value = data.name // 사용자 실제 이름 설정
      // 프로필 이미지 경로 수정
      profileImage.value = data.profile_image_url
        ? `/profile/img/${data.profile_image_url}`
        : '/assets/default_profile_image.jpg'
      postCount.value = data.post_count || 0 // 게시물 수 설정
    })
    .catch((error) => {
      console.error('프로필 정보를 가져오는데 실패했습니다.', error)
    })
    .finally(() => {
      postLoading.value = false // 게시물 로딩 완료 상태
    })
}

// 게시물 이미지 정보 API 호출
const getProfileImages = () => {
  axios
    .get(`/profile/img/${token}`) // 게시물 이미지 데이터를 요청
    .then((res) => {
      console.log('서버 응답:', res.data)
      posts.value = res.data
      console.log('게시물 데이터:', posts.value)
    })
    .catch((error) => {
      console.error('게시물 이미지를 가져오는데 실패했습니다.', error)
      postError.value = '게시물 이미지를 가져오는데 실패했습니다.' // 에러 메시지 설정
    })
    .finally(() => {
      postLoading.value = false // 게시물 로딩 완료 상태
    })
}

// 게시물 이미지 경로 처리 함수
const getPostImage = (post) => {
  console.log(post) // 게시물의 전체 구조 확인
  console.log(post.image_url) // image_url 확인

  // post.images가 배열이 아니거나 이미지 URL이 없으면 기본 이미지 반환
  if (!post.image_url) {
    return '/assets/default_profile_image.jpg'
  }

  return `/upload/${post.image_url}` // 게시물 이미지 경로 반환
}

// 파일 변경 처리 (프로필 사진 변경 기능 구현)
const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    const formData = new FormData()
    formData.append('userId', localStorage.getItem('userId')) // 사용자 ID (localStorage에서 가져오기)
    formData.append('profileImage', file)

    // 서버로 이미지 업로드 요청
    axios
      .post('/profile/photo', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then((response) => {
        console.log('프로필 사진 업로드 성공:', response.data)
        // 업로드 성공 후 새로운 이미지 URL을 받아와서 프로필 이미지 갱신
        profileImage.value = `/profile/img/${response.data.imageUrl}` // 경로 변경
      })
      .catch((error) => {
        console.error('프로필 사진 업로드 실패:', error)
      })
  }
}
</script>

<style scoped>
.profile {
  padding: 20px;
  margin-left: 600px;
}

.profile_header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.profile_picture,
.profile_img {
  margin-right: 50px;
  margin-top: 10px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
}

.profile_info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 5px;
}

.username {
  width: 150px;
  font-size: 24px;
  font-weight: bold;
}

.name {
  font-size: 18px;
  color: gray;
  margin-top: 30px;
}

.profile_stats {
  display: flex;
  width: 400px;
  gap: 40px;
  font-size: 18px;
  color: #333;
}

.edit_profile_button {
  padding: 10px;
  width: 140px;
  background-color: #d6d6d6;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 18px;
}

.post_gallery {
  border-top: 1px solid gray;
  margin-top: 120px;
  width: 200px;
  padding-left: 450px;
  padding-right: 35vh;
}

.post_grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 5px;
  margin-left: -450px;
}

.post {
  width: 323px;
  height: 323px;
  border-radius: 5px;
}

.uuu {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.post_image {
  width: 323px;
  height: 323px;
  object-fit: cover;
  transition: opacity 0.3s ease;
}
.post_image_wrapper {
}
</style>
