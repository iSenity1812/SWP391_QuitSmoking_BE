importScripts('https://www.gstatic.com/firebasejs/10.12.2/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.2/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: "AIzaSyCO7-QxQcG3j-fxCGlkgRlAarUo0C2T2-0",
  authDomain: "quitsmoking-886425.firebaseapp.com",
  projectId: "quitsmoking-886425",
  storageBucket: "quitsmoking-886425.appspot.com",
  messagingSenderId: "464304967792",
  appId: "1:464304967792:web:282b837786d6fd1e64a4b5"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
  self.registration.showNotification(payload.notification.title, {
    body: payload.notification.body,
    icon: '/firebase-logo.png'
  });
}); 