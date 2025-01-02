<template>
  <div class="modal" v-if="visible" @click="closeModal">
    <div class="modal_content" @click.stop>
      <span class="close" @click="closeModal">
        <span class="close_button">
          <img src="../../../public/assets/image.png" alt="닫기" class="close_icon" /></span
      ></span>

      <div class="post_container">
        <!-- 게시물 이미지 슬라이더 -->
        <div v-if="imageUrls.length > 0" class="image_slider">
          <div class="slider" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
            <div v-for="(imageUrl, index) in imageUrls" :key="index" class="slide">
              <img
                :src="`/upload/${imageUrl}`"
                alt="게시물 이미지"
                class="post_image"
                @dblclick="DoubleClick"
                @click="setCurrentImage(index)"
              />
            </div>
          </div>
          <!-- 왼쪽 버튼은 현재 인덱스가 0이 아닐 때만 표시 -->
          <div v-if="currentIndex > 0" class="prev" @click="prevImage">
            <img src="../../../public/assets/circle-chevron-left-solid.svg" alt="left" />
          </div>

          <!-- 오른쪽 버튼은 마지막 인덱스가 아닐 때만 표시 -->
          <div v-if="currentIndex < imageUrls.length - 1" class="next" @click="nextImage">
            <img src="../../../public/assets/circle-chevron-right-solid.svg" alt="right" />
          </div>
        </div>

        <!-- heart.gif 애니메이션 - 좋아요 상태에 따라 표시 -->
        <img
          v-if="showHeart"
          src="../../../public/assets/heart-unscreen.gif"
          alt="좋아요 하트 애니메이션"
          class="heart_gif"
        />

        <!-- 댓글 및 댓글 작성 폼 -->
        <div class="comments_section">
          <div class="home_home">
            <h3 class="user_id">{{ userName }}</h3>
          </div>
          <div class="date"></div>
          <!-- 댓글 목록 표시 -->
          <div v-if="content !== null" class="selsel">
            <div class="hoho">
              <h4 class="home_userName">{{ userName }}</h4>
              <!-- 사용자 이름 표시 -->
              <p class="home_content">{{ content }}</p>
            </div>
            <small class="small_small_small">{{ postDate }}</small>
            <div v-for="(comment, index) in comments" :key="index" class="comment_item">
              <strong>{{ comment.user_name }} </strong> {{ comment.content }} <br />
              <small class="small">{{ formatDate(comment.comment_date) }}</small>
            </div>
          </div>
          <div v-else>
            <h2 class="null">{{ message }}</h2>
          </div>
          <!-- 좋아요 아이콘 -->
          <div class="hear_hear">
            <img
              :src="isLiked ? '/assets/heart-solid.svg' : '/assets/heart-regular.svg'"
              alt="좋아요 아이콘"
              class="heart"
              @click="toggleLike"
            />
            <img
              src="../../../public/assets/comment-regular.svg"
              class="heart_heart"
              @click="focusCommentInput"
            />
            <h4 class="good_good">좋아요 {{ likeCount }}개</h4>
            <small class="small_small">{{ postDate }}</small>
          </div>
          <!-- 댓글 작성 폼 -->
          <div class="footer">
            <textarea
              ref="commentInput"
              v-model="newComment"
              placeholder="댓글 달기..."
              class="comment_input"
            ></textarea>
            <p @click="addComment" class="add_comment_button">게시</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  postId: {
    type: String,
    required: true
  },
  imageUrls: {
    type: Array,
    required: true
  },
  userName: {
    type: String,
    required: true
  },
  content: {
    type: String,
    required: true
  },
  postDate: {
    type: String,
    required: true
  },
  selectedImageIndex: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['close']) // 'close' 이벤트 정의

const closeModal = () => {
  emit('close', newComment.value) // 댓글 데이터를 부모 컴포넌트로 전달
  newComment.value = '' // 댓글 입력창 초기화
}

// 댓글 관련 상태 관리
const comments = ref([])
const newComment = ref('')
const message = ref('아직 댓글이 없습니다')
const isLiked = ref(false) // 좋아요 여부 초기화
const likeCount = ref(0) // 좋아요 개수 초기화
const commentInput = ref(null)
const showHeart = ref(false)
const currentIndex = ref(props.selectedImageIndex)

const DoubleClick = () => {
  // 더블클릭에서는 오직 좋아요 추가만 수행
  if (!isLiked.value) {
    toggleLike() // 좋아요 상태 변경
  }

  // heart.gif 표시
  showHeart.value = true
  setTimeout(() => {
    showHeart.value = false // 1초 뒤 heart.gif 숨기기
  }, 1000) // 1초(1000ms) 동안 표시
}

