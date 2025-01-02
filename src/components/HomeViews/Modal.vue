<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="over" v-if="isOpen" @click.self="over_Click">
    <div :class="['modal_content', { upload_active: upload }]">
      <span class="close_button" @click="over_Click">
        <img src="../../../public/assets/image.png" alt="닫기" class="close_icon" />
      </span>

      <!-- 이미지가 선택되지 않았을 때 보여줄 부분 -->
      <div v-show="!imageSrc.length" class="show" @dragover.prevent @drop="Drop">
        <p>새 게시물 만들기</p>
        <img src="/public/assets/pp.png" alt="기본 이미지" class="center_image" />
        <input type="file" @change="File_Change" id="fileInput" style="display: none" multiple />
        <label for="fileInput" class="custom_button">컴퓨터에서 선택</label>
      </div>

      <!-- 이미지가 선택된 후 보여줄 부분 -->
      <div v-show="imageSrc.length && !upload" class="show_2">
        <div class="toolbar">
          <span class="back_arrow" @click="arrow_img_delete_Modal">
            <img
              src="../../../public/assets/arrow-left-solid.svg"
              alt="뒤로 가기"
              class="arrow_image"
            />
          </span>
          <span class="cut">자르기</span>
          <span class="next_button" @click="go_Upload">다음</span>
        </div>

        <!-- 슬라이더 부분 -->
        <div class="image_slider" @click="close_Modal">
          <img
            v-if="currentIndex > 0"
            src="../../../public/assets/circle-chevron-left-solid.svg"
            alt="이전"
            class="arrow left_arrow"
            @click="prevImage"
          />

          <img :src="imageSrc[currentIndex]" alt="선택된 이미지" class="center_image_2" />

          <img
            v-if="currentIndex < imageSrc.length - 1"
            src="../../../public/assets/circle-chevron-right-solid.svg"
            alt="다음"
            class="arrow right_arrow"
            @click="nextImage"
          />
        </div>

        <div class="clone" :class="{ active: isActive }" @click="togg_Modal">
          <!-- 카피 이미지 클릭 시 썸네일 모달 창 뜸 -->
          <img src="../../../public/assets/clone-regular.svg" alt="카피" />
        </div>

        <!-- 썸네일 모달 창 -->
        <div v-if="show_Modal" class="thumbnail_modal_wrapper" @click="close_Modal">
          <div class="thumbnail_modal" @click.stop>
            <div class="thumbnail_content">
              <div class="thumbnail_scroll">
                <div
                  v-for="(src, index) in imageSrc.slice(0, 15)"
                  :key="index"
                  class="thumbnail_container"
                  draggable="true"
                  @dragstart="dragStart(index)"
                  @dragover.prevent
                  @drop="drop(index)"
                >
                  <!-- 썸네일 이미지 -->
                  <img
                    :src="src"
                    alt="썸네일"
                    :class="['thumbnail', { active: currentIndex === index }]"
                    @click="setCurrentImage(index)"
                  />
                  <!-- 모든 이미지에 X 표시 (x 표시 이미지 누르면 그 이미지는 삭제 됨) -->
                  <span
                    v-if="currentIndex === index"
                    class="delete_icon"
                    @click.stop="removeImage(index)"
                  >
                    <img
                      src="../../../public/assets/image.png"
                      alt="삭제"
                      class="delete_icon_image"
                    />
                  </span>
                </div>
              </div>
              <div class="Modal_plus" @click="openFileDialog" multiple>
                <img src="../../../public/assets/plus-solid.svg" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 업로드 상태일 때 보여줄 부분 -->
      <div v-show="upload" class="show_3">
        <div :class="['toolbar', { upload_toolbar: upload }]">
          <span class="back_arrow" @click="to_back">
            <img src="/public/assets/arrow-left-solid.svg" alt="뒤로 가기" class="arrow_image" />
          </span>
          <span class="cut">새 게시물 만들기</span>
          <span class="next_button" @click="upload_Click">업로드</span>
        </div>
        <h4 class="user_name">{{ userName }}</h4>
        <textarea class="Content_Content" type="text" v-model="postContent"></textarea>
        <!-- 업로드 상태에서 이미지 슬라이더 -->
        <div class="image_slider">
          <img
            v-if="currentIndex > 0"
            src="../../../public/assets/circle-chevron-left-solid.svg"
            alt="이전"
            :class="['arrow left_arrow', { upload_left_arrow: upload }]"
            @click="prevImage"
          />

          <img :src="imageSrc[currentIndex]" alt="선택된 이미지" class="center_image_3" />

          <img
            v-if="currentIndex < imageSrc.length - 1"
            src="../../../public/assets/circle-chevron-right-solid.svg"
            alt="다음"
            :class="['arrow right_arrow', { upload_right_arrow: upload }]"
            @click="nextImage"
          />
        </div>
      </div>

      <slot></slot>
    </div>

    <!-- 삭제 확인 모달 -->
    <Delete_Modal :isOpen="delete_Modal" @confirm="Delete" @cancel="cancel_delete" />

    <arrowimgdeleteModal
      :isOpen="arrow_Delete_Modal_img"
      @confirm="img_delete"
      @cancel="cancel_img_delete"
    />
  </div>
