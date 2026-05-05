<script setup>
import { computed } from 'vue'
import BaseModal from '../shared/BaseModal.vue'

defineEmits(['close', 'submit'])

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
  reportTypes: {
    type: Array,
    required: true,
  },
  submitting: {
    type: Boolean,
    default: false,
  },
})

const thumbnailUrl = computed(() => {
  const imageUrls = Array.isArray(props.item?.images)
    ? props.item.images.map((image) => image?.url || image?.publicUrl || image?.imageUrl).filter(Boolean)
    : []

  return props.item?.image || props.item?.representativeImageUrl || imageUrls[0] || props.assets.listWatchImage
})
</script>

<template>
  <BaseModal panel-class="detail-report-modal" title="신고하기" @close="$emit('close')">
      <div class="detail-inquiry-summary">
        <img :src="thumbnailUrl" :alt="item.title" class="detail-inquiry-thumb" />
        <div class="detail-inquiry-summary-copy">
          <strong>{{ item.title }}</strong>
          <span>현재 입찰가</span>
          <em>{{ item.price }}원</em>
        </div>
      </div>

      <div class="detail-report-fields">
        <div class="detail-report-field">
          <span>신고 유형 <em>*</em></span>
          <div class="detail-report-options">
            <button
              v-for="type in reportTypes"
              :key="type"
              type="button"
              class="detail-report-option"
              :class="{ 'is-selected': form.type === type }"
              @click="form.type = type"
            >
              <span class="detail-report-radio" />
              <span>{{ type }}</span>
            </button>
          </div>
        </div>

        <label class="detail-report-field">
          <span>신고 내용 <em>*</em></span>
          <textarea
            v-model="form.detail"
            class="detail-report-textarea"
            placeholder="신고 사유에 대한 구체적인 내용을 작성해주세요."
          />
        </label>
      </div>

      <button
        type="button"
        class="detail-report-submit"
        :disabled="submitting"
        @click="$emit('submit')"
      >
        {{ submitting ? '접수 중...' : '신고하기' }}
      </button>
  </BaseModal>
</template>
