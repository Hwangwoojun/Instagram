<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="home">
    <div v-for="post in posts" :key="post.post_id" class="post">
      <div class="post_header">
        <h2 class="h2_2">{{ post.user_name }}</h2>
        <div class="wjawja">.</div>
        <div class="post_date">{{ formatDate(post.post_date) }}</div>
      </div>
      <div class="ellipsis">
        <img
          v-if="post.user_name === getCookie('userName')"
          src="../../../public/assets/ellipsis-solid.svg"
          alt="이미지"
          @click="openConfirmModal(post)"
        />
        <span v-else style="display: inline-block; height: 30px"></span>
      </div>
      <div class="post_content">
        <!-- 게시물 이미지 슬라이더 -->
        <div class="image_slider">
          <!-- 이미지 리스트 -->
          <div
            class="image_container"
            :style="{ transform: `translateX(-${post.currentImageIndex * 100}%)` }"
          >
            <img
              v-for="(image, index) in post.images"
              :key="index"
              :src="`/upload/${image}`"
              alt="게시물 이미지"
              class="img_img"
              @dblclick="DoubleClick(post)"
            />
          </div>
          <!-- 이전 이미지 버튼 -->
          <div v-if="post.currentImageIndex > 0" @click="prevImage(post)" class="slider_button">
            <img
              src="../../../public/assets/circle-chevron-left-solid.svg"
              alt="arrow"
              class="arrow_left"
            />
          </div>

          <!-- 다음 이미지 버튼 -->
          <div
            v-if="post.currentImageIndex < post.images.length - 1"
            @click="nextImage(post)"
            class="slider_button"
          >
            <img
              src="../../../public/assets/circle-chevron-right-solid.svg"
              alt="arrow"
              class="arrow_right"
            />
          </div>
        </div>

        <!-- heart.gif 이미지 - 상태에 따라 표시 -->
        <img
          v-if="post.showHeart"
          src="../../../public/assets/heart-unscreen.gif"
          alt="좋아요 하트 애니메이션"
          class="heart_gif"
        />

        <!-- 좋아요 아이콘 -->
        <img
          :src="post.isLiked ? '/assets/heart-solid.svg' : '/assets/heart-regular.svg'"
          alt="좋아요 아이콘"
          class="heart"
          @click="toggleLike(post)"
        />
        <img
          src="../../../public/assets/comment-regular.svg"
          alt="댓글 아이콘"
          class="comment"
          @click="
            openCommentModal(
              post.post_id,
              post.images,
              post.user_name,
              post.content,
              formatDate(post.post_date),
              post.currentImageIndex
            )
          "
        />
        <h4 class="good">좋아요 {{ post.likeCount }}개</h4>

        <div class="footer">
          <h4>{{ post.user_name }}</h4>
          <p class="p_1">{{ post.content }}</p>
        </div>
        <small
          class="small"
          @click="
            openCommentModal(
              post.post_id,
              post.images,
              post.user_name,
              post.content,
              formatDate(post.post_date),
              post.currentImageIndex
            )
          "
          >댓글 {{ post.commentCount }}개 모두 보기</small
        >
      </div>
      <div class="comment_comment">
        <textarea
          v-model="post.newComment"
          placeholder="댓글 달기..."
          class="comment_input"
        ></textarea>
        <p @click="addComment(post)" class="add_comment_button">게시</p>
      </div>
      <hr class="post_divider" />
    </div>

    <!-- 더 이상 게시물이 없을 때 표시할 메시지 -->
    <div v-if="noMorePosts" class="no_more_posts">
      <img src="../../../public/assets/check.png" class="check" />
      <p>모두 확인했습니다.</p>
    </div>

    <div ref="loadTrigger" class="load_trigger"></div>
  </div>
  <!-- 댓글 모달 추가 -->
  <CommentModal
    :visible="isCommentModalVisible"
    :postId="selectedPostId"
    @close="handleModalClose"
    :imageUrls="selectedImageUrls"
    :selectedImageIndex="selectedImageIndex"
    :userName="selectedUserName"
    :content="selectedContent"
    :postDate="selectedPostDate"
  />

  <ConfirmModal
    :visible="isConfirmModalVisible"
    @close="closeConfirmModal"
    @openDeleteConfirm="openDeleteConfirmModal"
    @openUpdateModal="openUpdateModal"
  />

  <UpdateModal
    v-if="isUpdateModalVisible"
    :visible="isUpdateModalVisible"
    :post="selectedPost"
    :images="selectedPost.images"
    :user_name="selectedPost.user_name"
    @close="closeUpdateModal"
    @updateSuccess="updatePost"
  />

  <postdeleteModal
    :visible="ispostDeleteModalVisible"
    @close="closeDeleteConfirmModal"
    @confirmDelete="deletePost"
  />
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watchEffect, watch } from 'vue'
import axios from 'axios'
import CommentModal from '../CommentComponent/commentModal.vue'
import ConfirmModal from './confirmModal.vue'
import UpdateModal from './update.vue'
import postdeleteModal from './postdeleteModal.vue'

