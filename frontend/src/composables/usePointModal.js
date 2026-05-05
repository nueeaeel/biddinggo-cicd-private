import { computed, ref } from 'vue'
import { getBankLabel } from '../utils/banks'

const INITIAL_POINTS = 1850000

const CHARGE_PRESETS = [10000, 50000, 100000, 500000]
const WITHDRAW_PRESETS = [10000, 50000, 100000, 'all']

function formatAmount(value, suffix = '원') {
  return `${Number(value || 0).toLocaleString('ko-KR')} ${suffix}`
}

function formatTimestamp(date = new Date()) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')

  return `${year}.${month}.${day} ${hours}:${minutes}`
}

export function usePointModal(options = {}) {
  const currentPoints = options.currentPoints ?? ref(INITIAL_POINTS)
  const history = options.history ?? ref([])
  const onCharge = options.onCharge
  const onWithdraw = options.onWithdraw
  const modalMode = ref('')
  const amount = ref(0)
  const selectedPreset = ref(null)
  const issuedVirtualAccount = ref(null)

  const presets = computed(() => (modalMode.value === 'withdraw' ? WITHDRAW_PRESETS : CHARGE_PRESETS))
  const modalTitle = computed(() => {
    if (modalMode.value === 'withdraw') return '포인트 인출'
    if (modalMode.value === 'charge-details' || modalMode.value === 'charge' || modalMode.value === 'virtual-account') return '포인트 충전'
    return ''
  })
  const actionLabel = computed(() => (modalMode.value === 'withdraw' ? '인출하기' : '충전하기'))
  const amountLabel = computed(() => (modalMode.value === 'withdraw' ? '인출할 포인트' : '충전할 금액'))
  const expectedLabel = computed(() => (modalMode.value === 'withdraw' ? '인출 후 예상 포인트' : '충전 후 예상 포인트'))
  const expectedPoints = computed(() => {
    if (modalMode.value === 'withdraw') {
      return Math.max(currentPoints.value - amount.value, 0)
    }

    return currentPoints.value + amount.value
  })

  const virtualAccount = computed(() => issuedVirtualAccount.value || {
    bank: '-',
    accountNumber: '-',
    accountHolder: '-',
    depositAmount: formatAmount(amount.value),
    dueAt: '-',
  })

  function resetAmount() {
    amount.value = 0
    selectedPreset.value = null
  }

  function openWithdrawModal() {
    modalMode.value = 'withdraw'
    resetAmount()
  }

  function openChargeModal() {
    modalMode.value = 'charge'
    resetAmount()
  }

  function openVirtualAccountModal(account = {}) {
    issuedVirtualAccount.value = normalizeVirtualAccount(account)
    modalMode.value = 'virtual-account'
  }

  function closeModal() {
    modalMode.value = ''
    selectedPreset.value = null
    issuedVirtualAccount.value = null
  }

  function setPreset(preset) {
    selectedPreset.value = preset

    if (preset === 'all') {
      amount.value = currentPoints.value
      return
    }

    if (modalMode.value === 'withdraw') {
      amount.value = Math.min(amount.value + preset, currentPoints.value)
      return
    }

    amount.value += preset
  }

  async function submitModal() {
    if (amount.value <= 0) {
      return
    }

    if (modalMode.value === 'withdraw') {
      if (onWithdraw) {
        await onWithdraw(amount.value)
        closeModal()
        return
      }

      history.value.unshift({
        title: '인출',
        date: formatTimestamp(),
        amount: `-${amount.value.toLocaleString('ko-KR')} 원`,
        tone: 'minus',
      })
      currentPoints.value = Math.max(currentPoints.value - amount.value, 0)
      closeModal()
      return
    }

    if (modalMode.value === 'charge') {
      modalMode.value = 'charge-details'
    }
  }

  async function submitChargeDetails(details) {
    if (amount.value <= 0 || !onCharge) {
      return
    }

    await onCharge(amount.value, details)
  }

  function confirmVirtualAccount() {
    closeModal()
  }

  function formatDueDate(date) {
    if (!date) {
      return '-'
    }

    return String(date).replace('T', ' ')
  }

  function normalizeVirtualAccount(account = {}) {
    return {
      bank: account.bank || account.bankName || getBankLabel(account.bankCode),
      accountNumber: account.accountNumber || account.bankAccount || '-',
      accountHolder: account.accountHolder || account.accountHolderName || '-',
      depositAmount: account.depositAmount || formatAmount(Number(account.amount ?? amount.value ?? 0)),
      dueAt: account.dueAt || formatDueDate(account.dueDate),
    }
  }

  return {
    actionLabel,
    amount,
    amountLabel,
    closeModal,
    confirmVirtualAccount,
    currentPoints,
    expectedLabel,
    expectedPoints,
    formatAmount,
    history,
    modalMode,
    modalTitle,
    openChargeModal,
    openVirtualAccountModal,
    openWithdrawModal,
    presets,
    selectedPreset,
    setPreset,
    submitChargeDetails,
    submitModal,
    virtualAccount,
  }
}