// 댓글 입력창에 포커스 이동하는 함수
const focusCommentInput = () => {
  commentInput.value.focus()
}

// 이전 이미지로 이동하는 함수
const prevImage = () => {
  if (currentIndex.value > 0) {
    currentIndex.value -= 1
  }
}

// 다음 이미지로 이동하는 함수
const nextImage = () => {
  if (currentIndex.value < props.imageUrls.length - 1) {
    currentIndex.value += 1
  }
}

const setCurrentImage = (index) => {
  currentIndex.value = index // 현재 인덱스를 설정
}

const getCookie = (name) => {
  const value = `; ${document.cookie}`
  const parts = value.split(`; ${name}=`)
  if (parts.length === 2) return parts.pop().split(';').shift()
  return null
}

const id = ref(parseInt(getCookie('id'))) // 정수형으로 변환
console.log(id.value) // 사용자 ID 확인

// 댓글을 가져오는 함수
const fetchComments = () => {
  console.log(props.imageUrls)
  console.log(props.userName)
  console.log(props.content)
  console.log(props.postDate)
  console.log(props.selectedImageIndex)
  comments.value = []
  message.value = '댓글을 불러오는 중입니다...'

  axios
    .get(`/comment/${props.postId}`) // postId로 댓글 가져오기
    .then((res) => {
      if (res.data.message) {
        message.value = res.data.message
      } else {
        comments.value = res.data
        message.value = ''
      }
    })
    .catch((error) => {
      console.error('댓글을 가져오는 중 오류 발생:', error)
    })
}

// 좋아요 여부와 좋아요 개수 가져오는 함수
const fetchLikeInfo = () => {
  let userKeyUUID = localStorage.getItem('userKeyUUID')
  const params = {
    post_id: props.postId,
    userKeyUUID: userKeyUUID
  }

  // 좋아요 여부 확인
  axios
    .post('/like/check', params)
    .then((res) => {
      isLiked.value = res.data // 좋아요 여부 저장
      fetchLikeCount() // 좋아요 개수 가져오기

      console.log(isLiked.value)
    })
    .catch((error) => {
      console.error('좋아요 상태 확인 실패:', error)
    })
}

// 좋아요 개수 가져오는 함수
const fetchLikeCount = () => {
  axios
    .get(`/count/${props.postId}`)
    .then((res) => {
      likeCount.value = res.data // 좋아요 개수 저장
      console.log(likeCount.value)
    })
    .catch((error) => {
      console.error('좋아요 개수 조회 실패:', error)
    })
}

// 댓글 추가 함수
const addComment = () => {
  if (newComment.value.trim() === '') {
    alert('댓글 내용을 입력하세요.')
    return
  }

  axios
    .post('/comments', {
      post_id: props.postId, // 선택된 게시물 ID로 댓글 추가
      user_id: id.value,
      content: newComment.value
    })
    .then(() => {
      newComment.value = ''
      fetchComments()
    })
    .catch((error) => {
      console.error('댓글 추가 중 오류 발생:', error)
    })
}

// 시간 포맷팅 함수
const formatDate = (postDate) => {
  const now = new Date()
  const date1 = new Date(postDate)
  const elapsedMSec = now.getTime() - date1.getTime()

  const elapsedSec = elapsedMSec / 1000
  const elapsedMin = elapsedSec / 60
  const elapsedHour = elapsedMin / 60
  const elapsedDay = elapsedHour / 24
  const elapsedWeek = elapsedDay / 7 // 추가: 경과 주 수 계산

  if (elapsedSec < 60) {
    return '방금 전'
  } else if (elapsedMin < 60) {
    return `${Math.floor(elapsedMin)}분 전`
  } else if (elapsedHour < 24) {
    return `${Math.floor(elapsedHour)}시간 전`
  } else if (elapsedDay < 7) {
    // 수정: 7일 미만일 때
    return `${Math.floor(elapsedDay)}일 전`
  } else {
    return `${Math.floor(elapsedWeek)}주 전` // 추가: 주 수 반환
  }
}

// postId가 변경될 때마다 댓글 목록을 새로고침
watch(
  () => props.visible,
  (newValue) => {
    if (newValue) {
      currentIndex.value = props.selectedImageIndex
      fetchComments()
      fetchLikeInfo()
    }
  }
)

