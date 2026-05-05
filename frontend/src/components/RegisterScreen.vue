<script setup>
import { toRef } from 'vue'
import { useRouter } from 'vue-router'
import noImage from '../assets/no-image.svg'
import { assets } from '../data/marketplaceData'
import { useRegisterFlow } from '../composables/useRegisterFlow'
import AuctionSetupForm from './register/AuctionSetupForm.vue'
import InspectionPickStep from './register/InspectionPickStep.vue'
import RegisterMethodSelect from './register/RegisterMethodSelect.vue'
import RegisterProductForm from './register/RegisterProductForm.vue'
import RegisterStepper from './register/RegisterStepper.vue'

const inspectionProductImage = noImage

const props = defineProps({
  initialInspectionId: {
    type: Number,
    default: null,
  },
  initialMode: {
    type: String,
    default: 'select',
  },
})

const router = useRouter()

const registrationMethods = [
  {
    key: 'inspection-pick',
    title: '사전 검수 상품 등록',
    points: [
      '검수 완료된 상품만 골라 바로 경매에 등록할 수 있습니다.',
      '검수 정보와 등급이 함께 노출되어 입찰 신뢰도가 높습니다.',
      '상품 선택 후 바로 2단계 경매 등록으로 이어집니다.',
    ],
    buttonLabel: '검수 상품 등록하기',
    cardClass: 'is-inspection',
    buttonClass: 'is-inspection',
    icon: 'mdi-shield-check-outline',
    bulletIcon: 'mdi-check-circle',
  },
  {
    key: 'direct',
    title: '직접 상품 등록',
    points: [
      '판매자가 직접 사진 설명 및 상품 정보를 입력합니다.',
      '검수 절차 없이 빠르게 상품을 등록할 수 있습니다.',
      '낙찰 후 구매자와 직접 배송 및 거래를 진행합니다.',
    ],
    buttonLabel: '직접 등록하기',
    cardClass: 'is-direct',
    buttonClass: 'is-direct',
    icon: 'mdi-package-variant-closed-plus',
    bulletIcon: 'mdi-information-outline',
  },
]

const {
  auctionForm,
  bidUnitOptions,
  categoryOptions,
  closeInspectionDetail,
  currentMode,
  durationOptions,
  errorMessage,
  firstStepLabel,
  filteredInspectionPickItems,
  form,
  goBackToSelect,
  handleFiles,
  headerDescription,
  headerTitle,
  inspectionPickItems,
  isAuctionStep,
  isInspectionDetailOpen,
  openInspectionRequest,
  openMode,
  processing,
  removeImage,
  returnFromAuctionStep,
  selectedBidUnit,
  selectedDuration,
  selectedInspectionId,
  selectedInspectionItem,
  inspectionSearchQuery,
  selectInspectionItem,
  setInspectionSearchQuery,
  setAuctionStartDate,
  setAuctionStartTime,
  setPrimaryImage,
  showStepper,
  startAuctionFromInspection,
  submitted,
  submitForm,
  successMessage,
  thumbnailPlaceholders,
  toggleAuctionField,
  uploadInProgress,
  uploadedImages,
} = useRegisterFlow(toRef(props, 'initialMode'), toRef(props, 'initialInspectionId'))

async function handleSubmit() {
  const result = await submitForm()

  if (result?.type === 'auction' && result.auctionId) {
    await router.replace(`/auctions/${result.auctionId}`)
    return
  }

  if (result?.type === 'inspection') {
    await router.replace('/inspection')
  }
}
</script>

<template>
  <section class="register-screen">
    <header class="register-method-header">
      <h2>{{ headerTitle }}</h2>
      <p>{{ headerDescription }}</p>
    </header>

    <RegisterStepper
      v-if="showStepper"
      :first-step-label="firstStepLabel"
      :is-auction-step="isAuctionStep"
    />

    <RegisterMethodSelect
      v-if="currentMode === 'select'"
      :methods="registrationMethods"
      @select-mode="openMode"
    />

    <InspectionPickStep
      v-else-if="currentMode === 'inspection-pick'"
      :assets="assets"
      :error-message="errorMessage"
      :inspection-pick-items="filteredInspectionPickItems"
      :inspection-product-image="inspectionProductImage"
      :inspection-search-query="inspectionSearchQuery"
      :is-inspection-detail-open="isInspectionDetailOpen"
      :selected-inspection-id="selectedInspectionId"
      :selected-inspection-item="selectedInspectionItem"
      @close-detail="closeInspectionDetail"
      @open-inspection-request="openInspectionRequest"
      @select-item="selectInspectionItem"
      @update:inspection-search-query="setInspectionSearchQuery"
      @start-auction="startAuctionFromInspection"
    />

    <RegisterProductForm
      v-else-if="currentMode === 'inspection' || currentMode === 'direct'"
      :category-options="categoryOptions"
      :current-mode="currentMode"
      :error-message="errorMessage"
      :form="form"
      :processing="processing"
      :submitted="submitted"
      :success-message="successMessage"
      :thumbnail-placeholders="thumbnailPlaceholders"
      :upload-in-progress="uploadInProgress"
      :uploaded-images="uploadedImages"
      @cancel="goBackToSelect"
      @files-selected="handleFiles"
      @remove-image="removeImage"
      @set-primary-image="setPrimaryImage"
      @submit="handleSubmit"
    />

    <AuctionSetupForm
      v-else
      :auction-form="auctionForm"
      :bid-unit-options="bidUnitOptions"
      :duration-options="durationOptions"
      :error-message="errorMessage"
      :processing="processing"
      :selected-bid-unit="selectedBidUnit"
      :selected-duration="selectedDuration"
      :submitted="submitted"
      :success-message="successMessage"
      @cancel="returnFromAuctionStep"
      @select-bid-unit="selectedBidUnit = $event"
      @select-duration="selectedDuration = $event"
      @set-start-date="setAuctionStartDate"
      @set-start-time="setAuctionStartTime"
      @submit="handleSubmit"
      @toggle-field="toggleAuctionField"
    />
  </section>
</template>
