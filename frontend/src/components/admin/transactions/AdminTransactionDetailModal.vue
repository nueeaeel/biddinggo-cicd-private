<template>
  <div class="modal-backdrop admin-modal-backdrop" @click.self="$emit('close')">
    <section class="admin-transaction-modal">
      <header class="admin-transaction-modal__head">
        <button class="admin-transaction-modal__close" type="button" @click="$emit('close')">
          <v-icon icon="mdi-close" />
        </button>
      </header>

      <div class="admin-transaction-modal__grid">
        <div class="admin-transaction-modal__image-wrap">
          <img :src="item.image" :alt="item.productTitle" />
        </div>

        <section class="admin-transaction-modal__summary">
          <p>{{ item.category }}</p>
          <AdminStatusBadge :status="item.status" />
          <h2>{{ item.productTitle }}</h2>
          <div class="admin-transaction-modal__price">
            <span>낙찰가</span>
            <strong>{{ item.finalBid }}</strong>
          </div>
        </section>

        <section class="admin-transaction-modal__address">
          <h3>배송지 정보</h3>
          <div class="admin-transaction-modal__address-card">
            <div>
              <span>연락처</span>
              <p>{{ item.shippingAddress.name }}</p>
              <p>{{ item.shippingAddress.phone }}</p>
            </div>
            <div>
              <span>주소</span>
              <p>{{ item.shippingAddress.zip }}</p>
              <p>{{ item.shippingAddress.address1 }}</p>
              <p>{{ item.shippingAddress.address2 }}</p>
            </div>
          </div>
        </section>

        <section class="admin-transaction-modal__shipping">
          <h3>배송 정보</h3>
          <div v-if="item.shippingInfo" class="admin-transaction-modal__shipping-card">
            <span>택배사</span>
            <p>{{ item.shippingInfo.carrier }}</p>
            <span>송장 번호</span>
            <p>{{ item.shippingInfo.trackingNumber }}</p>
          </div>
          <div v-else class="admin-transaction-modal__shipping-alert">
            <v-icon icon="mdi-alert-circle-outline" />
            <p>배송 정보를 등록해주세요</p>
          </div>
        </section>
      </div>

      <footer class="admin-transaction-modal__actions">
        <button class="secondary-button" type="button" @click="$emit('close')">취소</button>
        <button
          v-if="showShippingRegisterButton"
          class="primary-button"
          type="button"
          @click="$emit('open-shipping')"
        >
          배송 정보 등록
        </button>
      </footer>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import AdminStatusBadge from '../shared/AdminStatusBadge.vue'

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
})

defineEmits(['close', 'open-shipping'])

function isBlank(value) {
  return value === null || value === undefined || String(value).trim() === ''
}

const showShippingRegisterButton = computed(() => {
  const isInspection =
    Boolean(props.item?.inspectionItem) ||
    String(props.item?.inspectionYn || '').trim().toUpperCase() === 'YES'
  const missingCarrier = isBlank(props.item?.carrier)
  const missingTrackingNumber = isBlank(props.item?.trackingNumber)

  return isInspection && missingCarrier && missingTrackingNumber
})
</script>
