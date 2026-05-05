<template>
  <BaseModal
    v-if="mode === 'detail'"
    panel-class="purchase-modal purchase-modal--detail"
    @close="$emit('close')"
  >
    <div class="purchase-modal__grid">
      <img :src="item.image || watchImage" :alt="item.name" class="purchase-modal__image" />

      <div class="purchase-modal__summary">
        <StatusBadge :status="item.status" />
        <h2>{{ item.name }}</h2>
        <div class="purchase-price-box">
          <p>낙찰가</p>
          <strong>{{ item.price }}</strong>
        </div>
      </div>

      <div class="purchase-info-block">
        <h3>배송지 정보</h3>
        <div
          v-if="item.shippingAddress"
          class="purchase-info-card"
          :class="{ 'purchase-info-card--alert': !item.shippingAddress.name }"
        >
          <template v-if="item.shippingAddress.name">
            <p class="purchase-info-card__label">연락처</p>
            <strong>{{ item.shippingAddress.name }}</strong>
            <strong>{{ item.shippingAddress.phone }}</strong>
            <p class="purchase-info-card__label purchase-info-card__label--spacing">주소</p>
            <strong>{{ item.shippingAddress.zip }}</strong>
            <strong>{{ item.shippingAddress.address1 }}</strong>
            <strong>{{ item.shippingAddress.address2 }}</strong>
          </template>
          <p v-else>배송지 정보를 등록해 주세요.</p>
        </div>

        <div v-else class="purchase-info-card purchase-info-card--alert winnerdeal-info-card--alert">
          <p>배송지 정보를 등록해 주세요.</p>
        </div>
      </div>

      <div class="purchase-info-block">
        <h3>배송 정보</h3>
        <div v-if="item.shippingInfo" class="purchase-info-card">
          <strong>{{ item.shippingInfo.courier || '-' }}</strong>
          <strong>{{ item.shippingInfo.trackingNumber || '-' }}</strong>
        </div>

        <div
          v-else-if="variant === 'sale' && item.modalType === 'seller-needs-shipping-info'"
          class="purchase-info-card purchase-info-card--alert winnerdeal-info-card--alert"
        >
          <p>배송 정보를 등록해 주세요.</p>
        </div>
      </div>
    </div>

    <div v-if="showActions" class="purchase-modal__actions">
      <button class="secondary-button purchase-modal__action" type="button" @click="$emit('close')">
        취소
      </button>
      <button
        v-if="variant === 'purchase' && item.modalType === 'pending-no-address'"
        class="primary-button purchase-modal__action"
        type="button"
        :disabled="saving"
        @click="$emit('next', 'shipping-form')"
      >
        배송지 정보 등록
      </button>

      <button
        v-else-if="variant === 'purchase' && item.modalType === 'delivery-complete-awaiting-confirm'"
        class="primary-button purchase-modal__action"
        type="button"
        :disabled="saving"
        @click="$emit('confirm-purchase')"
      >
        구매 확정
      </button>

      <button
        v-else-if="variant === 'purchase' && item.canWriteReview"
        class="primary-button purchase-modal__action"
        type="button"
        :disabled="saving"
        @click="$emit('next', 'review-form')"
      >
        리뷰 작성하기
      </button>

      <button
        v-else-if="variant === 'sale' && item.modalType === 'seller-needs-shipping-info'"
        class="primary-button purchase-modal__action"
        type="button"
        :disabled="saving"
        @click="$emit('next', 'shipping-form')"
      >
        배송 정보 등록
      </button>
    </div>
  </BaseModal>

  <BaseModal
    v-else-if="variant === 'purchase' && mode === 'shipping-form'"
    header-label="배송지 정보 등록"
    panel-class="purchase-modal purchase-modal--form"
    @close="$emit('close')"
  >
    <div class="purchase-form">
      <label>
        <span>이름 <em>*</em></span>
        <input
          :value="form.name"
          type="text"
          placeholder="수령인을 입력해 주세요."
          @input="emitForm('name', $event.target.value)"
        />
      </label>
      <label>
        <span>전화번호 <em>*</em></span>
        <input
          :value="form.phone"
          type="text"
          placeholder="연락처를 입력해 주세요."
          @input="emitForm('phone', $event.target.value)"
        />
      </label>
      <label>
        <span>주소 <em>*</em></span>
        <div class="purchase-address-preview">
          <div>
            <strong>{{ form.zip || '배송지를 선택해 주세요.' }}</strong>
            <p>{{ form.address1 }}</p>
            <p>{{ form.address2 }}</p>
          </div>
          <button
            class="ghost-button purchase-address-preview__change"
            type="button"
            @click="$emit('next', 'address-book')"
          >
            변경
          </button>
        </div>
      </label>
    </div>

    <button class="primary-button purchase-form__submit" type="button" :disabled="saving" @click="$emit('save-address')">
      {{ saving ? '등록 중...' : '배송지 정보 등록' }}
    </button>
  </BaseModal>

  <BaseModal
    v-else-if="variant === 'purchase' && mode === 'review-form'"
    header-label="리뷰 작성하기"
    panel-class="purchase-modal purchase-modal--form"
    @close="$emit('close')"
  >
    <div class="purchase-form purchase-review-form">
      <label>
        <span>평점 <em>*</em></span>
        <div class="purchase-review-rating" role="radiogroup" aria-label="평점 선택">
          <div
            v-for="star in ratingStars"
            :key="star"
            class="purchase-review-rating__star-wrap"
          >
            <button
              class="purchase-review-rating__button"
              type="button"
              :aria-label="`${star}점`"
              @click="setRating(star)"
            >
              <v-icon
                :icon="getRatingIcon(star)"
                :color="getRatingColor(star)"
                size="36"
              />
            </button>
          </div>
        </div>
      </label>

      <label>
        <span>리뷰 내용</span>
        <textarea
          :value="form.content"
          rows="6"
          placeholder="거래 후기를 입력해 주세요."
          @input="emitForm('content', $event.target.value)"
        />
      </label>
    </div>

    <div class="purchase-modal__actions purchase-modal__actions--stacked">
      <button class="secondary-button purchase-modal__action" type="button" @click="$emit('back', 'detail')">
        닫기
      </button>
      <button class="primary-button purchase-modal__action" type="button" :disabled="saving" @click="$emit('submit-review')">
        {{ saving ? '작성 중...' : '리뷰 작성 완료' }}
      </button>
    </div>
  </BaseModal>

  <BaseModal
    v-else-if="variant === 'purchase' && mode === 'address-book'"
    panel-class="purchase-modal purchase-modal--address-book"
    @close="$emit('close')"
  >
    <template #action>
      <button class="base-modal__close" type="button" @click="$emit('back', 'shipping-form')">
        <v-icon icon="mdi-close" />
      </button>
    </template>

    <AddressBookPanel
      :addresses="addresses"
      :empty-message="'등록된 배송지가 없습니다.'"
      :error-message="addressBookErrorMessage"
      :loading="addressBookLoading"
      :loading-message="'배송지 목록을 불러오는 중입니다.'"
      :selectable="true"
      :show-add-button="true"
      :show-management-actions="false"
      @add="$emit('next', 'address-add')"
      @select="$emit('select-address', $event)"
    />
  </BaseModal>

  <AddressAddModal
    v-else-if="variant === 'purchase' && mode === 'address-add'"
    :error-message="addressBookErrorMessage"
    :open="true"
    :saving="addressBookSaving"
    :suggest-primary="addresses.length === 0"
    @close="$emit('back', 'address-book')"
    @submit="$emit('create-address', $event)"
  />

  <BaseModal
    v-else-if="variant === 'sale' && mode === 'shipping-form'"
    header-label="배송 정보 등록"
    panel-class="purchase-modal purchase-modal--form"
    @close="$emit('close')"
  >
    <div class="purchase-form sales-form">
      <label>
        <span>택배사 <em>*</em></span>
        <v-menu location="bottom start" offset="8">
          <template #activator="{ props: menuProps }">
            <button
              v-bind="menuProps"
              class="winnerdeal-courier-select"
              type="button"
            >
              <span>{{ form.courier || '택배사를 선택해 주세요.' }}</span>
              <v-icon icon="mdi-chevron-down" />
            </button>
          </template>

          <div class="winnerdeal-courier-menu">
            <button
              v-for="option in courierOptions"
              :key="option"
              class="winnerdeal-courier-menu__item"
              type="button"
              @click="emitForm('courier', option)"
            >
              {{ option }}
            </button>
          </div>
        </v-menu>
      </label>

      <label>
        <span>운송장 번호 <em>*</em></span>
        <input
          :value="form.trackingNumber"
          type="text"
          placeholder="운송장 번호를 입력해 주세요."
          @input="emitForm('trackingNumber', $event.target.value)"
        />
      </label>
    </div>

    <button class="primary-button purchase-form__submit" type="button" :disabled="saving" @click="$emit('save-shipping')">
      {{ saving ? '등록 중...' : '등록하기' }}
    </button>
  </BaseModal>
