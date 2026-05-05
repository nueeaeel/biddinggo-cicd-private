<template>
  <header class="topbar">
    <v-text-field
      class="topbar-search-field"
      density="comfortable"
      hide-details
      placeholder="어떤 경매를 찾으시나요?"
      prepend-inner-icon="mdi-magnify"
      variant="solo"
    />

    <div class="topbar-links">
      <RouterLink to="/mypage">마이페이지</RouterLink>
      <button class="topbar-link-button topbar-link-button--icon" type="button" @click="$emit('open-notification')">
        <span>알림</span>
      </button>
      <template v-if="auth.isAuthenticated">
        <span class="topbar-auth-label">{{ auth.nickname || auth.username || '회원' }}</span>
        <button class="topbar-link-button" type="button" @click="$emit('logout')">로그아웃</button>
      </template>
      <RouterLink v-else to="/login">로그인/회원가입</RouterLink>
    </div>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'

defineProps({
  auth: {
    type: Object,
    required: true,
  },
})

defineEmits(['open-notification', 'logout'])
</script>
