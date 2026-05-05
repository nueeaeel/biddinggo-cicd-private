<template>
  <aside
    ref="sidebarRef"
    class="sidebar"
    :class="{ 'sidebar--collapsed': collapsed, 'sidebar--scrolling': isSidebarScrolling }"
    @scroll="handleSidebarScroll"
  >
    <RouterLink class="brand-block" to="/">
      <strong>Biddinggo</strong>
      <span>BIDDINGMATE</span>
    </RouterLink>

    <nav class="sidebar-nav" aria-label="마이페이지 메뉴">
      <RouterLink
        v-for="item in navItems"
        :key="item.route"
        :to="item.route"
        class="sidebar-link"
        :class="{ active: isActive(item.route) }"
      >
        {{ item.label }}
      </RouterLink>
    </nav>
  </aside>
</template>

<script setup>
import { onBeforeUnmount, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { navItems } from '../../data/mypage'

const route = useRoute()
defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})
const sidebarRef = ref(null)
const isSidebarScrolling = ref(false)
let sidebarScrollTimer = null

function isActive(targetRoute) {
  return route.path === targetRoute
}

function handleSidebarScroll() {
  if (!sidebarRef.value) {
    return
  }

  isSidebarScrolling.value = true

  if (sidebarScrollTimer) {
    window.clearTimeout(sidebarScrollTimer)
  }

  sidebarScrollTimer = window.setTimeout(() => {
    isSidebarScrolling.value = false
  }, 240)
}

onBeforeUnmount(() => {
  if (sidebarScrollTimer) {
    window.clearTimeout(sidebarScrollTimer)
  }
})
</script>
