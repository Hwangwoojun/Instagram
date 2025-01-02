<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="chat">
    <!-- 왼쪽 사용자 목록 -->
    <div class="chat_left">
      <!-- 로그인에 성공한 유저에 사용자 이름을 가져와 표시 하는곳 -->
      <div class="user_info">
        <img class="user_avatar" src="../../../public/assets/유메노아카링.png" alt="user_avatar" />
        <div class="user_name">dnwns06</div>
        <button class="memo_button">메모</button>
      </div>

      <!-- 메시지 가능한 사용자 목록 -->
      <div class="user_list">
        <div class="user_item" v-for="user in users" :key="user.id" @click="selectUser(user)">
          <img class="user_avatar" :src="user.avatar" alt="user_avatar" />
          <div class="user_details">
            <div class="user_name">{{ user.name }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 오른쪽 메시지 창 -->
    <div class="chat_message">
      <div class="message_area">
        <div class="message" v-for="(msg, index) in selectedUser.messages" :key="index">
          <div :class="['message_text', { sent: msg.sentByUser }]">{{ msg.text }}</div>
          <div class="message_time">{{ msg.time }}</div>
        </div>
      </div>

      <!-- 메시지 입력 -->
      <div class="message_input">
        <input
          type="text"
          v-model="newMessage"
          placeholder="메시지 입력..."
          @keyup.enter="sendMessage"
        />
        <button @click="sendMessage">전송</button>
      </div>
    </div>
  </div>
</template>

<script setup>
// 테스트 겸 임의로 사용자 생성
// 30명의 사용자 이름을 "이형헌"으로 설정
const users = Array.from({ length: 30 }, (_, i) => ({
  id: i + 1, // 사용자 ID
  name: '이형헌', // 사용자 이름
  avatar: '/assets/aspas.webp', // 프로필 이미지
  messages: [] // 초기 메시지 비어 있음
}))

let selectedUser = users[0] // 기본 선택된 사용자
let newMessage = '' // 새로운 메시지 초기화

// 사용자 선택
function selectUser(user) {
  selectedUser = user
}

// 메시지 전송
function sendMessage() {
  if (newMessage.trim()) {
    selectedUser.messages.push({
      text: newMessage,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      sentByUser: true
    })
    newMessage = ''
  }
}
</script>

<style scoped>
.chat {
  display: flex;
  height: 99vh;
  width: 98.3vw;
}

/* 왼쪽 사용자 목록 */
.chat_left {
  margin-left: 295px;
  width: 17%;
  border-right: 1px solid #ddd;
  overflow-y: auto;
  background-color: #f8f8f8;
  margin-top: -8px;
  display: flex;
  flex-direction: column;
}

.user_info {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #fff;
  border-bottom: 1px solid #ddd;
  flex-shrink: 0;
}

.user_avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 10px;
}

.user_name {
  font-weight: bold;
}

.memo_button {
  margin-left: auto;
  background: none;
  border: none;
  color: #007aff;
  cursor: pointer;
}

.user_list {
  padding: 10px;
  overflow-y: auto;
  flex-grow: 1;
}

.user_item {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.user_item:hover {
  background-color: #eaeaea;
}

.user_details {
  margin-left: 10px;
}

.last_active {
  font-size: 12px;
  color: #888;
}

/* 오른쪽 메시지 창 */
.chat_message {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.message_area {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
}

.message {
  margin: 10px 0;
}

.message_text {
  max-width: 70%;
  padding: 10px;
  border-radius: 10px;
  font-size: 14px;
}

.message_text.sent {
  background-color: #007aff;
  color: white;
  align-self: flex-end;
}

.message_text {
  background-color: #f1f1f1;
  color: black;
}

.message_input {
  outline: none;
  display: flex;
  padding: 10px;
  border-top: 1px solid #ffffff;
}

.message_input input {
  font-size: 15pt;
  outline: none;
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 10px;
}

.message_input button {
  margin-left: 10px;
  padding: 10px 20px;
  background-color: #007aff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
