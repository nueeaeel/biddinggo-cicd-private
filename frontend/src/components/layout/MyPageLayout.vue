<template>
  <div class="app-shell" :class="{ 'app-shell--sidebar-collapsed': isSidebarCollapsed }">
    <MyPageSidebar :collapsed="isSidebarCollapsed" />

    <div class="content-shell">
      <header class="topbar">
        <button
          class="sidebar-toggle-button"
          type="button"
          :aria-label="isSidebarCollapsed ? '네비게이션 바 펼치기' : '네비게이션 바 접기'"
          @click="toggleSidebar"
        >
          <v-icon :icon="isSidebarCollapsed ? 'mdi-menu-open' : 'mdi-menu'" aria-hidden="true" />
        </button>

        <form class="topbar-search-field topbar-search" role="search" @submit.prevent="submitSearch">
          <button type="submit" class="topbar-search__button" aria-label="경매 검색">
            <v-icon icon="mdi-magnify" class="topbar-search__icon" aria-hidden="true" />
          </button>
          <input
            v-model.trim="searchQuery"
            type="search"
            placeholder="어떤 경매를 찾으시나요?"
            aria-label="경매 검색"
            @keydown.enter.prevent="submitSearch"
          />
        </form>

        <div class="topbar-links">
          <button class="topbar-link-button" type="button" @click="openMyPage">마이페이지</button>
          <button class="topbar-link-button topbar-link-button--icon" type="button" @click="isNotificationOpen = true">
            <span>알림</span>
            <span v-if="unreadBadgeLabel" class="topbar-notification-badge">{{ unreadBadgeLabel }}</span>
          </button>
          <template v-if="auth.isAuthenticated">
            <span class="topbar-auth-label">{{ displayUsername }}</span>
            <button class="topbar-link-button" type="button" @click="handleLogout">로그아웃</button>
          </template>
          <button v-else class="topbar-link-button" type="button" @click="openLogin">로그인/회원가입</button>
        </div>
      </header>

      <main class="page-area">
        <RouterView />
      </main>
    </div>

    <NotificationModal :open="isNotificationOpen" @close="isNotificationOpen = false" />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth'
import NotificationModal from '../NotificationModal.vue'
import MyPageSidebar from './MyPageSidebar.vue'
import { useNotificationCenter } from '../../composables/useNotificationCenter'

const SIDEBAR_STORAGE_KEY = 'biddinggo:mypage-sidebar-collapsed'

const route = useRoute()
const router = useRouter()
const { auth, logout } = useAuth()
const isNotificationOpen = ref(false)
const isSidebarCollapsed = ref(readSidebarCollapsed())

const currentSearchQuery = computed(() => String(route.query.q || ''))
const searchQuery = ref(currentSearchQuery.value)
const displayUsername = computed(() => {
  const username = String(auth.nickname || auth.name || auth.username || '').trim()

  return username ? username.slice(0, 10) : '로그인됨'
})

function searchAuctions(query) {
  const keyword = String(query || '').trim()

  if (!keyword) {
    return
  }

  router.push({
    name: 'auction-search',
    query: { q: keyword },
  })
}

function submitSearch() {
  searchAuctions(searchQuery.value)
}

function openLogin() {
  router.push('/login')
}

function openMyPage() {
  router.push('/mypage')
}

function readSidebarCollapsed() {
  if (typeof window === 'undefined') {
    return false
  }

  return window.localStorage.getItem(SIDEBAR_STORAGE_KEY) === 'true'
}

function toggleSidebar() {
  isSidebarCollapsed.value = !isSidebarCollapsed.value

  if (typeof window !== 'undefined') {
    window.localStorage.setItem(SIDEBAR_STORAGE_KEY, String(isSidebarCollapsed.value))
  }
}

async function handleLogout() {
  await logout()
  router.push('/')
}
const { unreadCount } = useNotificationCenter()

const unreadBadgeLabel = computed(() => {
  const count = Number(unreadCount.value || 0)
  if (count <= 0) return ''
  return String(Math.min(count, 99))
})

watch(
  currentSearchQuery,
  (next) => {
    searchQuery.value = String(next || '')
  },
  { immediate: true },
)
</script>
