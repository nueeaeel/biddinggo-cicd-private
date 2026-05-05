<template>
  <PointsScreen
    :current-points="currentPoints"
    :has-next="hasNext"
    :history="pointHistoryItems"
    :issued-virtual-account="issuedVirtualAccount"
    :loading="loading"
    :prepare-charge-payment="prepareChargePayment"
    :show-issued-virtual-account="showIssuedVirtualAccount"
    :withdraw-points="withdrawPoints"
    @close-issued-virtual-account="closeIssuedVirtualAccount"
    @load-more="loadMorePoints"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { createVirtualAccount, exchangeUserPoints, getUserPoints, getUserProfile } from '../api/users'
import PointsScreen from '../components/mypage/points/PointsScreen.vue'
import { authState } from '../lib/authSession'
import { getBankLabel } from '../utils/banks'

const currentPoints = ref(0)
const issuedVirtualAccount = ref(null)
const pointHistoryItems = ref([])
const hasNext = ref(true)
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const showIssuedVirtualAccount = ref(false)

const pointTypeMeta = {
  CHARGE: { label: '충전', tone: 'plus' },
  BID: { label: '입찰', tone: 'minus' },
  EXCHANGE: { label: '인출', tone: 'minus' },
  REFUND: { label: '환불', tone: 'plus' },
  SETTLEMENT: { label: '구매 확정', tone: 'plus' },
}

function formatDate(date) {
  if (!date) {
    return ''
  }

  return String(date).replace('T', ' ')
}

function formatAmount(value) {
  return `${Number(value || 0).toLocaleString('ko-KR')}원`
}

function normalizePointHistory(item = {}) {
  const type = String(item.type || '').trim().toUpperCase()
  const meta = pointTypeMeta[type] || {
    label: item.type || '포인트',
    tone: Number(item.amount ?? 0) < 0 ? 'minus' : 'plus',
  }
  const absoluteAmount = Math.abs(Number(item.amount ?? 0))

  return {
    id: item.id,
    title: meta.label,
    date: formatDate(item.createdAt),
    amount: `${meta.tone === 'minus' ? '-' : '+'}${absoluteAmount.toLocaleString('ko-KR')}원`,
    tone: meta.tone,
  }
}

function getHistoryPage(response = {}) {
  return response.histroies || response.histories || response.history || response.pointHistories || {}
}

function normalizeIssuedVirtualAccount(account = {}) {
  return {
    bank: account.bankName || getBankLabel(account.bankCode || account.bank),
    accountNumber: account.bankAccount || account.accountNumber || '-',
    accountHolder: account.accountHolderName || account.accountHolder || '-',
    depositAmount: formatAmount(account.amount),
    dueAt: formatDate(account.dueDate || account.dueAt),
  }
}

async function loadMorePoints() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  loading.value = true

  try {
    const response = await getUserPoints({
      page: requestedPage,
      size: pageSize,
      order: 'DESC',
    })
    const historyPage = getHistoryPage(response)
    const content = historyPage?.content || []
    const existingIds = new Set(pointHistoryItems.value.map((item) => item.id))
    const nextItems = content.map(normalizePointHistory).filter((item) => !existingIds.has(item.id))

    currentPoints.value = Number(response?.currentPoint ?? currentPoints.value ?? 0)
    pointHistoryItems.value = [...pointHistoryItems.value, ...nextItems]
    page.value += 1
    hasNext.value = typeof historyPage?.hasNext === 'boolean'
      ? historyPage.hasNext
      : requestedPage < Number(historyPage?.totalPages ?? requestedPage)
  } catch {
    hasNext.value = false
  } finally {
    loading.value = false
  }
}

async function reloadPoints() {
  page.value = 1
  hasNext.value = true
  pointHistoryItems.value = []
  await loadMorePoints()
}

function closeIssuedVirtualAccount() {
  showIssuedVirtualAccount.value = false
  issuedVirtualAccount.value = null
}

async function withdrawPoints(amount) {
  await exchangeUserPoints({
    amount,
  })
  await reloadPoints()
}

function createChargeOrderId() {
  const now = new Date()
  const timestamp = [
    now.getFullYear(),
    String(now.getMonth() + 1).padStart(2, '0'),
    String(now.getDate()).padStart(2, '0'),
    String(now.getHours()).padStart(2, '0'),
    String(now.getMinutes()).padStart(2, '0'),
    String(now.getSeconds()).padStart(2, '0'),
    String(now.getMilliseconds()).padStart(3, '0'),
  ].join('')

  return `charge_${timestamp}`
}

async function prepareChargePayment(amount, details = {}) {
  const userProfile = await getUserProfile().catch(() => null)
  const virtualAccount = await createVirtualAccount({
    orderId: createChargeOrderId(),
    orderName: '포인트 충전',
    amount,
    customerName: details.customerName || userProfile?.name || userProfile?.username || authState.username || '회원',
    bank: details.bank || '20',
  })

  issuedVirtualAccount.value = normalizeIssuedVirtualAccount(virtualAccount)
  showIssuedVirtualAccount.value = true
}

onMounted(() => {
  loadMorePoints()
})
</script>
