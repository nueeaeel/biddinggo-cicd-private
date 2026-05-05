<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
  canAnswer: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['open-answer'])

const expandedInquiryKey = ref('')

const inquiries = computed(() => props.item.inquiries || [])

function getInquiryKey(inquiry, index) {
  return String(inquiry.id ?? `${inquiry.title}-${index}`)
}

function isExpanded(inquiry, index) {
  return expandedInquiryKey.value === getInquiryKey(inquiry, index)
}

function toggleInquiry(inquiry, index) {
  const key = getInquiryKey(inquiry, index)
  expandedInquiryKey.value = expandedInquiryKey.value === key ? '' : key
}

watch(
  inquiries,
  (nextInquiries) => {
    expandedInquiryKey.value = nextInquiries.length ? getInquiryKey(nextInquiries[0], 0) : ''
  },
  { immediate: true },
)
</script>

<template>
  <div class="inquiry-section">
    <div class="inquiry-heading">상품 문의 ({{ inquiries.length }})</div>
    <div v-if="inquiries.length" class="inquiry-list">
      <article
        v-for="(inquiry, index) in inquiries"
        :key="getInquiryKey(inquiry, index)"
        class="inquiry-item"
        :class="{ 'is-expanded': isExpanded(inquiry, index) }"
      >
        <button
          type="button"
          class="inquiry-summary"
          :aria-expanded="isExpanded(inquiry, index)"
          @click="toggleInquiry(inquiry, index)"
        >
          <div class="inquiry-summary-left">
            <span class="status-badge" :class="{ 'is-pending': inquiry.status === '답변 대기' }">
              {{ inquiry.status }}
            </span>
            <div class="inquiry-text">
              <strong>{{ inquiry.title }}</strong>
              <span>{{ inquiry.meta }}</span>
            </div>
          </div>
          <span class="inquiry-toggle">{{ isExpanded(inquiry, index) ? '⌃' : '⌄' }}</span>
        </button>

        <div v-if="isExpanded(inquiry, index)" class="inquiry-detail">
          <div class="question-box">
            {{ inquiry.question }}
          </div>
          <div v-if="inquiry.status === '답변 완료'" class="answer-box">
            <div class="answer-icon" aria-hidden="true">A</div>
            <div class="answer-content">
              <div class="answer-label">
                <strong>판매자 답변</strong>
                <span v-if="inquiry.answeredAt !== '-'">{{ inquiry.answeredAt }}</span>
              </div>
              <p>{{ inquiry.answer }}</p>
            </div>
          </div>
          <div v-else-if="canAnswer" class="answer-action-row">
            <button type="button" class="answer-action-button" @click="$emit('open-answer', inquiry)">
              답변하기
            </button>
          </div>
          <div v-else class="answer-box is-empty">
            <div class="answer-content">
              <div class="answer-label"><strong>판매자 답변</strong></div>
              <p>아직 답변이 등록되지 않았습니다.</p>
            </div>
          </div>
        </div>
      </article>
    </div>
    <div v-else class="question-box">아직 등록된 문의가 없습니다.</div>
  </div>
</template>
