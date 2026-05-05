<template>
  <section class="page-header-inline">
    <h1>배송지 관리</h1>
    <button class="primary-button" type="button" @click="showAddressModal = true">배송지 추가</button>
  </section>

  <AddressBookPanel
    :addresses="addresses"
    :deleting-id="deletingId"
    :empty-message="'등록된 배송지가 없습니다.'"
    :error-message="errorMessage"
    :loading="loading"
    :loading-message="'배송지 목록을 불러오는 중입니다.'"
    :setting-default-id="settingDefaultId"
    @delete="removeAddress"
    @set-default="setPrimaryAddress"
  />

  <AddressAddModal
    :error-message="errorMessage"
    :open="showAddressModal"
    :saving="saving"
    :suggest-primary="addresses.length === 0"
    @close="showAddressModal = false"
    @submit="submitAddress"
  />
</template>

<script setup>
import { ref, watch } from 'vue'
import { useToast } from '../../../composables/useToast'
import AddressAddModal from './AddressAddModal.vue'
import AddressBookPanel from './AddressBookPanel.vue'

const props = defineProps({
  addresses: {
    type: Array,
    default: () => [],
  },
  errorMessage: {
    type: String,
    default: '',
  },
  loading: {
    type: Boolean,
    default: false,
  },
  saving: {
    type: Boolean,
    default: false,
  },
  createAddress: {
    type: Function,
    default: null,
  },
  deleteAddress: {
    type: Function,
    default: null,
  },
  setDefaultAddress: {
    type: Function,
    default: null,
  },
  deletingId: {
    type: [String, Number],
    default: null,
  },
  settingDefaultId: {
    type: [String, Number],
    default: null,
  },
})

const showAddressModal = ref(false)
const { showToast } = useToast()

watch(
  () => props.errorMessage,
  (message) => {
    if (message) {
      showToast(message, { color: 'error' })
    }
  },
)

async function submitAddress(payload) {
  try {
    await props.createAddress?.(payload)
    showAddressModal.value = false
  } catch {
    // Keep the modal open so the shared form can surface the error message.
  }
}

async function setPrimaryAddress(addressId) {
  await props.setDefaultAddress?.(addressId)
}

async function removeAddress(addressId) {
  await props.deleteAddress?.(addressId)
}
</script>
