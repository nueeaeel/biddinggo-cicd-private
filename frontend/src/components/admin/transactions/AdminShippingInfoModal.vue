<template>
  <div class="modal-backdrop admin-modal-backdrop" @click.self="$emit('close')">
    <section class="admin-shipping-modal">
      <header class="admin-shipping-modal__head">
        <h2>배송 정보 등록</h2>
        <button class="admin-shipping-modal__close" type="button" @click="$emit('close')">
          <v-icon icon="mdi-close" />
        </button>
      </header>

      <form class="admin-shipping-modal__form" @submit.prevent="submit">
        <label>
          <span>택배사 <em>*</em></span>
          <v-menu location="bottom start" offset="8">
            <template #activator="{ props: menuProps }">
              <button
                v-bind="menuProps"
                class="winnerdeal-courier-select admin-shipping-modal__carrier-select"
                type="button"
              >
                <span>{{ carrier || '선택' }}</span>
                <v-icon icon="mdi-chevron-down" />
              </button>
            </template>

            <div class="winnerdeal-courier-menu admin-shipping-modal__carrier-menu">
              <button
                v-for="company in shippingCompanies"
                :key="company"
                class="winnerdeal-courier-menu__item"
                type="button"
                @click="carrier = company"
              >
                {{ company }}
              </button>
            </div>
          </v-menu>
        </label>

        <label>
          <span>송장 번호 <em>*</em></span>
          <input v-model="trackingNumber" placeholder="송장 번호를 입력해 주세요." type="text" />
        </label>

        <button class="primary-button" type="submit">등록하기</button>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
  shippingCompanies: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['close', 'submit'])

const carrier = ref('')
const trackingNumber = ref('')

watch(
  () => props.item,
  (nextItem) => {
    carrier.value = nextItem?.shippingInfo?.carrier ?? ''
    trackingNumber.value = nextItem?.shippingInfo?.trackingNumber ?? ''
  },
  { immediate: true },
)

function submit() {
  const payload = {
    carrier: carrier.value.trim(),
    trackingNumber: trackingNumber.value.trim(),
  }

  if (!payload.carrier || !payload.trackingNumber) {
    return
  }

  emit('submit', payload)
}
</script>
