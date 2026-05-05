<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAuctionDetail, updateAuction } from '../api/auctions'
import AuctionSetupForm from '../components/register/AuctionSetupForm.vue'
import { authState } from '../lib/authSession'
import { formatPrice } from '../utils/marketplace'

const route = useRoute()
const router = useRouter()

const bidUnitOptions = ['1,000', '5,000', '10,000', '50,000', '100,000']
const detail = ref(null)
const errorMessage = ref('')
const loading = ref(false)
const processing = ref(false)
const selectedBidUnit = ref('10,000')
const selectedDuration = ref('5일')
const submitted = ref(false)
const successMessage = ref('')

const auctionForm = ref({
  extendAuction: true,
  timeDeal: false,
  startPrice: '',
  buyNowPrice: '',
  startDate: '',
  startTime: '',
  endDate: '',
  endTime: '',
})

const auctionType = computed(() => String(detail.value?.type || '').toUpperCase())
const durationOptions = computed(() => (
  auctionType.value === 'TIME_DEAL'
    ? ['4시간', '8시간', '12시간', '16시간', '20시간', '24시간', '28시간', '32시간', '36시간', '40시간', '44시간', '48시간']
    : ['3일', '4일', '5일', '6일', '7일', '8일', '9일', '10일']
))

function toDate(value) {
  const date = new Date(value)

  return Number.isNaN(date.getTime()) ? null : date
}

function formatDatePart(value) {
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
    .format(value)
    .replace(/\. /g, ' / ')
    .replace('.', '')
}

function formatTimePart(value) {
  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  }).format(value)
}

function toLocalDateTimeString(value) {
  const year = value.getFullYear()
  const month = String(value.getMonth() + 1).padStart(2, '0')
  const day = String(value.getDate()).padStart(2, '0')
  const hours = String(value.getHours()).padStart(2, '0')
  const minutes = String(value.getMinutes()).padStart(2, '0')
  const seconds = String(value.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`
}

function computeEndDate(startDate, durationLabel) {
  const endDate = new Date(startDate)
  const value = Number.parseInt(String(durationLabel || '').replace(/[^\d]/g, ''), 10) || 0

  if (String(durationLabel).includes('시간')) {
    endDate.setHours(endDate.getHours() + value)
  } else {
    endDate.setDate(endDate.getDate() + value)
  }

  return endDate
}

function parseMoney(value) {
  return Number(String(value || '').replace(/[^\d]/g, '')) || 0
}

function getDurationLabel(startDate, endDate) {
  const diffHours = Math.max(1, Math.round((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60)))

  if (auctionType.value === 'TIME_DEAL') {
    const hourLabel = `${diffHours}시간`
    return durationOptions.value.includes(hourLabel) ? hourLabel : durationOptions.value[0]
  }

  const diffDays = Math.max(1, Math.round(diffHours / 24))
  const dayLabel = `${diffDays}일`

  return durationOptions.value.includes(dayLabel) ? dayLabel : durationOptions.value[0]
}

function syncDisplayedSchedule(startDate, endDate) {
  auctionForm.value.startDate = formatDatePart(startDate)
  auctionForm.value.startTime = formatTimePart(startDate)
  auctionForm.value.endDate = formatDatePart(endDate)
  auctionForm.value.endTime = formatTimePart(endDate)
}

function selectDuration(option) {
  selectedDuration.value = option
  const startDate = toDate(detail.value?.startDate) || new Date()
  const endDate = computeEndDate(startDate, option)
  syncDisplayedSchedule(startDate, endDate)
}

function hydrateForm(auctionDetail) {
  const startDate = toDate(auctionDetail.startDate) || new Date()
  const endDate = toDate(auctionDetail.endDate) || computeEndDate(startDate, durationOptions.value[0])

  auctionForm.value = {
    extendAuction: true,
    timeDeal: auctionType.value === 'TIME_DEAL',
    startPrice: auctionDetail.startPrice ? String(auctionDetail.startPrice) : '',
    buyNowPrice: auctionDetail.buyNowPrice ? String(auctionDetail.buyNowPrice) : '',
    startDate: '',
    startTime: '',
    endDate: '',
    endTime: '',
  }
  selectedBidUnit.value = auctionDetail.bidUnit ? formatPrice(auctionDetail.bidUnit) : '10,000'
  selectedDuration.value = getDurationLabel(startDate, endDate)
  syncDisplayedSchedule(startDate, endDate)
}

async function loadAuction() {
  const auctionId = route.params.id

  if (!auctionId) {
    errorMessage.value = '수정할 경매를 찾을 수 없습니다.'
    return
  }

  if (!authState.isAuthenticated) {
    await router.replace('/login')
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const auctionDetail = await getAuctionDetail(auctionId)
    const memberId = Number(authState.memberId)
    const sellerId = Number(auctionDetail?.sellerId)

    if (!Number.isFinite(memberId) || memberId !== sellerId) {
      errorMessage.value = '본인이 등록한 경매만 수정할 수 있습니다.'
      return
    }

    detail.value = auctionDetail
    hydrateForm(auctionDetail)
  } catch (error) {
    errorMessage.value = error?.message || '경매 정보를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

async function submitEdit() {
  if (!detail.value?.auctionId || processing.value) {
    return
  }

  const startDate = toDate(detail.value.startDate) || new Date()
  const endDate = computeEndDate(startDate, selectedDuration.value)
  const startPrice = parseMoney(auctionForm.value.startPrice)
  const bidUnit = parseMoney(selectedBidUnit.value)

  if (!startPrice || !bidUnit) {
    errorMessage.value = '경매 시작가와 입찰 단위를 확인해주세요.'
    return
  }

  processing.value = true
  errorMessage.value = ''

  try {
    const result = await updateAuction(detail.value.auctionId, {
      startPrice,
      bidUnit,
      buyNowPrice: parseMoney(auctionForm.value.buyNowPrice) || null,
      startDate: toLocalDateTimeString(startDate),
      endDate: toLocalDateTimeString(endDate),
    })

    const auctionId = result?.auctionId || detail.value.auctionId
    await router.replace(`/auctions/${auctionId}`)
  } catch (error) {
    errorMessage.value = error?.message || '경매 수정에 실패했습니다.'
  } finally {
    processing.value = false
  }
}

function cancelEdit() {
  router.push(`/auctions/${route.params.id}`)
}

onMounted(loadAuction)
</script>

<template>
  <section class="register-screen">
    <header class="register-method-header">
      <h2>경매 수정</h2>
      <p>수정 가능한 경매 가격과 일정을 변경해주세요.</p>
    </header>

    <p v-if="loading" class="feedback-strip">경매 정보를 불러오는 중입니다.</p>

    <AuctionSetupForm
      v-else-if="detail"
      :auction-form="auctionForm"
      :bid-unit-options="bidUnitOptions"
      cancel-label="상세로 돌아가기"
      :duration-options="durationOptions"
      :error-message="errorMessage"
      :processing="processing"
      :selected-bid-unit="selectedBidUnit"
      :selected-duration="selectedDuration"
      :show-type-toggles="false"
      :submitted="submitted"
      submit-label="수정하기"
      :success-message="successMessage"
      @cancel="cancelEdit"
      @select-bid-unit="selectedBidUnit = $event"
      @select-duration="selectDuration"
      @submit="submitEdit"
    />

    <div v-else-if="errorMessage" class="feedback-strip is-error">
      {{ errorMessage }}
    </div>
  </section>
</template>

<style scoped>
.feedback-strip {
  margin: 0;
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
