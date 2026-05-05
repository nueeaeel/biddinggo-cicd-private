<template>
  <div class="modal-backdrop admin-modal-backdrop" @click.self="$emit('close')">
    <section class="admin-notice-modal">
      <header class="admin-notice-modal__head">
        <h2>{{ isEdit ? '공지사항 수정' : '공지사항 등록' }}</h2>
        <button class="admin-notice-modal__close" type="button" @click="$emit('close')">
          <v-icon icon="mdi-close" />
        </button>
      </header>

      <form class="admin-notice-modal__form" @submit.prevent="submit">
        <label>
          <span>공지사항 제목 <em>*</em></span>
          <input v-model="title" placeholder="제목을 입력해 주세요." type="text" />
        </label>

        <label>
          <span>공지사항 내용 <em>*</em></span>
          <textarea v-model="content" placeholder="내용을 입력해 주세요." rows="5" />
        </label>

        <button class="primary-button" type="submit">{{ isEdit ? '수정하기' : '등록하기' }}</button>
      </form>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  notice: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['close', 'submit'])

const title = ref('')
const content = ref('')
const isEdit = computed(() => Boolean(props.notice))

watch(
  () => props.notice,
  (next) => {
    title.value = next?.title ?? ''
    content.value = next?.content ?? ''
  },
  { immediate: true },
)

function submit() {
  const payload = {
    title: title.value.trim(),
    content: content.value.trim(),
  }

  if (!payload.title || !payload.content) {
    return
  }

  emit('submit', payload)
}
</script>
