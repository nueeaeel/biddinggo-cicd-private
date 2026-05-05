<template>
  <header class="topbar">
    <form class="topbar-search-field topbar-search" role="search" @submit.prevent="submitSearch">
      <button type="submit" class="topbar-search__button" aria-label="상품 검색">
        <v-icon icon="mdi-magnify" class="topbar-search__icon" aria-hidden="true" />
      </button>
      <input
        v-model.trim="searchQuery"
        type="search"
        placeholder="어떤 경매를 찾으시나요?"
        aria-label="경매 검색어"
        @keydown.enter.prevent="submitSearch"
      />
    </form>

    <div class="topbar-links">
      <button class="topbar-link-button" type="button" @click="$emit('open-mypage')">마이페이지</button>
      <button class="topbar-link-button topbar-link-button--icon" type="button" @click="$emit('open-notification')">
        <span>알림</span>
      </button>
      <template v-if="auth.isAuthenticated">
        <span class="topbar-auth-label">{{ displayUsername }}</span>
        <button class="topbar-link-button" type="button" @click="$emit('logout')">로그아웃</button>
      </template>
      <button v-else class="topbar-link-button" type="button" @click="$emit('open-login')">로그인/회원가입</button>
    </div>
  </header>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  auth: {
    type: Object,
    required: true,
  },
  initialSearchQuery: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['open-login', 'open-mypage', 'open-notification', 'logout', 'search'])

const searchQuery = ref(String(props.initialSearchQuery || ''))

const displayUsername = computed(() => {
  const username = String(props.auth.nickname || props.auth.name || props.auth.username || '').trim()

  return username ? username.slice(0, 10) : '로그인됨'
})

function submitSearch() {
  const keyword = String(searchQuery.value || '').trim()

  if (!keyword) {
    return
  }

  emit('search', keyword)
}

watch(
  () => props.initialSearchQuery,
  (next) => {
    searchQuery.value = String(next || '')
  },
  { immediate: true },
)
</script>
