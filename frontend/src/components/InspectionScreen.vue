<script setup>
import { toRef } from 'vue'
import { useInspectionState } from '../composables/useInspectionState'
import InspectionDetailModal from './inspection/InspectionDetailModal.vue'
import InspectionProductGrid from './inspection/InspectionProductGrid.vue'
import InspectionShippingModal from './inspection/InspectionShippingModal.vue'
import InspectionSummaryGrid from './inspection/InspectionSummaryGrid.vue'
import InspectionToolbar from './inspection/InspectionToolbar.vue'

const emit = defineEmits(['open-register', 'open-auction-register', 'submit-shipping'])

const props = defineProps({
  assets: {
    type: Object,
    required: true,
  },
  items: {
    type: Array,
    required: true,
  },
  summary: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  errorMessage: {
    type: String,
    default: '',
  },
})

const {
  activeFilter,
  badgeClass,
  closeDetail,
  closeShippingModal,
  detailActionLabel,
  filterOptions,
  filteredItems,
  handleDetailAction,
  isShippingModalOpen,
  openDetail,
  searchQuery,
  selectedItem,
  shippingForm,
  submitShippingInfo,
} = useInspectionState(toRef(props, 'items'), {
  onAuctionRegister: (item) => emit('open-auction-register', item),
  onShippingSubmit: (item, form) => emit('submit-shipping', { item, form }),
})
</script>

<template>
  <section class="inspection-screen">
    <div class="inspection-header">
      <div>
        <h2>사전 검수 상품</h2>
        <p>검수가 완료된 상품을 확인하고 경매에 등록하세요.</p>
      </div>
      <button type="button" class="inspection-action" @click="$emit('open-register', 'inspection')">
        <span class="inspection-action-plus">+</span>
        검수 신청하기
      </button>
    </div>

    <InspectionSummaryGrid :summary="summary" />

    <InspectionToolbar
      :active-filter="activeFilter"
      :filter-options="filterOptions"
      :search-query="searchQuery"
      @update:active-filter="activeFilter = $event"
      @update:search-query="searchQuery = $event"
    />

    <div v-if="errorMessage" class="feedback-strip is-error">{{ errorMessage }}</div>
    <div v-else-if="loading" class="feedback-strip">사전 검수 상품을 불러오는 중입니다.</div>

    <InspectionProductGrid
      :assets="assets"
      :badge-class="badgeClass"
      :items="filteredItems"
      @open-detail="openDetail"
    />

    <InspectionDetailModal
      v-if="selectedItem"
      :assets="assets"
      :badge-class="badgeClass"
      :detail-action-label="detailActionLabel"
      :item="selectedItem"
      @close="closeDetail"
      @handle-action="handleDetailAction"
    />

    <InspectionShippingModal
      v-if="isShippingModalOpen"
      :form="shippingForm"
      @close="closeShippingModal"
      @submit="submitShippingInfo"
    />
  </section>
</template>

<style scoped>
.feedback-strip {
  margin: 14px 0;
  border-radius: 14px;
  background: #fff;
  padding: 14px 15px;
  color: #64748b;
  font-size: 11px;
  text-align: center;
}

.feedback-strip.is-error {
  background: #fef2f2;
  color: #b91c1c;
}
</style>
