<template>
  <div class="app-shell" :class="{ 'app-shell--sidebar-collapsed': isSidebarCollapsed }">
    <aside
      ref="sidebarRef"
      class="sidebar"
      :class="{ 'sidebar--scrolling': isSidebarScrolling }"
      @scroll="handleSidebarScroll"
    >
      <button class="brand-block app-shell__brand" type="button" @click="emit('navigate', '/')">
        <strong>Biddinggo</strong>
        <span>BIDDINGMATE</span>
      </button>

      <nav class="sidebar-nav app-shell__nav" aria-label="주요 메뉴">
        <button
          v-for="item in navigationItems"
          :key="item.label"
          type="button"
          class="sidebar-link app-shell__nav-link"
          :class="{ active: currentNavKey === item.key || (!currentNavKey && currentScreen === item.key) }"
          @click="emit('navigate', item.route ?? item.key)"
        >
          <span>{{ item.label }}</span>
        </button>
      </nav>
    </aside>

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
          <button type="submit" class="topbar-search__button" aria-label="상품 검색">
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
          <button class="topbar-link-button" type="button" @click="emit('open-mypage')">마이페이지</button>
          <button class="topbar-link-button topbar-link-button--icon" type="button" @click="isNotificationOpen = true">
            <span>알림</span>
            <span v-if="unreadBadgeLabel" class="topbar-notification-badge">{{ unreadBadgeLabel }}</span>
          </button>
          <template v-if="auth.isAuthenticated">
            <span class="topbar-auth-label">{{ displayUsername }}</span>
            <button class="topbar-link-button" type="button" @click="emit('logout')">로그아웃</button>
          </template>
          <button v-else class="topbar-link-button" type="button" @click="emit('open-login')">로그인/회원가입</button>
        </div>
      </header>

      <main class="page-area">
        <slot />
      </main>
    </div>

    <NotificationModal :open="isNotificationOpen" @close="isNotificationOpen = false" />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import NotificationModal from './NotificationModal.vue'
import { useNotificationCenter } from '../composables/useNotificationCenter'

const SIDEBAR_STORAGE_KEY = 'biddinggo:sidebar-collapsed'

const props = defineProps({
  currentScreen: {
    type: String,
    required: true,
  },
  currentNavKey: {
    type: String,
    default: '',
  },
  navigationItems: {
    type: Array,
    required: true,
  },
  auth: {
    type: Object,
    required: true,
  },
  initialSearchQuery: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['navigate', 'open-login', 'open-mypage', 'logout', 'search'])

const isNotificationOpen = ref(false)
const isSidebarCollapsed = ref(readSidebarCollapsed())
const sidebarRef = ref(null)
const isSidebarScrolling = ref(false)
const searchQuery = ref(String(props.initialSearchQuery || ''))
let sidebarScrollTimer = null

const displayUsername = computed(() => {
  const username = String(props.auth.nickname || props.auth.name || props.auth.username || '').trim()

  return username ? username.slice(0, 10) : '로그인됨'
})

const { unreadCount } = useNotificationCenter()

const unreadBadgeLabel = computed(() => {
  const count = Number(unreadCount.value || 0)
  if (count <= 0) return ''
  return String(Math.min(count, 99))
})

function clearSidebarScrollTimer() {
  if (sidebarScrollTimer) {
    window.clearTimeout(sidebarScrollTimer)
    sidebarScrollTimer = null
  }
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

function handleSidebarScroll() {
  if (!sidebarRef.value) {
    return
  }

  isSidebarScrolling.value = true
  clearSidebarScrollTimer()

  sidebarScrollTimer = window.setTimeout(() => {
    isSidebarScrolling.value = false
    sidebarScrollTimer = null
  }, 240)
}

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

onBeforeUnmount(() => {
  clearSidebarScrollTimer()
})
</script>
