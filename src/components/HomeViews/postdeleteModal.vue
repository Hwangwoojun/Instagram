<template>
  <div class="modal" v-if="visible">
    <div class="modal_content">
      <p id="p_1">게시물을 삭제하시겠어요?</p>
      <p id="p_2">이 게시물을 삭제하시겠어요?</p>
      <div class="modal_button">
        <button @click="handleDelete">삭제</button>
        <button @click="$emit('close')">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, watchEffect } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['close', 'confirmDelete'])

// 삭제 확인 버튼 클릭 시 호출되는 함수
const handleDelete = () => {
  emit('confirmDelete') // 실제 삭제 이벤트 발생
  emit('close') // 모달 닫기
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
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  width: 400px; /* 너비 추가 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal_button {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px; /* 버튼 그룹 상단 여백 */
}
#p_1 {
  font-size: 21px;
  margin-top: 13px;
}

#p_2 {
  margin-top: -15px;
  color: gray;
  font-size: 15px;
}

button {
  background: none;
  border: none;
  padding: 10px;
  cursor: pointer;
  font-size: 16px;
}

button:nth-child(1) {
  border-top: 1px solid rgb(206, 206, 206);
  color: red;
  font-weight: bold;
  width: 46.5vh;
  border-bottom: 1px solid rgb(206, 206, 206);
  padding-bottom: 17px;
  padding-top: 15px;
}

button:nth-child(2) {
  color: gray;
  margin-top: 7px;
}
</style>
