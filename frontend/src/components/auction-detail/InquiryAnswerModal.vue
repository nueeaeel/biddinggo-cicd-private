<script setup>
import BaseModal from '../shared/BaseModal.vue'

defineProps({
  form: {
    type: Object,
    required: true,
  },
  inquiry: {
    type: Object,
    required: true,
  },
  submitting: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['close', 'submit'])
</script>

<template>
  <BaseModal panel-class="detail-inquiry-answer-modal" title="문의 답변하기" @close="$emit('close')">
    <div class="detail-answer-target">
      <span>문의 제목</span>
      <strong>{{ inquiry.title }}</strong>
      <p>{{ inquiry.question }}</p>
    </div>

    <label class="detail-inquiry-field">
      <span>답변 내용 <em>*</em></span>
      <textarea
        v-model="form.answer"
        placeholder="구매자에게 전달할 답변을 입력해주세요."
      />
    </label>

    <button
      type="button"
      class="detail-inquiry-submit"
      :disabled="submitting"
      @click="$emit('submit')"
    >
      {{ submitting ? '등록 중...' : '답변하기' }}
    </button>
  </BaseModal>
</template>