</template>

<script setup>
import { computed, watch } from 'vue'
import BaseModal from '../../BaseModal.vue'
import AddressAddModal from '../addresses/AddressAddModal.vue'
import AddressBookPanel from '../addresses/AddressBookPanel.vue'
import StatusBadge from '../cards/StatusBadge.vue'
import { useToast } from '../../../composables/useToast'
import { watchImage } from '../../../data/mypage'
import { courierOptions } from '../../../data/salesHistory'

const props = defineProps({
  addresses: {
    type: Array,
    default: () => [],
  },
  addressBookErrorMessage: {
    type: String,
    default: '',
  },
  addressBookLoading: {
    type: Boolean,
    default: false,
  },
  addressBookSaving: {
    type: Boolean,
    default: false,
  },
  deletingId: {
    type: [String, Number],
    default: null,
  },
  errorMessage: {
    type: String,
    default: '',
  },
  form: {
    type: Object,
    required: true,
  },
  item: {
    type: Object,
    required: true,
  },
  mode: {
    type: String,
    required: true,
  },
  saving: {
    type: Boolean,
    default: false,
  },
  settingDefaultId: {
    type: [String, Number],
    default: null,
  },
  variant: {
    type: String,
    required: true,
    validator: (value) => ['purchase', 'sale'].includes(value),
  },
})

