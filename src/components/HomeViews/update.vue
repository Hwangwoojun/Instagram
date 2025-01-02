<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="modal" v-if="visible" @click.self="closeModal">
    <div class="modal_content">
      <div class="button_group">
        <button @click="closeModal" class="butt_1">취소</button>
        <p class="p_7">정보 수정</p>
        <button @click="handleUpdate" class="butt_2">완료</button>
      </div>
      <h2 class="l_1">{{ user_name }}</h2>
      <div class="image_gallery">
        <img
          :src="`/upload/${localImages[currentIndex]}`"
          alt="게시물 이미지"
          class="slider_image"
        />
        <div class="prev" v-if="currentIndex > 0">
          <img
            src="../../../public/assets/circle-chevron-left-solid.svg"
            alt="left"
            @click="prevImage"
          />
        </div>
        <div class="next" v-if="currentIndex < localImages.length - 1">
          <img
            src="../../../public/assets/circle-chevron-right-solid.svg"
            alt="right"
            @click="nextImage"
          />
        </div>
        <textarea
          class="tex"
          v-model="updatedContent"
          placeholder="수정할 내용을 입력하세요..."
          rows="5"
        ></textarea>
      </div>
      <p class="cscs">{{ currentIndex + 1 }} / {{ localImages.length }}</p>
      <img
        src="../../../public/assets/trash-can-solid.svg"
        alt="trash"
        class="trash"
        @click="removeImage(currentIndex)"
        v-if="localImages.length > 2"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watchEffect } from 'vue'
import axios from 'axios'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  post: {
    type: Object,
    required: true
  },
  images: {
    type: Array,
    required: true
  },
  user_name: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['close', 'updateSuccess'])

// 부모에서 받은 이미지 배열을 복사하여 사용
const localImages = ref([...props.images])

// 기존 게시물 내용을 가져와서 수정
const updatedContent = ref(props.post.content)

// 현재 이미지 인덱스
const currentIndex = ref(0)

// 모달 외부 클릭 시 모달을 닫는 함수
const closeModal = () => {
  emit('close')
}

// 이미지 제거 함수
const removeImage = (index) => {
  if (index >= 0 && index < localImages.value.length) {
    localImages.value.splice(index, 1) // 배열에서 이미지 제거
    // 현재 인덱스가 마지막 이미지를 넘어갈 경우 인덱스를 조정
    if (currentIndex.value >= localImages.value.length) {
      currentIndex.value = localImages.value.length - 1
    }
  }
}

// 수정 버튼 클릭 시 수정 완료 이벤트 발생
const handleUpdate = () => {
  const updatedPost = {
    content: updatedContent.value,
    images: localImages.value // 수정된 이미지 정보 포함
  }

  // API 요청
  axios
    .put(`/posts/${props.post.post_id}`, updatedPost, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}` // 토큰 추가
      }
    })
    .then((res) => {
      emit('updateSuccess', res.data) // 성공 시 업데이트 성공 이벤트 발생
      closeModal() // 모달 닫기
    })
    .catch((error) => {
      console.error('업데이트 중 오류 발생:', error)
      // 오류 처리 로직 추가 가능
    })
}

// 이전 이미지로 이동
const prevImage = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

// 다음 이미지로 이동
const nextImage = () => {
  if (currentIndex.value < props.images.length - 1) {
    currentIndex.value++
  }
}

// 스크롤 상태 관리
watchEffect(() => {
  const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth

  // updateModal이 열렸을 때만 스크롤 막기
  if (props.visible) {
    document.body.style.overflow = 'hidden' // 스크롤 막기
    document.body.style.paddingRight = `${scrollbarWidth}px` // 오른쪽 패딩 설정
  } else {
    document.body.style.overflow = '' // 스크롤 복구
    document.body.style.paddingRight = '' // 오른쪽 패딩 초기화
  }
})
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal_content {
  width: 1000px;
  height: 860px;
  background: white;
  padding-left: 20px;
  border-radius: 4px;
}

.button_group {
  padding-top: 4px;
  margin-top: 5px;
  padding-bottom: 5px;
  display: flex;
  border-bottom: 1px solid rgb(187, 187, 187);
  width: 1020px;
  margin-left: -20px;
  justify-content: space-between;
}

.image_gallery {
  display: flex;
}

.slider_image {
  margin-top: -43px;
  margin-left: -20px;
  object-fit: contain;
  background-color: rgb(222, 222, 222);
  width: 792px;
  height: 792px;
  flex-shrink: 0;
}

.slider_controls {
  margin-top: 10px;
}

.prev {
  cursor: pointer;
  width: 30px;
  position: absolute;
  top: 50%;
  left: 24.1%;
  transform: translateY(70%);
}

.next {
  cursor: pointer;
  width: 30px;
  position: absolute;
  top: 50%;
  right: 35.9%;
  transform: translateY(70%);
}
.cscs {
  display: flex;
  justify-content: flex-end;
  margin-right: 88px;
  margin-top: -40px;
}
.butt_1 {
  cursor: pointer;
  font-size: 16px;
  border: none;
  background: none;
  color: #2d91fd;
  width: 50px;
  height: 50px;
}
.butt_2 {
  cursor: pointer;
  font-size: 16px;
  border: none;
  background: none;
  color: #2d91fd;
  width: 50px;
  height: 50px;
}
.p_7 {
  font-size: 16px;
}
.tex {
  resize: none;
  border-top: none;
  border-left: none;
  border-right: none;
  height: 200px;
  border-bottom: 1px solid rgb(178, 178, 178);
  width: 500px;
  font-size: 15pt;
  outline: none;
}
.l_1 {
  margin-left: 780px;
  margin-top: 5px;
  font-size: 18px;
  font-weight: bold;
}
.trash {
  display: flex;
  justify-content: flex-end;
  margin-top: -150px;
  margin-left: 870px;
  width: 35px;
  height: 35px;
}
</style>
