<template>
  <article class="surface-card admin-inquiry-card">
    <button class="admin-inquiry-card__header" type="button" @click="$emit('toggle')">
      <div class="admin-inquiry-card__heading">
        <AdminStatusBadge :status="inquiry.status" />
        <div>
          <h3>{{ inquiry.title }}</h3>
          <p>{{ inquiry.createdAt }}</p>
        </div>
      </div>
      <v-icon :icon="expanded ? 'mdi-chevron-up' : 'mdi-chevron-down'" />
    </button>

    <div v-if="expanded" class="admin-inquiry-card__body">
      <section class="admin-inquiry-card__question">
        <p>{{ inquiry.question }}</p>
      </section>

      <section v-if="inquiry.answer" class="admin-inquiry-card__answer">
        <div class="admin-inquiry-card__answer-head">
          <strong>{{ inquiry.answer.author }}</strong>
          <span>{{ inquiry.answer.createdAt }}</span>
        </div>
        <p>{{ inquiry.answer.content }}</p>
      </section>

      <section v-else class="admin-inquiry-card__reply">
        <v-textarea
          v-model="replyText"
          auto-grow
          class="admin-inquiry-card__textarea"
          density="comfortable"
          hide-details
          placeholder="답변을 입력해 주세요."
          rows="3"
          variant="solo"
        />
        <button class="primary-button admin-inquiry-card__submit" type="button" @click="submitReply">답변 등록</button>
      </section>
    </div>
  </article>
</template>

<script setup>
import { ref, watch } from 'vue'
import AdminStatusBadge from '../shared/AdminStatusBadge.vue'

const props = defineProps({
  inquiry: {
    type: Object,
    required: true,
  },
  expanded: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['toggle', 'submit-reply'])
const replyText = ref('')

watch(
  () => props.expanded,
  (isExpanded) => {
    if (!isExpanded) {
      replyText.value = ''
    }
  },
)

function submitReply() {
  const content = replyText.value.trim()

  if (!content) {
    return
  }

  emit('submit-reply', content)
  replyText.value = ''
}
</script>
