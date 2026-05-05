<template>
  <div class="modal-backdrop admin-modal-backdrop" @click.self="$emit('close')">
    <section class="admin-notice-modal">
      <header class="admin-notice-modal__head">
        <h2>검수 반려</h2>
        <button class="admin-notice-modal__close" type="button" @click="$emit('close')">
          <v-icon icon="mdi-close" />
        </button>
      </header>

      <form class="admin-notice-modal__form" @submit.prevent="submit">
        <label>
          <span>반려 사유 <em>*</em></span>
          <textarea v-model="reason" rows="5" placeholder="반려 사유를 입력해 주세요." />
        </label>

        <button class="primary-button" type="submit">반려 처리</button>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close', 'submit'])
const reason = ref('')

function submit() {
  const value = reason.value.trim()
  if (!value) return
  emit('submit', value)
  reason.value = ''
}
</script>
