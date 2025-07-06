// @ts-nocheck
// eslint-disable-next-line
import { initializeApp } from "firebase/app";
import { getMessaging } from "firebase/messaging";

const firebaseConfig = {
  apiKey: "AIzaSyCO7-QxQcG3j-fxCGlkgRlAarUo0C2T2-0",
  authDomain: "quitsmoking-886425.firebaseapp.com",
  projectId: "quitsmoking-886425",
  storageBucket: "quitsmoking-886425.appspot.com",
  messagingSenderId: "464304967792",
  appId: "1:464304967792:web:282b837786d6fd1e64a4b5"
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

export { messaging }; 