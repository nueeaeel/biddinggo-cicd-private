<template>
  <div class="stack-list">
    <SurfaceCard v-if="loading" as="article" class="address-card">
      <p>{{ loadingMessage }}</p>
    </SurfaceCard>

    <SurfaceCard v-else-if="!addressItems.length" as="article" class="address-card">
      <p>{{ emptyMessage }}</p>
    </SurfaceCard>

    <SurfaceCard
      v-else
      v-for="address in addressItems"
      :key="address.id"
      as="article"
      class="address-card"
      :class="{ 'address-card--selectable': selectable }"
      @click="handleSelect(address)"
    >
      <div>
        <div class="address-card__top">
          <strong>{{ address.zip }}</strong>
          <span v-if="address.primary" class="primary-tag">기본 배송지</span>
        </div>
        <p>{{ formatAddress(address) }}</p>
      </div>
      <div v-if="showManagementActions" class="address-card__actions">
        <button
          v-if="!address.primary"
          class="secondary-button"
          type="button"
          :disabled="settingDefaultId === address.id"
          @click.stop="$emit('set-default', address.id)"
        >
          {{ settingDefaultId === address.id ? '설정 중...' : '기본 배송지로 설정' }}
        </button>
        <button
          class="ghost-button"
          type="button"
          :disabled="deletingId === address.id"
          @click.stop="$emit('delete', address.id)"
        >
          {{ deletingId === address.id ? '삭제 중...' : '삭제' }}
        </button>
      </div>
    </SurfaceCard>
  </div>

  <div v-if="showAddButton" class="purchase-address-book__actions">
    <button class="primary-button" type="button" @click="$emit('add')">{{ addButtonLabel }}</button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import SurfaceCard from '../../SurfaceCard.vue'

const props = defineProps({
  addButtonLabel: {
    type: String,
    default: '+ 배송지 추가',
  },
  addresses: {
    type: Array,
    default: () => [],
  },
  deletingId: {
    type: [String, Number],
    default: null,
  },
  emptyMessage: {
    type: String,
    default: '등록된 배송지가 없습니다.',
  },
  errorMessage: {
    type: String,
    default: '',
  },
  loading: {
    type: Boolean,
    default: false,
  },
  loadingMessage: {
    type: String,
    default: '배송지 목록을 불러오는 중입니다.',
  },
  selectable: {
    type: Boolean,
    default: false,
  },
  settingDefaultId: {
    type: [String, Number],
    default: null,
  },
  showAddButton: {
    type: Boolean,
    default: false,
  },
  showManagementActions: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['add', 'delete', 'select', 'set-default'])

const addressItems = computed(() => props.addresses.map((address, index) => ({
  ...address,
  id: address.id ?? `address-${index}-${address.zip || 'zip'}`,
})))

function formatAddress(address = {}) {
  if (address.address) {
    return address.address
  }

  return [address.address1, address.address2].filter(Boolean).join(' ') || '-'
}

function handleSelect(address) {
  if (props.selectable) {
    emit('select', address)
  }
}
</script>
