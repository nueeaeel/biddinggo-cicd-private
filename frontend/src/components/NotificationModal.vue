<template>
  <BaseModal
    :open="open"
    panel-class="purchase-modal purchase-modal--notification"
    body-class="base-modal__body base-modal__body--notification modal-scroll"
    header-label="알림"
    @close="$emit('close')"
  >
    <template #action>
      <div class="notification-modal-actions">
        <button
          type="button"
          class="notification-mark-all-button"
          :disabled="unreadCount <= 0"
          @click="handleReadAll"
        >
          전체 읽음
        </button>
        <button class="base-modal__close" type="button" @click="$emit('close')">
          <v-icon icon="mdi-close" />
        </button>
      </div>
    </template>

    <div v-if="notifications.length > 0" class="notification-list">
      <article
        v-for="notification in notifications"
        :key="notification.id"
        class="notification-card notification-card--interactive"
        :class="{ unread: notification.unread }"
        @click="handleNotificationClick(notification)"
      >
        <span v-if="notification.unread" class="notification-card__dot"></span>

        <div class="notification-card__body">
          <h3>{{ notification.title }}</h3>
          <p>{{ notification.body }}</p>
          <span>{{ notification.time }}</span>
        </div>

        <img
          v-if="notification.image"
          :src="notification.image"
          :alt="notification.title"
        />
      </article>
    </div>

    <div v-else class="notification-empty">
      도착한 알림이 없습니다.
    </div>
  </BaseModal>
</template>

<script setup>
import { watch } from 'vue'
import { useRouter } from 'vue-router'
import BaseModal from './BaseModal.vue'
import { useNotificationCenter } from '../composables/useNotificationCenter'

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['close'])

const router = useRouter()
const {
  notifications,
  unreadCount,
  loadNotifications,
  refreshUnreadCount,
  markAsRead,
  markAllAsRead,
} = useNotificationCenter()

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) return
    void Promise.all([loadNotifications(), refreshUnreadCount()])
  },
  { immediate: true },
)

async function handleNotificationClick(notification) {
  if (notification.unread) {
    await markAsRead(notification.id)
  }

  emit('close')

  if (notification.url && notification.url.startsWith('/')) {
    router.push(notification.url).catch(() => {})
  }
}

async function handleReadAll() {
  await markAllAsRead()
}
</script>
