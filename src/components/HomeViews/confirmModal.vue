<template>
  <div class="over" v-if="visible" @click.self="closeModal">
    <div class="delete_modal">
      <div class="modal_button">
        <button @click="showDeleteConfirmModal">삭제</button>
        <button @click="handleUpdate">수정</button>
        <button @click="closeModal">취소</button>
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

const emit = defineEmits(['close', 'openDeleteConfirm', 'openUpdateModal'])

// 모달 외부 클릭 시 모달을 닫는 함수
const closeModal = () => {
  emit('close')
}

// 삭제 버튼 클릭 시 삭제 확인 모달을 호출하는 함수
const showDeleteConfirmModal = () => {
  emit('openDeleteConfirm')
}

// 수정 버튼 클릭 시 수정 모달을 호출하는 함수
const handleUpdate = () => {
  emit('openUpdateModal')
}

// 스크롤 상태 관리
watchEffect(() => {
  const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth

  // confirmModal이 열렸을 때만 스크롤 막기
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
.over {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.delete_modal {
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  width: 400px;
  height: 180px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal_button {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}

button {
  background: none;
  border: none;
  padding: 10px;
  cursor: pointer;
  font-size: 16px;
}

button:nth-child(1) {
  color: red;
  font-weight: bold;
  width: 46.5vh;
  border-bottom: 1px solid rgb(206, 206, 206);
  padding-bottom: 25px;
  margin-top: -25px;
}

button:nth-child(2) {
  color: gray;
  margin-top: 15px;
  width: 46.5vh;
  padding-bottom: 25px;
  border-bottom: 1px solid rgb(206, 206, 206);
}

button:nth-child(3) {
  color: black;
  margin-top: 15.5px;
  width: 46.5vh;
}
</style>