const emit = defineEmits([
  'back',
  'close',
  'confirm-purchase',
  'create-address',
  'delete-address',
  'next',
  'save-address',
  'save-shipping',
  'select-address',
  'set-default-address',
  'submit-review',
  'update-form',
])

const { showToast } = useToast()

const addresses = computed(() => props.addresses)
const ratingStars = [1, 2, 3, 4, 5]

const showActions = computed(() => {
  if (props.variant === 'purchase') {
    return (
      props.item.modalType === 'pending-no-address' ||
      props.item.modalType === 'delivery-complete-awaiting-confirm' ||
      Boolean(props.item.canWriteReview)
    )
  }

  return props.item.modalType === 'seller-needs-shipping-info'
})

function emitForm(field, value) {
  emit('update-form', field, value)
}

function normalizeRatingValue(value) {
  const parsedValue = Number(value)

  if (Number.isNaN(parsedValue)) {
    return 5
  }

  return Math.min(5, Math.max(1, Math.round(parsedValue)))
}

function setRating(value) {
  emitForm('rating', normalizeRatingValue(value))
}

function getRatingIcon(star) {
  return Number(props.form?.rating || 0) >= star ? 'mdi-star' : 'mdi-star-outline'
}

function getRatingColor(star) {
  return Number(props.form?.rating || 0) >= star ? 'primary' : 'grey'
}

watch(
  () => props.errorMessage,
  (message) => {
    if (message) {
      showToast(message, { color: 'error' })
    }
  },
)

watch(
  () => props.addressBookErrorMessage,
  (message) => {
    if (message && !props.addressBookLoading) {
      showToast(message, { color: 'error' })
    }
  },
)
</script>
