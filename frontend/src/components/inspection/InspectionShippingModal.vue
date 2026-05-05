<script setup>
import BaseModal from '../shared/BaseModal.vue'
import { courierOptions } from '../../data/salesHistory'

defineProps({
  form: {
    type: Object,
    required: true,
  },
})

defineEmits(['close', 'submit'])
</script>

<template>
  <BaseModal
    overlay-class="inspection-shipping-overlay"
    panel-class="inspection-shipping-modal"
    @close="$emit('close')"
  >
    <template #header>
      <div class="inspection-shipping-header">
        <h3>배송 정보 등록</h3>
        <button type="button" class="inspection-shipping-close" @click="$emit('close')">×</button>
      </div>
    </template>

      <div class="inspection-shipping-form">
        <label class="inspection-shipping-field is-small">
          <span>택배사 <em>*</em></span>
          <v-menu location="bottom start" offset="8">
            <template #activator="{ props: menuProps }">
              <button
                v-bind="menuProps"
                class="winnerdeal-courier-select"
                type="button"
              >
                <span>{{ form.company || '선택' }}</span>
                <v-icon icon="mdi-chevron-down" />
              </button>
            </template>

            <div class="winnerdeal-courier-menu inspection-shipping-courier-menu">
              <button
                v-for="option in courierOptions"
                :key="option"
                class="winnerdeal-courier-menu__item"
                type="button"
                @click="form.company = option"
              >
                {{ option }}
              </button>
            </div>
          </v-menu>
        </label>

        <label class="inspection-shipping-field">
          <span>송장 번호 <em>*</em></span>
          <input
            v-model="form.invoiceNumber"
            type="text"
            placeholder="송장 번호를 입력해 주세요."
          />
        </label>
      </div>

      <button type="button" class="inspection-shipping-submit" @click="$emit('submit')">
        등록하기
      </button>
  </BaseModal>
</template>