</template>

<script setup>
import { ref, defineEmits, defineProps } from 'vue'
import axios from 'axios'
import Delete_Modal from './delete_Modal.vue'
import arrowimgdeleteModal from './arrow_img_delete_Modal.vue'
import { useRouter } from 'vue-router'

// 쿠키에서 userName 값을 불러오는 함수
const getCookie = (name) => {
  const value = `; ${document.cookie}`
  const parts = value.split(`; ${name}=`)
  if (parts.length === 2) return parts.pop().split(';').shift()
  return null
}

const userName = ref(getCookie('userName'))

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps({
  isOpen: Boolean
})

const emit = defineEmits(['close'])
const router = useRouter()

const close = () => {
  emit('close')
}

// 이미지 미리보기를 위한 src 데이터
const imageSrc = ref([])

// 현재 표시 중인 이미지의 인덱스
const currentIndex = ref(0)

// 업로드 상태
const upload = ref(false)

// 삭제 확인 모달 상태
const delete_Modal = ref(false)

// 뒤로가기 이미지 삭제 확인 모달 상태
const arrow_Delete_Modal_img = ref(false)

// 선택한 파일을 저장하기 위한 변수
const selectedFiles = ref([])

// 게시물 내용을 저장하기 위한 변수
const postContent = ref('')

// 썸네일 모달 표시 여부
const show_Modal = ref(false)

// 클릭 상태 저장
const isActive = ref(false)

// 드래그 시작 시 이미지 인덱스 저장
const selectedImageIndex = ref(null)

// 썸네일 모달 열고 닫기 함수
const togg_Modal = () => {
  show_Modal.value = !show_Modal.value // 모달 상태 토글
  isActive.value = show_Modal.value // 배경색 변경
}

// 썸네일 모달 열고 닫기 함수
const close_Modal = () => {
  show_Modal.value = false
  isActive.value = false // 모달이 닫히면 배경색을 초기화
}

// 파일 선택 다이얼로그를 열기 위한 함수
const openFileDialog = () => {
  const fileInput = document.getElementById('fileInput')
  if (fileInput) {
    fileInput.click() // 파일 선택 다이얼로그 열기
  }
}

// 파일 선택 시 처리
const File_Change = (event) => {
  const files = Array.from(event.target.files)
  if (files.length > 0) {
    // 기존 이미지 배열과 선택한 파일 배열을 초기화하지 않고 유지
    files.forEach((file) => view_Image(file))
    selectedFiles.value.push(...files)
    event.target.value = '' // 같은 파일 재선택 가능하게

    if (!imageSrc.value.length) {
      currentIndex.value = 0 // 현재 인덱스를 0으로 설정하여 첫 번째 이미지로 초기화
    } else {
      currentIndex.value
    }
    // 파일 input의 value 값을 초기화
  }
}

// 드래그 앤 드롭으로 파일을 놓았을 때 처리 함수
const Drop = (event) => {
  event.preventDefault()
  const files = Array.from(event.dataTransfer.files)
  if (files.length > 0) {
    files.forEach((file) => view_Image(file))
    // 기존 선택한 파일과 새로운 파일 합치기
    selectedFiles.value.push(...files) // 선택한 파일을 계속 유지
  }
}

// 이미지 미리보기를 위해 파일을 읽는 함수
const view_Image = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    imageSrc.value.push(e.target.result) // 기존 이미지 목록에 새 이미지 추가
  }
  reader.readAsDataURL(file)
}

// 썸네일 삭제
const removeImage = (index) => {
  if (imageSrc.value.length > 0) {
    imageSrc.value.splice(index, 1) // 썸네일 배열에서 선택된 이미지 삭제
    selectedFiles.value.splice(index, 1) // 선택한 파일 배열에서 선택된 이미지 삭제
    if (currentIndex.value >= imageSrc.value.length) {
      currentIndex.value = imageSrc.value.length - 1 // 인덱스가 배열 길이보다 크면 마지막 인덱스로 조정
    }
  }

  // 모든 이미지가 삭제되면 초기화 상태로 돌아감
  if (imageSrc.value.length === 0) {
    close_Modal() // 모달을 닫고 상태 초기화
  }
}

// 모달 바깥 클릭 처리
const over_Click = () => {
  if (imageSrc.value.length > 0) {
    delete_Modal.value = true
  } else {
    close()
  }
}