// 게시물 관련 상태 관리
const posts = ref([]) // 게시물 목록
const loading = ref(false) // 로딩 상태
const page = ref(0) // 페이지 번호
const isCommentModalVisible = ref(false) // 댓글 모달 가시성 상태
const selectedPostId = ref(null) // 선택된 게시물 ID
const selectedImageUrls = ref(null) // 선택된 이미지 URL
const selectedUserName = ref('') // 선택된 사용자 이름
const selectedContent = ref('') // 선택된 게시물 내용
const selectedPostDate = ref('')
const noMorePosts = ref(false) // 더 이상 게시물이 없는 상태
const selectedImageIndex = ref(null) // 현재 이미지 배열에 있는 이미지 순서 인덱스 번호
const isConfirmModalVisible = ref(false) // 삭제 확인 모달 가시성 상태
const isUpdateModalVisible = ref(false) // 수정 모달 가시성 상태
const selectedPost = ref(null) // 선택된 게시물
const ispostDeleteModalVisible = ref(false) // 삭제 확인 모달 상태

const handleModalClose = (newComment) => {
  const post = posts.value.find((p) => p.post_id === selectedPostId.value) // selectedPostId.value로 수정
  if (post && newComment) {
    post.newComment = newComment // 모달에서 받은 댓글을 해당 게시물의 댓글 입력창에 반영
  }
  isCommentModalVisible.value = false
}

const openConfirmModal = (post) => {
  selectedPost.value = post // 선택된 게시물 저장
  isConfirmModalVisible.value = true // 확인 모달 열기
}

const closeConfirmModal = () => {
  isConfirmModalVisible.value = false // 확인 모달 닫기
}

const openUpdateModal = () => {
  selectedPost.value = { ...selectedPost.value } // 기존 게시물의 데이터를 복사
  isUpdateModalVisible.value = true // 수정 모달 열기
  closeConfirmModal() // 확인 모달 닫기
}

const closeUpdateModal = () => {
  isUpdateModalVisible.value = false // 수정 모달 닫기
  document.body.style.overflow = '' // 스크롤 복구
  document.body.style.paddingRight = '' // 오른쪽 패딩 초기화
}

const openDeleteConfirmModal = () => {
  ispostDeleteModalVisible.value = true // 삭제 확인 모달 열기
  closeConfirmModal() // 기존의 확인 모달 닫기
}

const closeDeleteConfirmModal = () => {
  ispostDeleteModalVisible.value = false // 삭제 확인 모달 닫기
  document.body.style.overflow = '' // 스크롤 복구
  document.body.style.paddingRight = '' // 오른쪽 패딩 초기화
}

const deletePost = () => {
  axios
    .delete(`/posts/${selectedPost.value.post_id}`)
    .then(() => {
      posts.value = posts.value.filter((p) => p.post_id !== selectedPost.value.post_id)
      closeConfirmModal()
      console.log('게시물이 삭제되었습니다.')
    })
    .catch((error) => {
      console.error('게시물 삭제 실패:', error)
    })
}

const getCookie = (name) => {
  const value = `; ${document.cookie}`
  const parts = value.split(`; ${name}=`)
  if (parts.length === 2) return parts.pop().split(';').shift()
  return null
}

const id = ref(parseInt(getCookie('id'))) // 정수형으로 변환
console.log(id.value) // 사용자 ID 확인

// 게시물을 가져오는 함수
const fetchPosts = () => {
  if (loading.value) return // 로딩 중이면 함수 종료
  loading.value = true // 로딩 상태 설정

  axios
    .post('/posts', { page: page.value })
    .then((res) => {
      if (res.data.posts && res.data.posts.length > 0) {
        // 새로운 게시물 추가
        const newPosts = res.data.posts.map((post) => ({
          ...post,
          showHeart: false, // 초기값 설정
          newComment: '', // 댓글 입력 필드 초기값
          isLiked: false, // 좋아요 초기값 설정
          currentImageIndex: 0, // 추가: 현재 이미지 인덱스 초기화
          commentCount: 0 // 초기 댓글 수 설정
        }))

        // 기존 게시물에 새로운 게시물을 합쳐 posts 업데이트
        posts.value = [...posts.value, ...newPosts]

        fetchAllCommentCounts()

        fetchLikeStatus() // 좋아요 상태를 서버에서 로드
      } else {
        // 더 이상 게시물이 없는 경우
        noMorePosts.value = true // 더 이상 게시물이 없다는 상태 업데이트
        window.removeEventListener('scroll', handleScroll) // 이벤트 리스너 해제
      }
    })
    .catch((error) => {
      console.error('게시물 가져오기 실패:', error)
    })
    .finally(() => {
      loading.value = false // 로딩 상태 해제
    })
}

