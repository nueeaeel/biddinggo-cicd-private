import { reactive, ref } from 'vue'

export function usePurchaseModal({ onConfirmPurchase, onSaveAddress, onSubmitReview } = {}) {
  const selectedItem = ref(null)
  const modalMode = ref('detail')
  const savingAddress = ref(false)
  const confirmingPurchase = ref(false)
  const submittingReview = ref(false)
  const modalErrorMessage = ref('')
  const shippingForm = reactive({
    name: '',
    phone: '',
    zip: '',
    address1: '',
    address2: '',
  })
  const reviewForm = reactive({
    rating: 5,
    content: '',
  })

  function syncShippingForm(address = {}) {
    shippingForm.name = address.name || address.recipient || ''
    shippingForm.phone = address.phone || address.tel || ''
    shippingForm.zip = address.zip || address.zipcode || ''
    shippingForm.address1 = address.address1 || address.address || ''
    shippingForm.address2 = address.address2 || address.detailAddress || ''
  }

  function resetReviewForm() {
    reviewForm.rating = 5
    reviewForm.content = ''
  }

  function normalizeReviewRating(value) {
    const parsedValue = Number(value)

    if (Number.isNaN(parsedValue)) {
      return 5
    }

    return Math.min(5, Math.max(1, Math.round(parsedValue)))
  }

  function openModal(item) {
    selectedItem.value = item
    modalMode.value = 'detail'
    modalErrorMessage.value = ''
    syncShippingForm(item?.shippingAddress || {})
    resetReviewForm()
  }

  function closeModal() {
    selectedItem.value = null
    modalMode.value = 'detail'
    modalErrorMessage.value = ''
    resetReviewForm()
  }

  function selectAddress(address) {
    syncShippingForm({
      ...address,
      name: address?.name || address?.recipient || shippingForm.name,
      phone: address?.phone || address?.tel || shippingForm.phone,
    })
    modalMode.value = 'shipping-form'
  }

  function updateForm(field, value) {
    if (field in shippingForm) {
      shippingForm[field] = value
      return
    }

    if (field in reviewForm) {
      reviewForm[field] = field === 'rating' ? normalizeReviewRating(value) : value
    }
  }

  async function saveAddress() {
    if (!selectedItem.value || savingAddress.value) {
      return
    }

    const nextAddress = {
      name: shippingForm.name,
      phone: shippingForm.phone,
      zip: shippingForm.zip,
      address1: shippingForm.address1,
      address2: shippingForm.address2,
    }

    savingAddress.value = true
    modalErrorMessage.value = ''

    try {
      if (onSaveAddress) {
        await onSaveAddress(selectedItem.value, nextAddress)
      }

      selectedItem.value.shippingAddress = nextAddress
      selectedItem.value.modalType = 'readonly'
      modalMode.value = 'detail'
    } catch (error) {
      modalErrorMessage.value = error?.message || '배송지 정보를 등록하지 못했습니다.'
    } finally {
      savingAddress.value = false
    }
  }

  async function confirmPurchase() {
    if (!selectedItem.value || confirmingPurchase.value) {
      return
    }

    confirmingPurchase.value = true
    modalErrorMessage.value = ''

    try {
      if (onConfirmPurchase) {
        await onConfirmPurchase(selectedItem.value)
      }

      selectedItem.value.status = '거래 완료'
      selectedItem.value.rawStatus = 'CONFIRMED'
      selectedItem.value.modalType = 'readonly'
      closeModal()
    } catch (error) {
      modalErrorMessage.value = error?.message || '구매 확정에 실패했습니다.'
    } finally {
      confirmingPurchase.value = false
    }
  }

  async function submitReview() {
    if (!selectedItem.value || submittingReview.value) {
      return
    }

    submittingReview.value = true
    modalErrorMessage.value = ''

    try {
      if (onSubmitReview) {
        await onSubmitReview(selectedItem.value, {
          rating: normalizeReviewRating(reviewForm.rating),
          content: reviewForm.content.trim(),
        })
      }

      closeModal()
    } catch (error) {
      modalErrorMessage.value = error?.message || '리뷰를 작성하지 못했습니다.'
    } finally {
      submittingReview.value = false
    }
  }

  return {
    selectedItem,
    modalMode,
    shippingForm,
    reviewForm,
    savingAddress,
    confirmingPurchase,
    submittingReview,
    modalErrorMessage,
    openModal,
    closeModal,
    selectAddress,
    updateForm,
    saveAddress,
    confirmPurchase,
    submitReview,
  }
}
