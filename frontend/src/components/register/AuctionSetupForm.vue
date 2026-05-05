<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  auctionForm: {
    type: Object,
    required: true,
  },
  bidUnitOptions: {
    type: Array,
    required: true,
  },
  durationOptions: {
    type: Array,
    required: true,
  },
  errorMessage: {
    type: String,
    default: '',
  },
  processing: {
    type: Boolean,
    default: false,
  },
  selectedBidUnit: {
    type: String,
    required: true,
  },
  selectedDuration: {
    type: String,
    required: true,
  },
  submitted: {
    type: Boolean,
    required: true,
  },
  successMessage: {
    type: String,
    required: true,
  },
  cancelLabel: {
    type: String,
    default: '취소',
  },
  canUseTimeDeal: {
    type: Boolean,
    default: true,
  },
  showTypeToggles: {
    type: Boolean,
    default: true,
  },
  submitLabel: {
    type: String,
    default: '확인',
  },
})

const emit = defineEmits(['cancel', 'select-bid-unit', 'select-duration', 'set-start-date', 'set-start-time', 'submit', 'toggle-field'])
const isStartPickerOpen = ref(false)
const tempStartDate = ref('')
const tempStartTime = ref('')

function syncTempStartDateTime() {
  tempStartDate.value = props.auctionForm.startDateInput || ''
  tempStartTime.value = props.auctionForm.startTimeInput || ''
}

function openStartPicker() {
  if (!props.auctionForm.timeDeal) {
    return
  }

  syncTempStartDateTime()
  isStartPickerOpen.value = true
}

function closeStartPicker() {
  isStartPickerOpen.value = false
}

function confirmStartPicker() {
  emit('set-start-date', tempStartDate.value)
  emit('set-start-time', tempStartTime.value)
  closeStartPicker()
}

watch(
  () => [props.auctionForm.startDateInput, props.auctionForm.startTimeInput],
  () => {
    if (!isStartPickerOpen.value) {
      syncTempStartDateTime()
    }
  },
  { immediate: true },
)
</script>

<template>
  <section class="register-form-card register-auction-card">
    <div class="auction-settings">
      <div v-if="showTypeToggles" class="auction-toggle-grid">
        <article class="auction-toggle-card">
          <div class="auction-toggle-copy">
            <div class="auction-toggle-icon is-extend">↻</div>
            <div>
              <strong>연장 경매</strong>
              <p>종료 직전 입찰 시 시간 연장</p>
            </div>
          </div>
          <button
            type="button"
            class="auction-switch"
            :class="{ 'is-on': auctionForm.extendAuction }"
            @click="$emit('toggle-field', 'extendAuction')"
          >
            <span />
          </button>
        </article>

        <article v-if="canUseTimeDeal" class="auction-toggle-card">
          <div class="auction-toggle-copy">
            <div class="auction-toggle-icon is-timedeal">⚡</div>
            <div>
              <strong>타임딜</strong>
              <p>단시간 내 고가가 낙찰 유도</p>
            </div>
          </div>
          <button
            type="button"
            class="auction-switch"
            :class="[{ 'is-on': auctionForm.timeDeal }, 'is-danger']"
            @click="$emit('toggle-field', 'timeDeal')"
          >
            <span />
          </button>
        </article>
      </div>

      <div class="auction-field-grid">
        <label class="register-field">
          <span class="register-label">경매 시작가 <span>*</span></span>
          <div class="auction-money-field">
            <input v-model="auctionForm.startPrice" type="text" inputmode="numeric" placeholder="0" />
            <span>원</span>
          </div>
        </label>

        <label class="register-field">
          <span class="register-label">즉시 구매가</span>
          <div class="auction-money-field">
            <input v-model="auctionForm.buyNowPrice" type="text" inputmode="numeric" placeholder="0" />
            <span>원</span>
          </div>
        </label>
      </div>

      <div class="auction-option-section">
        <span class="register-label">입찰 단위 <span>*</span></span>
        <div class="auction-chip-row">
          <button
            v-for="option in bidUnitOptions"
            :key="option"
            type="button"
            class="auction-chip"
            :class="{ 'is-selected': selectedBidUnit === option }"
            @click="$emit('select-bid-unit', option)"
          >
            {{ option }}
          </button>
        </div>
      </div>

      <div class="auction-option-section">
        <span class="register-label">경매 기간 <span>*</span></span>

        <div class="auction-date-grid">
          <div
            class="auction-date-card"
            :class="{ 'is-clickable': auctionForm.timeDeal }"
            role="button"
            tabindex="0"
            @click="openStartPicker"
            @keydown.enter.prevent="openStartPicker"
            @keydown.space.prevent="openStartPicker"
          >
            <span class="auction-date-label">시작 일시</span>
            <div class="auction-date-value-row">
              <div class="auction-date-value">🗓 {{ auctionForm.startDate }}</div>
              <div class="auction-date-value">🕒 {{ auctionForm.startTime }}</div>
            </div>
          </div>

          <div class="auction-date-card">
            <span class="auction-date-label">종료 일시</span>
            <div class="auction-date-value-row">
              <div class="auction-date-value">🗓 {{ auctionForm.endDate }}</div>
              <div class="auction-date-value">🕒 {{ auctionForm.endTime }}</div>
            </div>
          </div>
        </div>

        <div class="auction-chip-row is-duration">
          <button
            v-for="option in durationOptions"
            :key="option"
            type="button"
            class="auction-chip"
            :class="{ 'is-selected': selectedDuration === option }"
            @click="$emit('select-duration', option)"
          >
            {{ option }}
          </button>
        </div>
      </div>

      <div v-if="errorMessage" class="register-success-banner is-error">
        {{ errorMessage }}
      </div>

      <div v-else-if="submitted" class="register-success-banner">
        {{ successMessage }}
      </div>

      <div class="register-actions">
        <button type="button" class="register-secondary-button" :disabled="processing" @click="$emit('cancel')">
          {{ cancelLabel }}
        </button>
        <button type="button" class="register-primary-button" :disabled="processing" @click="$emit('submit')">
          {{ processing ? '처리 중...' : submitLabel }}
        </button>
      </div>
    </div>
  </section>

  <div v-if="isStartPickerOpen" class="auction-picker-overlay" @click.self="closeStartPicker">
    <section class="auction-picker-modal">
      <header class="auction-picker-header">
        <div>
          <strong>타임딜 시작 일시</strong>
          <p>시작 날짜와 시간을 선택해주세요.</p>
        </div>
        <button type="button" class="auction-picker-close" @click="closeStartPicker">×</button>
      </header>

      <div class="auction-picker-fields">
        <label class="auction-picker-field">
          <span>시작 날짜</span>
          <input v-model="tempStartDate" type="date" class="auction-start-input" />
        </label>
        <label class="auction-picker-field">
          <span>시작 시간</span>
          <input v-model="tempStartTime" type="time" class="auction-start-input" />
        </label>
      </div>

      <div class="auction-picker-actions">
        <button type="button" class="register-secondary-button" @click="closeStartPicker">취소</button>
        <button
          type="button"
          class="register-primary-button"
          :disabled="!tempStartDate || !tempStartTime"
          @click="confirmStartPicker"
        >
          적용
        </button>
      </div>
    </section>
  </div>
</template>
