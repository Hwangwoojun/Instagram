<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content" @click.stop>
      <h2>{{ title }}</h2>
      <p>{{ message }}</p>
      <button @click="confirmLogin">확인</button>
      <button @click="closeModal">취소</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

// Props 정의
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps({
  title: {
    type: String,
    default: '알림'
  },
  message: {
    type: String,
    default: ''
  },
  isOpen: {
    type: Boolean,
    default: false
  }
})

// 이벤트 정의
const emit = defineEmits(['closeModal', 'confirmLogin'])

// 모달 닫기 함수
const closeModal = () => {
  emit('closeModal') // 부모에게 모달 닫기 이벤트 전송
}

// 확인 버튼 클릭 함수
const confirmLogin = () => {
  emit('confirmLogin') // 부모에게 로그인 확인 이벤트 전송
  closeModal() // 모달 닫기
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
}

button {
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #0095f6;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 10px;
  margin-right: 10px;
}

button:hover {
  background-color: #007bbf;
}
</style>
