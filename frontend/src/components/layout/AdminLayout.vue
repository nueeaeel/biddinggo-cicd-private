<template>
  <div class="app-shell admin-layout" :class="{ 'app-shell--sidebar-collapsed': isSidebarCollapsed }">
    <aside class="sidebar admin-layout__sidebar">
      <RouterLink class="brand-block" to="/">
        <strong>Biddinggo</strong>
        <span>BIDDINGMATE</span>
      </RouterLink>

      <nav class="sidebar-nav admin-layout__nav" aria-label="Admin Menu">
        <RouterLink
          v-for="item in adminNavItems"
          :key="item.key"
          :to="item.route"
          class="sidebar-link admin-layout__link"
          :class="{ active: isActive(item.route) }"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
    </aside>

    <div class="content-shell">
      <header class="topbar admin-layout__topbar">
        <button
          class="sidebar-toggle-button"
          type="button"
          :aria-label="isSidebarCollapsed ? '네비게이션 바 펼치기' : '네비게이션 바 접기'"
          @click="toggleSidebar"
        >
          <v-icon :icon="isSidebarCollapsed ? 'mdi-menu-open' : 'mdi-menu'" aria-hidden="true" />
        </button>

        <button class="topbar-link-button" type="button" :disabled="isLoggingOut" @click="handleLogout">
          로그아웃
        </button>
      </header>

      <main class="page-area admin-layout__page">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth'
import { adminNavItems } from '../../data/admin'

const SIDEBAR_STORAGE_KEY = 'biddinggo:admin-sidebar-collapsed'

const route = useRoute()
const router = useRouter()
const { logout } = useAuth()
const isLoggingOut = ref(false)
const isSidebarCollapsed = ref(readSidebarCollapsed())

function isActive(targetRoute) {
  return route.path === targetRoute
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
  if (isLoggingOut.value) return
  isLoggingOut.value = true

  try {
    await logout()
    await router.replace({ name: 'admin-login' })
  } finally {
    isLoggingOut.value = false
  }
}
</script>
