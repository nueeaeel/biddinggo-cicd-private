<script setup>
import { computed } from 'vue'
import BaseModal from '../shared/BaseModal.vue'

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  form: {
    type: Object,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
  submitting: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['close', 'submit'])

const thumbnailUrl = computed(() => {
  const firstImage = props.item?.images?.[0]

  return firstImage?.url || firstImage?.publicUrl || firstImage?.imageUrl || props.assets.listWatchImage
})
</script>

<template>
  <BaseModal panel-class="detail-inquiry-modal" title="문의하기" @close="$emit('close')">
    <div class="detail-inquiry-summary">
      <img :src="thumbnailUrl" :alt="item.title" class="detail-inquiry-thumb" />
      <div class="detail-inquiry-summary-copy">
        <strong>{{ item.title }}</strong>
        <span>현재 입찰가</span>
        <em>{{ item.price }}</em>
      </div>
    </div>

    <label class="detail-inquiry-secret">
      <input v-model="form.isPrivate" type="checkbox" />
      <span>비밀글</span>
    </label>

    <div class="detail-inquiry-fields">
      <label class="detail-inquiry-field">
        <span>문의 제목 <em>*</em></span>
        <input
          v-model="form.title"
          type="text"
          maxlength="50"
          placeholder="제목을 입력해 주세요."
        />
      </label>

      <label class="detail-inquiry-field">
        <span>문의 내용 <em>*</em></span>
        <textarea
          v-model="form.content"
          placeholder="문의 내용을 상세하게 작성해 주세요."
        />
      </label>
    </div>

    <button
      type="button"
      class="detail-inquiry-submit"
      :disabled="submitting"
      @click="$emit('submit')"
    >
      {{ submitting ? '등록 중...' : '문의하기' }}
    </button>
  </BaseModal>
</template>