// 삭제 확인 처리
const Delete = () => {
  // 모든 이미지를 삭제하고 상태 초기화
  imageSrc.value = []
  selectedFiles.value = []
  currentIndex.value = 0 // 모달을 닫을 때 인덱스 초기화
  upload.value = false
  postContent.value = ''
  delete_Modal.value = false
  close_Modal()
  close() // 전체 모달 닫기
}

// 삭제 취소 처리
const cancel_delete = () => {
  delete_Modal.value = false
}

// 뒤로 가기 이미지 클릭 시 이미지 삭제 확인 모달 열기
const arrow_img_delete_Modal = () => {
  if (imageSrc.value.length > 0) {
    arrow_Delete_Modal_img.value = true // 이미지가 선택된 경우 이미지 삭제 확인 모달 열기
  }
}

// 이미지 삭제 확인 처리
const img_delete = () => {
  imageSrc.value = [] // 이미지 배열을 비워서 모든 이미지 삭제
  upload.value = false
  currentIndex.value = 0 // 모달을 닫을 때 인덱스 초기화
  postContent.value = ''
  arrow_Delete_Modal_img.value = false
  close_Modal()
}

// 이미지 삭제 취소 처리
const cancel_img_delete = () => {
  arrow_Delete_Modal_img.value = false // 모달 닫기
}

// 다음 버튼 클릭 시 업로드 상태로 변경
const go_Upload = () => {
  // 이미지와 파일 배열 모두 15개로 제한
  if (imageSrc.value.length > 15) {
    imageSrc.value = imageSrc.value.slice(0, 15)
  }
  if (selectedFiles.value.length > 15) {
    selectedFiles.value = selectedFiles.value.slice(0, 15)
  }
  close_Modal()
  upload.value = true
}

// 뒤로 가기 버튼 클릭 시 자르기 화면으로 돌아가기
const to_back = () => {
  upload.value = false
}

// 오른쪽으로 이미지를 넘기는 함수
const nextImage = () => {
  if (currentIndex.value < imageSrc.value.length - 1) {
    currentIndex.value++
  }
}
// 오른쪽으로 넘김 이미지에서 다시 왼쪽으로 이미지를 넘기는 함수
const prevImage = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

// 현재 이미지 설정 및 ID 관리

const setCurrentImage = (index) => {
  if (imageSrc.value[index]) {
    // 인덱스가 유효한 경우에만 실행
    currentIndex.value = index
  }
}

// 드래그 시작 시 실행되는 함수
const dragStart = (index) => {
  selectedImageIndex.value = index // 드래그한 이미지의 인덱스를 저장
}

// 드래그 앤 드롭으로 이미지 위치 변경
const drop = (index) => {
  // 드래그한 이미지가 떨어진 위치의 인덱스와 현재 선택된 이미지의 인덱스를 비교하여 배열에서 위치를 변경
  const draggedImageIndex = selectedImageIndex.value

  if (draggedImageIndex !== null && draggedImageIndex !== index) {
    // 이미지 배열에서 드래그한 이미지를 제거하고 새로운 위치에 추가
    const draggedImage = imageSrc.value[draggedImageIndex]
    imageSrc.value.splice(draggedImageIndex, 1)
    imageSrc.value.splice(index, 0, draggedImage)

    // 파일 배열도 같은 방식으로 업데이트
    const draggedFile = selectedFiles.value[draggedImageIndex]
    selectedFiles.value.splice(draggedImageIndex, 1)
    selectedFiles.value.splice(index, 0, draggedFile)

    // 현재 인덱스를 업데이트
    currentIndex.value = index
  }
}

// 서버로 이미지 업로드 요청
const uploadImage = () => {
  if (!selectedFiles.value.length) {
    alert('이미지를 선택하세요!')
    return
  }

  const formData = new FormData()
  selectedFiles.value.forEach((file) => {
    formData.append('files', file)
  })
  formData.append('content', postContent.value)

  const token = localStorage.getItem('token')
  axios
    .post('/upload', formData, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      }
    })
    .then(() => {
      alert('업로드 완료!')
      imageSrc.value = []
      upload.value = false
      postContent.value = ''
      selectedFiles.value = []
      close()
      router.go(0)
    })
    .catch((error) => {
      console.error('이미지 업로드 실패:', error)
      alert('이미지 업로드에 실패했습니다. 다시 시도해 주세요.')
    })
}

// 업로드 버튼 클릭 시 처리
const upload_Click = () => {
  uploadImage()
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
  align-items: center;
  justify-content: center;
}

