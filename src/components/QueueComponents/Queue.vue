<!-- eslint-disable vue/multi-word-component-names -->

<template>
  <div class="queue-container">
    <h1>대기 중... (남은 인원: {{ queueCount }}명)</h1>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const queueCount = ref(0) // 대기열의 초기값은 0으로 설정, 서버에서 값을 가져올 예정
const router = useRouter() // Vue Router 인스턴스
let queueInterval = null // interval을 관리할 변수

// 대기열 상태 조회
const getQueueStatus = () => {
  axios
    .get('/queue/status') // 서버에서 대기열 상태 조회
    .then((res) => {
      queueCount.value = res.data.queueCount // 서버에서 반환된 대기열 수 업데이트
      if (queueCount.value === 0) {
        clearInterval(queueInterval) // 대기열이 0이 되면 interval을 멈추고 처리
        axios
          .get('/process') // 서버의 /process 호출
          .then(() => {
            router.push('/home') // 홈 화면으로 이동
          })
          .catch((error) => {
            console.error('프로세스 진행 중 오류 발생:', error)
          })
      }
    })
    .catch((error) => {
      console.error('대기열 상태 조회 오류:', error)
    })
}

// 컴포넌트가 마운트되었을 때 대기열 상태를 가져오고, 1초 간격으로 대기열 상태 업데이트
onMounted(() => {
  getQueueStatus() // 최초 대기열 상태 가져오기
  queueInterval = setInterval(() => {
    getQueueStatus() // 3초마다 대기열 상태 가져오기
  }, 3000) // 3초 간격으로 처리
})

// 컴포넌트가 언마운트되면 interval 정리
onBeforeUnmount(() => {
  if (queueInterval) {
    clearInterval(queueInterval)
  }
})
</script>

<style scoped>
.queue-container {
  text-align: center;
  margin-top: 50px;
}
</style>