// 모든 게시물의 댓글 수를 불러오는 함수
const fetchAllCommentCounts = () => {
  posts.value.forEach((post) => {
    axios
      .get(`/post/count/${post.post_id}`)
      .then((res) => {
        post.commentCount = res.data.count // 댓글 수를 게시물에 추가
      })
      .catch((error) => {
        console.error('댓글 수 로드 실패:', error)
      })
  })
}

// 서버에서 좋아요 상태를 로드하는 함수
const fetchLikeStatus = () => {
  let userKeyUUID = localStorage.getItem('userKeyUUID')
  posts.value.forEach((post) => {
    const params = {
      post_id: post.post_id,
      userKeyUUID: userKeyUUID
    }

    axios
      .post('/like/check', params)
      .then((res) => {
        post.isLiked = res.data // 서버에서 반환된 좋아요 상태로 설정
      })
      .catch((error) => {
        console.error('좋아요 상태 로드 실패:', error)
      })
  })
}

// 스크롤 이벤트에 따라 게시물 가져오기
const handleScroll = () => {
  const scrollHeight = document.documentElement.scrollHeight
  const scrollTop = window.scrollY
  const clientHeight = window.innerHeight

  // 스크롤이 바닥에 거의 도달하면 페이지 증가 및 게시물 요청
  if (scrollTop + clientHeight >= scrollHeight - 600 && !loading.value) {
    page.value += 1 // 페이지 증가
    fetchPosts() // 게시물 가져오기
  }
}

// 페이지가 로드될 때 게시물 가져오기 및 이벤트 리스너 설정
onMounted(() => {
  fetchPosts() // 게시물 가져오기
  window.addEventListener('scroll', handleScroll) // 스크롤 이벤트 리스너 추가
})

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 게시일을 포맷팅하는 함수
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

