<template>
  <Teleport to="body">
    <div v-if="toasts.length > 0" class="notification-toast-stack">
      <article
        v-for="toast in toasts"
        :key="toast.id"
        class="notification-toast"
        role="status"
        @click="handleToastClick(toast)"
      >
        <div class="notification-toast__text">
          <strong>{{ toast.title }}</strong>
          <p>{{ toast.body }}</p>
        </div>

        <button
          type="button"
          class="notification-toast__close"
          aria-label="토스트 닫기"
          @click.stop="removeToast(toast.id)"
        >
          ×
        </button>
      </article>
    </div>
  </Teleport>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useNotificationCenter } from '../composables/useNotificationCenter'

const router = useRouter()
const { toasts, removeToast } = useNotificationCenter()

function handleToastClick(toast) {
  removeToast(toast.id)

  if (toast.url && toast.url.startsWith('/')) {
    router.push(toast.url).catch(() => {})
  }
}
</script>
