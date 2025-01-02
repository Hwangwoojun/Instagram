// firebase.js
import { initializeApp } from 'firebase/app'
import { getAnalytics } from 'firebase/analytics'
import { getAuth } from 'firebase/auth'
import { getStorage } from 'firebase/storage'
import { getFirestore } from 'firebase/firestore'

// Firebase 설정 정보
const firebaseConfig = {
  apiKey: 'AIzaSyD-wscWEh2VXeaB1-1iHwmB-LbDuvPa1Nw',
  authDomain: 'instagram-login-regist.firebaseapp.com',
  projectId: 'instagram-login-regist',
  storageBucket: 'instagram-login-regist.firebasestorage.app',
  messagingSenderId: '141251773238',
  appId: '1:141251773238:web:c3c4185899e5608a34e997',
  measurementId: 'G-X0K2H2B436'
}

// Firebase 초기화
const app = initializeApp(firebaseConfig)

// Firebase Analytics 초기화
export const analytics = getAnalytics(app)

// Firebase 인증 초기화
export const auth = getAuth(app)

// Firebase Storage 초기화
export const storage = getStorage(app)

// Firebase Firestore 초기화
export const db = getFirestore(app)
