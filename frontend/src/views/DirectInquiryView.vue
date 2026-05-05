<template>
  <DirectInquiryScreen
    :has-next="hasNext"
    :inquiries="inquiries"
    :loading="loading"
    :submit-inquiry="submitInquiry"
    @load-more="loadMoreInquiries"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import DirectInquiryScreen from '../components/mypage/direct-inquiries/DirectInquiryScreen.vue'
import { useToast } from '../composables/useToast'
import { createUserDirectInquiry, getUserDirectInquiries } from '../api/users'

const { showToast } = useToast()
const inquiries = ref([])
const hasNext = ref(true)
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const defaultOffset = 1073741824

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

function normalizeInquiry(item = {}) {
  return {
    id: item.id,
    status: item.answer ? '답변 완료' : '답변 대기',
    title: item.category || '1:1 문의',
    date: formatDate(item.createdAt),
    question: item.content || '',
    answerAuthor: item.adminNickname || '관리자 답변',
    answerDate: formatDate(item.answeredAt),
    answer: item.answer || '',
  }
}

async function loadMoreInquiries() {
  if (loading.value || !hasNext.value) {
    return
  }

  const requestedPage = page.value
  loading.value = true

  try {
    const response = await getUserDirectInquiries({
      page: requestedPage,
      size: pageSize,
      order: 'DESC',
      offset: defaultOffset,
    })
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
    showToast(error?.message || '1:1 문의 내역을 불러오지 못했습니다.', { color: 'error' })
    hasNext.value = false
  } finally {
    loading.value = false
  }
}

async function submitInquiry(payload) {
  try {
    const result = await createUserDirectInquiry({
      category: payload.type,
      content: payload.content,
    })

    inquiries.value = [normalizeInquiry(result), ...inquiries.value]
    showToast('1:1 문의가 등록되었습니다.')
  } catch (error) {
    showToast(error?.message || '1:1 문의 등록에 실패했습니다.', { color: 'error' })
    throw error
  }
}

onMounted(() => {
  loadMoreInquiries()
})
</script>
