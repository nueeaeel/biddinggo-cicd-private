<template>
  <BaseModal
    :open="open"
    panel-class="purchase-modal point-modal"
    :header-label="title"
    @close="$emit('close')"
  >
    <form v-if="mode === 'charge-details'" class="point-action-modal" @submit.prevent="submitChargeDetails">
      <section class="point-balance-card point-balance-card--amount">
        <span>충전 금액</span>
        <strong>{{ formatAmount(amount) }}</strong>
      </section>

      <div class="point-detail-field">
        <span>입금할 은행</span>
        <v-menu location="bottom start" offset="8">
          <template #activator="{ props: menuProps }">
            <button class="point-bank-menu__trigger" type="button" v-bind="menuProps">
              <span>{{ selectedBankLabel }}</span>
              <v-icon icon="mdi-chevron-down" />
            </button>
          </template>

          <div class="point-bank-menu__panel">
            <button
              v-for="bank in selectableBanks"
              :key="bank.code"
              class="point-bank-menu__item"
              :class="{ 'point-bank-menu__item--active': selectedBank === bank.code }"
              type="button"
              @click="selectBank(bank.code)"
            >
              {{ bank.label }}
            </button>
          </div>
        </v-menu>
      </div>

      <p class="point-detail-note">선택한 은행으로 가상계좌를 발급합니다.</p>

      <button class="primary-button point-action-modal__submit" type="submit" :disabled="!selectedBank">
        가상계좌 발급하기
      </button>
    </form>

    <div v-else-if="mode === 'virtual-account'" class="point-action-modal point-action-modal--account">
      <section class="point-account-banner">
        <v-icon class="point-account-banner__icon" icon="mdi-credit-card-outline" />
        <div>
          <strong>가상계좌 발급 완료</strong>
          <p>아래 계좌로 입금하시면 자동으로 충전됩니다.</p>
        </div>
      </section>

      <dl class="point-account-list">
        <div class="point-account-row">
          <dt>은행</dt>
          <dd>{{ virtualAccount.bank }}</dd>
        </div>
        <div class="point-account-row">
          <dt>계좌번호</dt>
          <dd>{{ virtualAccount.accountNumber }}</dd>
        </div>
        <div class="point-account-row">
          <dt>예금주</dt>
          <dd>{{ virtualAccount.accountHolder }}</dd>
        </div>
        <div class="point-account-row point-account-row--amount">
          <dt>입금 금액</dt>
          <dd>{{ virtualAccount.depositAmount }}</dd>
        </div>
        <div class="point-account-row point-account-row--deadline">
          <dt>입금 기한</dt>
          <dd>{{ virtualAccount.dueAt }}</dd>
        </div>
      </dl>

      <button class="primary-button point-action-modal__submit" type="button" @click="$emit('confirm')">확인</button>
    </div>

    <div v-else class="point-action-modal">
      <section class="point-balance-card">
        <span>현재 보유 포인트</span>
        <strong>{{ formatAmount(currentPoints) }}</strong>
      </section>

      <section class="point-balance-card point-balance-card--amount">
        <span>{{ amountLabel }}</span>
        <strong>{{ formatAmount(amount) }}</strong>
      </section>

      <div class="point-quick-actions">
        <button
          v-for="preset in presets"
          :key="String(preset)"
          class="chip point-quick-actions__chip"
          :class="{ active: selectedPreset === preset }"
          type="button"
          @click="$emit('preset', preset)"
        >
          {{ formatPreset(preset) }}
        </button>
      </div>

      <div class="point-summary-row">
        <span>{{ expectedLabel }}</span>
        <strong>{{ formatAmount(expectedPoints) }}</strong>
      </div>

      <button class="primary-button point-action-modal__submit" type="button" :disabled="amount <= 0" @click="$emit('submit')">
        {{ actionLabel }}
      </button>
    </div>
  </BaseModal>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import BaseModal from '../../BaseModal.vue'
import { bankOptions } from '../../../utils/banks'

const props = defineProps({
  actionLabel: { type: String, default: '' },
  amount: { type: Number, default: 0 },
  amountLabel: { type: String, default: '' },
  currentPoints: { type: Number, default: 0 },
  expectedLabel: { type: String, default: '' },
  expectedPoints: { type: Number, default: 0 },
  mode: { type: String, default: '' },
  open: { type: Boolean, default: false },
  presets: { type: Array, default: () => [] },
  selectedPreset: { type: [Number, String], default: null },
  title: { type: String, default: '' },
  virtualAccount: { type: Object, default: () => ({}) },
})

const emit = defineEmits(['close', 'confirm', 'preset', 'submit', 'submit-charge-details'])

const selectedBank = ref('020')
const selectableBanks = computed(() => bankOptions.filter((bank) => bank.code))
const selectedBankLabel = computed(() => selectableBanks.value.find((bank) => bank.code === selectedBank.value)?.label || '은행 선택')

watch(
  () => props.mode,
  (mode) => {
    if (mode === 'charge-details') {
      selectedBank.value = selectedBank.value || '020'
    }
  },
)

function formatAmount(value) {
  return `${Number(value || 0).toLocaleString('ko-KR')} 원`
}

function formatPreset(preset) {
  if (preset === 'all') return '전액'
  return `+ ${(preset / 10000).toLocaleString('ko-KR')}만`
}

function selectBank(bankCode) {
  selectedBank.value = bankCode
}

function submitChargeDetails() {
  emit('submit-charge-details', {
    bank: selectedBank.value,
  })
}
</script>
