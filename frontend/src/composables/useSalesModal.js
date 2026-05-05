import { reactive, ref } from 'vue'

export function useSalesModal({ onSaveShipping } = {}) {
  const selectedItem = ref(null)
  const modalMode = ref('detail')
  const savingShipping = ref(false)
  const shippingForm = reactive({
    courier: '',
    trackingNumber: '',
  })

  function openModal(item) {
    selectedItem.value = item
    modalMode.value = 'detail'
    shippingForm.courier = item.shippingInfo?.courier ?? ''
    shippingForm.trackingNumber = item.shippingInfo?.trackingNumber ?? ''
  }

  function closeModal() {
    selectedItem.value = null
    modalMode.value = 'detail'
    shippingForm.courier = ''
    shippingForm.trackingNumber = ''
  }

  function updateForm(field, value) {
    shippingForm[field] = value
  }

  async function saveShipping() {
    if (!selectedItem.value || !shippingForm.courier || !shippingForm.trackingNumber || savingShipping.value) {
      return
    }

    const nextShipping = {
      courier: shippingForm.courier,
      trackingNumber: shippingForm.trackingNumber,
    }

    savingShipping.value = true

    try {
      if (onSaveShipping) {
        await onSaveShipping(selectedItem.value, nextShipping)
      }

      selectedItem.value.shippingInfo = nextShipping
      selectedItem.value.status = '배송 중'
      selectedItem.value.modalType = 'readonly'
      modalMode.value = 'detail'
    } finally {
      savingShipping.value = false
    }
  }

  return {
    selectedItem,
    modalMode,
    shippingForm,
    savingShipping,
    openModal,
    closeModal,
    updateForm,
    saveShipping,
  }
}