.modal_content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 700px;
  height: 690px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.show {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.show_2 {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.show_3 {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.upload_text {
  color: #2d91fd;
  margin-bottom: 10px;
  font-size: 16px;
}

p {
  margin-bottom: 200px;
  margin-top: -18px;
  font-size: 20px;
  padding-bottom: 11px;
  font-weight: bold;
  border-bottom: 1px solid rgb(185, 185, 185);
  text-align: center;
  width: 80.5vh;
}

.center_image {
  width: 400px;
  margin-bottom: 20px;
  text-align: center;
  pointer-events: none;
}

.center_image_2 {
  background-color: rgb(220, 220, 220);
  object-fit: contain;
  height: 760px;
  margin-top: 0.5px;
  width: 760px;
  pointer-events: none;
}

.center_image_3 {
  background-color: rgb(220, 220, 220);
  object-fit: contain;
  width: 705.8px;
  margin-left: -2px;
  height: 705.8px;
  margin-top: -253px;
  border-right: 1px solid rgb(195, 195, 195);
}

.custom_button {
  display: inline-block;
  background-color: #2d91fd;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 20px;
  font-size: 16px;
  text-align: center;
}

.cut {
  font-weight: bold;
}

.custom_button:hover {
  background-color: #0974e5;
}

.toolbar {
  display: flex;
  border-bottom: 1px solid rgb(177, 177, 177);
  width: 80.5vh;
  justify-content: space-between;
  margin-top: -20px;
  padding-bottom: 9px;
}

.arrow_image {
  width: 20px;
  height: 20px;
  cursor: pointer;
  padding-left: 20px;
}

.next_button {
  cursor: pointer;
  color: #2d91fd;
  font-weight: bold;
  padding-right: 20px;
}

.upload_active {
  width: 1050px;
  padding-left: 33px;
}

.upload_toolbar {
  width: 117.8vh;
  margin-bottom: -4px;
  margin-left: -2.5px;
}

.user_name {
  margin-left: 725px;
  margin-top: 20px;
}

.Content_Content {
  margin-left: 705px;
  height: 200px;
  width: 401px;
  resize: none;
  margin-top: -10px;
  border-top: none;
  border-left: none;
  border-right: none;
  border-bottom: 1px solid rgb(178, 178, 178);
  outline: none;
  font-size: 16pt;
  font-weight: bold;
}

.close_button {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
}

.close_icon {
  width: 27px;
  height: auto;
}

.image_slider {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.arrow {
  background: none;
  border: none;
  font-size: 100px;
  cursor: pointer;
}

.left_arrow {
  width: 30px;
  position: absolute;
  top: 50%;
  left: 2.5%;
  transform: translateY(-50%);
}

.right_arrow {
  width: 30px;
  position: absolute;
  top: 50%;
  right: 2.5%;
  transform: translateY(-50%);
}

.thumbnail_modal_wrapper {
  position: fixed;
  transform: translateY(-100%);
  left: 12%;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.thumbnail_modal {
  background-color: rgba(0, 0, 0, 0.65);
  padding: 12px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.thumbnail_scroll {
  width: 400px;
  height: 122px;
  display: flex;
  overflow-x: auto;
}

.thumbnail_content {
  display: flex;
}

.thumbnail {
  width: 100px;
  height: 100px;
  object-fit: cover;
  cursor: pointer;
  margin-right: 13px;
  cursor: pointer;
}

.thumbnail.active {
  opacity: 1.1;
}

.thumbnail:not(.active) {
  filter: brightness(0.5);
}

.clone {
  position: relative;
  width: 30px;
  height: 30px;
  padding: 10px;
  cursor: pointer;
  transform: translateY(-150%);
  right: -45%;
  border-radius: 50%;
  border: 1px solid black;
  background-color: black;
}

.clone:hover {
  background-color: rgb(92, 92, 92);
  border: 1px solid rgb(92, 92, 92);
}

.clone.active {
  position: relative;
  width: 30px;
  height: 30px;
  padding: 10px;
  cursor: pointer;
  transform: translateY(-150%);
  right: -45%;
  border-radius: 50%;
  border: 1px solid rgb(195, 195, 195);
  background-color: rgb(195, 195, 195);
}

.delete_icon {
  position: relative;
  left: 66.1%;
  transform: translateY(-425%);
  z-index: 1;
  height: auto;
  width: 20px;
  border-radius: 50%;
  padding: 2px;
  background-color: black;
  cursor: pointer;
  display: flex;
  justify-content: center;
}

.delete_icon_image {
  width: 15px;
}

.Modal_plus {
  width: 35px;
  height: 35px;
  margin-left: 21px;
  margin-right: 9px;
  padding: 5px;
  margin-top: 10px;
  border-radius: 50%;
  border: 2px solid white;
  display: flex;
  justify-content: center;
}

.upload_left_arrow {
  width: 30px;
  position: absolute;
  top: 50%;
  left: 2.5%;
  transform: translateY(-480%);
}

.upload_right_arrow {
  width: 30px;
  position: absolute;
  top: 50%;
  right: 2.5%;
  transform: translateY(-480%);
}
</style>
