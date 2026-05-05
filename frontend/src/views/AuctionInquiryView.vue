<template>
  <AuctionInquiryScreen
    :error-message="errorMessage"
    :has-next="hasNext"
    :inquiries="inquiries"
    :loading="loading"
    :selected-type="selectedType"
    @filter-change="changeInquiryType"
    @load-more="loadMoreInquiries"
    @open-auction="openAuctionDetail"
    @submit-reply="submitReply"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { answerAuctionInquiry } from '../api/auctionInquiries'
import { useToast } from '../composables/useToast'
import { getUserAuctionInquiries } from '../api/users'
import AuctionInquiryScreen from '../components/mypage/auction-inquiries/AuctionInquiryScreen.vue'

const router = useRouter()
const { showToast } = useToast()
const inquiries = ref([])
const hasNext = ref(true)
const loading = ref(false)
const errorMessage = ref('')
const page = ref(1)
const selectedType = ref('ALL')
const requestVersion = ref(0)
const pageSize = 10
const defaultOffset = 1073741824

const typeLabels = {
  PURCHASE: '구매',
  SALE: '판매',
  SALES: '판매',
  SELL: '판매',
  SELLER: '판매',
}

const statusLabels = {
  PENDING: '답변 대기',
  ANSWERED: '답변 완료',
  COMPLETE: '답변 완료',
  COMPLETED: '답변 완료',
}

function formatDate(value) {
  if (!value) {
    return '-'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return '-'
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date)
}

function normalizeType(value) {
  const key = String(value || '').trim().toUpperCase()

  return typeLabels[key] || value || '-'
}

function normalizeAnswerStatus(value, answer) {
  const key = String(value || '').trim().toUpperCase()

  return statusLabels[key] || (answer ? '답변 완료' : '답변 대기')
}

function canReply(item = {}) {
  const type = String(item.inquiryType || '').trim().toUpperCase()
  const status = String(item.answerStatus || '').trim().toUpperCase()

  return ['SALE', 'SALES', 'SELL', 'SELLER'].includes(type) && status === 'PENDING'
}

function normalizeInquiry(item = {}, index = 0) {
  const answerStatus = normalizeAnswerStatus(item.answerStatus, item.answer)
  const inquiryType = normalizeType(item.inquiryType)

  return {
    id: item.id,
    index,
    inquiryType,
    rawInquiryType: item.inquiryType,
    status: answerStatus,
    rawAnswerStatus: item.answerStatus,
    title: item.title || '문의',
    date: formatDate(item.createdAt),
    question: item.content || '',
    answer: item.answer || '',
    answerAuthor: '판매자 답변',
    answerDate: formatDate(item.answeredAt),
    auctionId: item.auctionId,
    action: item.auctionId ? '상품 보러가기' : '',
    pendingAction: canReply(item) ? '답변하기' : null,
  }
}

function resetInquiries() {
  inquiries.value = []
  page.value = 1
  hasNext.value = true
  loading.value = false
  errorMessage.value = ''
  requestVersion.value += 1
}

async function loadMoreInquiries() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  const currentRequestVersion = requestVersion.value
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getUserAuctionInquiries({
      type: selectedType.value,
      page: requestedPage,
      size: pageSize,
      order: 'ASC',
      offset: defaultOffset,
    })

    if (currentRequestVersion !== requestVersion.value) {
      return
    }

    const content = response?.content || []
    const existingIds = new Set(inquiries.value.map((item) => item.id))
    const nextItems = content
      .map(normalizeInquiry)
      .filter((item) => !existingIds.has(item.id))

    inquiries.value = [...inquiries.value, ...nextItems]
    page.value += 1
    hasNext.value = typeof response?.hasNext === 'boolean'
      ? response.hasNext
      : requestedPage < Number(response?.totalPages ?? requestedPage)
  } catch (error) {
    if (currentRequestVersion !== requestVersion.value) {
      return
    }

    showToast(error?.message || '구매/판매 문의 내역을 불러오지 못했습니다.', { color: 'error' })
    hasNext.value = false
  } finally {
    if (currentRequestVersion === requestVersion.value) {
      loading.value = false
    }
  }
}

function changeInquiryType(type) {
  if (selectedType.value === type) {
    return
  }

  selectedType.value = type
  resetInquiries()
  loadMoreInquiries()
}

function openAuctionDetail(inquiry) {
  if (inquiry?.auctionId) {
    router.push(`/auctions/${inquiry.auctionId}`)
  }
}

async function submitReply(inquiry, content) {
  const inquiryId = inquiry?.id
  const answer = String(content || '').trim()

  if (!inquiryId) {
    showToast('답변을 등록할 문의 ID가 없습니다.', { color: 'error' })
    return
  }

  if (!answer) {
    showToast('답변 내용을 입력해주세요.', { color: 'error' })
    return
  }

  try {
    const result = await answerAuctionInquiry(inquiryId, { answer })

    inquiries.value = inquiries.value.map((item) => (
      item.id === inquiryId
        ? {
            ...item,
            status: '답변 완료',
            rawAnswerStatus: 'ANSWERED',
            answer: result?.answer || answer,
            answerAuthor: '판매자 답변',
            answerDate: formatDate(result?.answeredAt || new Date()),
            pendingAction: null,
          }
        : item
    ))
  } catch (error) {
    showToast(error?.message || '답변 등록에 실패했습니다.', { color: 'error' })
  }
}

onMounted(() => {
  loadMoreInquiries()
})
</script>