// 좋아요 토글 함수
const toggleLike = () => {
  let userKeyUUID = localStorage.getItem('userKeyUUID')
  const params = {
    post_id: props.postId,
    userKeyUUID: userKeyUUID
  }

  axios
    .post('/like/check', params)
    .then((res) => {
      const currentlyLiked = res.data
      if (currentlyLiked) {
        // 좋아요 삭제 요청
        axios
          .delete('/like', { data: params })
          .then(() => {
            isLiked.value = false
            likeCount.value -= 1
          })
          .catch((error) => {
            console.error('좋아요 삭제 실패:', error)
          })
      } else {
        // 좋아요 추가 요청
        axios
          .post('/like', params)
          .then(() => {
            isLiked.value = true
            likeCount.value += 1
          })
          .catch((error) => {
            console.error('좋아요 추가 실패:', error)
          })
      }
    })
    .catch((error) => {
      console.error('좋아요 상태 확인 실패:', error)
    })
}
</script>

<style scoped>
.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}
.modal_content {
  background: white;
  border-radius: 3px;
  width: 1370px;
  height: 920px;
}

.post_container {
  display: flex;
}

.image_section {
  flex: 2;
}

.comments_section {
  flex: 3;
  position: relative;
}

.post_image {
  width: 920px;
  height: 920px;
  object-fit: contain;
  background-color: black;
  border-right: 1px solid rgb(201, 199, 199);
}

.comment_item {
  margin-bottom: 25px;
  margin-left: 20px;
}

.comment_input {
  width: 390px;
  height: 23px;
  padding: 10px;
  resize: none;
  border: none;
  outline: none;
  font-size: 13pt;
  font-weight: bold;
}

.add_comment_button {
  color: #007bff;
  border: none;
  cursor: pointer;
  font-size: 14px;
  margin-top: 12px;
}

.add-comment_button:hover {
  background-color: #0056b3;
}

.close {
  cursor: pointer;
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 24px;
  font-weight: bold;
}
.close_button {
  position: absolute;
  right: 5px;
  cursor: pointer;
}

.close_icon {
  width: 27px;
  height: auto;
}
.user_id {
  position: fixed;
  width: 429px;
  border-bottom: 1px solid rgb(208, 208, 208);
  padding-left: 20px;
  padding-bottom: 20px;
}
.null {
  margin-top: 350px;
  margin-left: 106px;
}
.footer {
  border-top: 1px solid rgb(205, 205, 205);
  position: absolute;
  bottom: 11px;
  display: flex;
  width: 449px;
}
textarea::placeholder {
  font-size: 14px;
  color: rgb(136, 136, 136);
  font-weight: 400;
  padding-top: 4px;
}

.home_userName {
  margin-left: 19px;
}
.home_content {
  margin-left: 10px;
}
.small {
  color: rgb(144, 144, 144);
}
.date {
  margin-left: 18px;
  margin-top: -21px;
  margin-bottom: 20px;
}
.heart {
  width: 27px;
  margin-top: 20px;
}
.hear_hear {
  border-top: 1px solid rgb(210, 210, 210);
  position: absolute;
  bottom: 65px;
  padding-left: 15px;
  width: 434px;
  cursor: pointer;
}

.good_good {
  margin-top: 14px;
  margin-bottom: 1px;
}

.small_small {
  color: rgb(144, 144, 144);
}

.heart_heart {
  width: 27px;
  margin-left: 15px;
}
.selsel {
  display: flex;
  flex-direction: column;
  max-height: 680px;
  overflow-y: auto; /* 댓글 영역에 스크롤 추가 */
  scrollbar-width: none;
}
.hoho {
  display: flex;
  align-items: center;
  margin-top: -7px;
}
.small_small_small {
  margin-left: 18px;
  margin-top: -20px;
  color: rgb(144, 144, 144);
  margin-bottom: 20px;
}
.home_home {
  padding-bottom: 65px;
}
.heart_gif {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-100%, -20%);
  width: 450px; /* 원하는 크기로 설정 */
  opacity: 1;
  z-index: 10; /* 이미지 위에 heart.gif 띄우기 */
}
.image_slider {
  width: 920px;
  position: relative;
  overflow: hidden;
}

.slider {
  display: flex;
  transition: transform 0.3s ease;
}

.slide {
  min-width: 100%;
}

.post_image {
  width: 100%;
}
.prev {
  cursor: pointer;
  width: 30px;
  position: absolute;
  top: 50%;
  left: 2.5%;
  transform: translateY(-50%);
}

.next {
  cursor: pointer;
  width: 30px;
  position: absolute;
  top: 50%;
  right: 2%;
  transform: translateY(-50%);
}
</style>
