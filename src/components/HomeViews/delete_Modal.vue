<template>
  <div class="over" v-if="isOpen">
    <div class="delete_modal">
      <p id="p_1">게시물을 삭제하시겠어요?</p>
      <p id="p_2">지금 나가면 수정 내용이 저장되지 않습니다.</p>
      <div class="modal_button">
        <button @click="confirm_delete">삭제</button>
        <button @click="cancel_delete">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps({
  isOpen: Boolean // 모달 열림 여부
})

const emit = defineEmits(['confirm', 'cancel'])

const confirm_delete = () => {
  emit('confirm')
}

const cancel_delete = () => {
  emit('cancel')
}
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
#p_1 {
  font-size: 21px;
  margin-top: 13px;
}
#p_2 {
  margin-top: -15px;
  color: gray;
  font-size: 15px;
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
