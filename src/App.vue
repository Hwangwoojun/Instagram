<template>
  <div class="app_container">
    <Side />
  </div>
</template>

<script setup>
import Side from './components/Sideba/side.vue'
</script>

<style scoped>
.app_container {
  display: flex; /* 수평으로 배치 */
  height: 90vh; /* 전체 높이를 100vh로 설정 */
}
</style>
