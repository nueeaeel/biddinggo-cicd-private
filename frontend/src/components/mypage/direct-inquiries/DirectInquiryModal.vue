<template>
  <BaseModal
    :open="open"
    panel-class="purchase-modal direct-inquiry-modal"
    header-label="문의하기"
    @close="$emit('close')"
  >
    <form class="direct-inquiry-form" @submit.prevent="$emit('submit')">
      <section class="direct-inquiry-form__section">
        <label class="direct-inquiry-form__label">문의 유형 <em>*</em></label>
        <div class="direct-inquiry-form__grid">
          <label
            v-for="option in typeOptions"
            :key="option"
            class="direct-inquiry-option"
            :class="{ 'direct-inquiry-option--selected': modelValue.type === option }"
          >
            <input
              :checked="modelValue.type === option"
              class="direct-inquiry-option__input"
              name="direct-type"
              type="radio"
              :value="option"
              @change="$emit('update:modelValue', { ...modelValue, type: option })"
            />
            <span class="direct-inquiry-option__radio"></span>
            <span>{{ option }}</span>
          </label>
        </div>
      </section>

      <section class="direct-inquiry-form__section">
        <label class="direct-inquiry-form__label">문의 내용 <em>*</em></label>
        <textarea
          :value="modelValue.content"
          class="direct-inquiry-form__textarea"
          placeholder="문의 내용을 상세히 작성해 주세요."
          @input="$emit('update:modelValue', { ...modelValue, content: $event.target.value })"
        />
      </section>

      <button class="primary-button direct-inquiry-form__submit" type="submit">문의하기</button>
    </form>
  </BaseModal>
</template>

<script setup>
import BaseModal from '../../BaseModal.vue'

defineProps({
  modelValue: {
    type: Object,
    required: true,
  },
  open: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['close', 'submit', 'update:modelValue'])

const typeOptions = ['결제', '계정', '경매', '배송', '분쟁 해결', '기타']
</script>