const toggleLike = (post) => {
  // 로컬스토리지에서 userKeyUUID를 가져옵니다.
  let userKeyUUID = localStorage.getItem('userKeyUUID')

  const params = {
    post_id: post.post_id, // 게시물 ID
    userKeyUUID: userKeyUUID // 사용자 고유 키 UUID
  }

  // 먼저 좋아요 상태를 확인합니다.
  axios
    .post('/like/check', params)
    .then((res) => {
      const currentlyLiked = res.data

      if (currentlyLiked) {
        // 좋아요가 눌린 상태에서 삭제 요청
        axios
          .delete('/like', { data: params })
          .then(() => {
            post.isLiked = false // 좋아요 상태 업데이트
            post.likeCount -= 1 // 좋아요 수 감소
            console.log('좋아요가 삭제되었습니다.')
          })
          .catch((error) => {
            console.error('좋아요 삭제 실패:', error)
          })
      } else {
        // 좋아요가 눌리지 않은 상태에서 추가 요청
        axios
          .post('/like', params)
          .then(() => {
            post.isLiked = true // 좋아요 상태 업데이트
            post.likeCount += 1 // 좋아요 수 증가
            console.log('좋아요가 추가되었습니다.')
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

// 더블클릭 이벤트 핸들러
const DoubleClick = (post) => {
  // 더블클릭에서는 오직 좋아요 추가만 수행
  if (!post.isLiked) {
    toggleLike(post) // 좋아요 상태 변경
  }

  // heart.gif 표시
  post.showHeart = true
  setTimeout(() => {
    post.showHeart = false // 1초 뒤 heart.gif 숨기기
  }, 1000) // 1초(1000ms) 동안 표시
}

// 댓글 모달 열기

const openCommentModal = (postId, imageUrls, userName, content, postDate, imageIndex) => {
  selectedImageUrls.value = imageUrls // 선택된 이미지 URL 배열 설정
  selectedPostId.value = postId // 선택된 게시물 ID 설정
  selectedImageIndex.value = imageIndex // 이미지 인덱스 설정
  isCommentModalVisible.value = true // 댓글 모달 열기
  selectedUserName.value = userName // 선택된 사용자 이름 설정
  selectedContent.value = content // 선택된 게시물 내용 설정
  selectedPostDate.value = postDate // post_date 추가

  console.log(selectedImageIndex.value + '이건 인덱스다')
}

const addComment = (post) => {
  if (post.newComment.trim() === '') {
    alert('댓글 내용을 입력하세요.')
    return
  }

  axios
    .post('/comments', {
      post_id: post.post_id, // 선택된 게시물 ID`
      user_id: id.value, // 사용자 ID
      content: post.newComment // 댓글 내용
    })
    .then(() => {
      post.newComment = '' // 입력 필드 초기화
    })
    .catch((error) => {
      console.error('댓글 추가 중 오류 발생:', error)
    })
}

// 이미지 슬라이더 초기화
const initializeSlider = (post) => {
  post.currentImageIndex = 0 // 현재 이미지 인덱스 초기
}

// 이전 이미지 보기
const prevImage = (post) => {
  if (post.currentImageIndex > 0) post.currentImageIndex -= 1
}

// 다음 이미지 보기
const nextImage = (post) => {
  if (post.currentImageIndex < post.images.length - 1) post.currentImageIndex += 1
}

// 스크롤 상태 관리
watchEffect(() => {
  const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth
  if (isCommentModalVisible.value) {
    document.body.style.overflow = 'hidden'
    document.body.style.paddingRight = `${scrollbarWidth}px` // 수정된 부분
  } else {
    document.body.style.overflow = ''
    document.body.style.paddingRight = ''
  }
})

// 게시물 데이터가 로드될 때 슬라이더 초기화
watch(posts, (newPosts) => {
  newPosts.forEach((post) => {
    initializeSlider(post) // 각 게시물에 대해 슬라이더 초기화
  })
})
</script>

<style scoped>
.home {
  width: 600px;
  margin-left: 750px;
  padding: 20px;
}

.post {
  margin-bottom: 20px;
  background-color: white;
  border: 1px solid #ffffff;
  border-radius: 10px;
}

.post_header {
  margin-bottom: -50px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #ffffff;
  align-items: center;
  justify-items: start;
  margin-left: -19px;
}

.post_header h2 {
  font-size: 18px;
  font-weight: bold;
}

.post_date {
  font-size: 14px;
  color: #8b8989;
  grid-column: 3;
  margin-left: 5px;
  margin-top: 1.5px;
}
.wjawja {
  font-size: 30px;
  margin-top: -18px;
  color: #767676;
  grid-column: 2;
  margin-left: 5px;
}
.ellipsis {
  cursor: pointer;
  width: 27px;
  grid-column: 6;
  margin-left: 550px;
  margin-bottom: -10px;
}
.comment {
  width: 27px;
  margin-top: 10px;
  margin-left: 20px;
  cursor: pointer;
}
.heart {
  width: 27px;
  margin-top: 10px;
}
.good {
  margin-left: -13px;
  margin-top: 8px;
  margin-bottom: -10px;
}
.post_content {
  border-radius: 3px;
  margin-top: 10px;
}
.img_img {
  width: 590px;
  height: 590px;
  object-fit: contain;
  background-color: black;
  flex-shrink: 0;
}
.footer {
  margin-left: -12px;
  margin-bottom: -10px;
  display: flex;
  align-items: center;
}
.p_1 {
  margin-left: 5px;
}
.post_divider {
  border: 0;
  height: 1px;
  background-color: #dbdbdb;
  width: 595px;
  margin-left: -13px;
}

.load_more {
  display: block;
  margin: 20px auto;
  padding: 10px 20px;
  background-color: #0095f6;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.load-more:hover {
  background-color: #007bbf;
}
.post_content {
  position: relative; /* heart.gif를 게시물 이미지 위에 띄우기 위해 설정 */
}

.heart_gif {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 450px;
  opacity: 1;
  z-index: 10; /* 이미지 위에 heart.gif 띄우기 */
}

.heart {
  margin-left: -13px;
  cursor: pointer;
}
.comment_comment {
  margin-left: -13px;
  margin-top: 10px;
  display: flex;
  margin-bottom: -7px;
}
textarea {
  margin-left: -4px;
  resize: none;
  border: none;
  outline: none;
  width: 545px;
  margin-right: 10px;
  font-size: 12pt;
  color: black;
  font-weight: 500;
}
textarea::placeholder {
  color: #8e8e8e;
}
.add_comment_button {
  color: #007bff;
  border: none;
  cursor: pointer;
  font-size: 14px;
  margin-top: 12px;
}
.small {
  margin-left: -14px;
  font-size: 15px;
  color: gray;
  cursor: pointer;
}
.no_more_posts {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 30px 0;
  font-size: 26px;
  color: #000000;
}
.check {
  width: 250px;
}
.image_slider {
  margin-left: -12px;
  background-color: black;
  object-fit: contain;
  position: relative;
  width: 590px;
  overflow: hidden;
}
.image_container {
  display: flex;
  transition: transform 0.3s ease;
}
.slider_button {
  background: transparent;
  cursor: pointer;
}
.arrow_left {
  position: absolute;
  transform: translateY(-1000%);
  left: 3.1%;
  width: 30px;
}

.arrow_right {
  position: absolute;
  transform: translateY(-1000%);
  right: 3.3%;
  width: 30px;
}
</style>
