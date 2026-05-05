<script setup>
import InspectionDetailModal from './InspectionDetailModal.vue'

defineProps({
  assets: {
    type: Object,
    required: true,
  },
  errorMessage: {
    type: String,
    default: '',
  },
  inspectionPickItems: {
    type: Array,
    required: true,
  },
  inspectionSearchQuery: {
    type: String,
    default: '',
  },
  inspectionProductImage: {
    type: String,
    required: true,
  },
  isInspectionDetailOpen: {
    type: Boolean,
    required: true,
  },
  selectedInspectionId: {
    type: Number,
    required: true,
  },
  selectedInspectionItem: {
    type: Object,
    default: null,
  },
})

defineEmits(['close-detail', 'open-inspection-request', 'select-item', 'start-auction', 'update:inspection-search-query'])
</script>

<template>
  <section class="register-form-card register-inspection-pick-card">
    <div class="inspection-toolbar register-inspection-toolbar">
      <div class="inspection-search">
        <v-icon icon="mdi-magnify" class="inspection-search-icon" size="18" />
        <input
          :value="inspectionSearchQuery"
          type="search"
          class="inspection-search-input"
          placeholder="상품명, 브랜드 검색"
          @input="$emit('update:inspection-search-query', $event.target.value)"
        />
      </div>
    </div>

    <div v-if="errorMessage" class="register-success-banner is-error">
      {{ errorMessage }}
    </div>

    <div class="inspection-card-grid register-inspection-grid">
      <article
        v-for="item in inspectionPickItems"
        :key="`${item.title}-${item.displayId}`"
        class="inspection-product-card register-inspection-card"
        :class="{ 'is-selected': selectedInspectionId === item.displayId }"
        @click="$emit('select-item', item.displayId)"
      >
        <div class="inspection-product-image-wrap">
          <img :src="item.image || inspectionProductImage" :alt="item.title" class="inspection-product-image" />
        </div>

        <div class="inspection-product-body">
          <span class="register-inspection-status">검수 통과</span>
          <h3>{{ item.title }}</h3>
          <div class="inspection-product-meta">
            <div>
              <span>검수 등급</span>
              <strong>{{ item.inspectionGrade }}</strong>
            </div>
            <span>{{ item.inspectionDate }}</span>
          </div>
        </div>
      </article>

      <button type="button" class="register-empty-card" @click="$emit('open-inspection-request')">
        <div class="register-empty-icon"><v-icon icon="mdi-plus" size="28" /></div>
        <strong>상품이 없으신가요?</strong>
        <p>신규 사전 검수 상품을 등록해보세요.</p>
      </button>
    </div>

    <InspectionDetailModal
      v-if="isInspectionDetailOpen && selectedInspectionItem"
      :inspection-product-image="inspectionProductImage"
      :item="selectedInspectionItem"
      @close="$emit('close-detail')"
      @start-auction="$emit('start-auction')"
    />
  </section>
</template>
