<!-- src/components/admin/inquiries/AdminInquiryScreen.vue -->
<template>
  <AdminLayout>
    <section class="page-header-block">
      <h1>1:1 문의 관리</h1>
    </section>

    <section class="filter-bar admin-filter-bar admin-filter-bar--compact">
      <div class="filter-chips">
        <button
          v-for="status in inquiryFilters"
          :key="status"
          class="chip admin-chip"
          :class="{ active: selectedFilter === status }"
          type="button"
          @click="selectedFilter = status"
        >
          {{ status }}
        </button>
      </div>
    </section>

    <section v-if="errorMessage" class="admin-placeholder-card surface-card">
      <h2>문의 목록을 불러오지 못했습니다.</h2>
      <p>{{ errorMessage }}</p>
      <button class="primary-button" type="button" @click="loadInquiries">다시 시도</button>
    </section>

    <section v-else class="stack-list admin-inquiry-stack">
      <AdminInquiryCard
        v-for="inquiry in filteredInquiries"
        :key="inquiry.id"
        :expanded="expandedIds.has(inquiry.id)"
        :inquiry="inquiry"
        @submit-reply="submitReply(inquiry.id, $event)"
        @toggle="toggleExpanded(inquiry.id)"
      />
    </section>

    <section v-if="!isLoading && !errorMessage && filteredInquiries.length === 0" class="admin-placeholder-card surface-card">
      <h2>조건에 맞는 문의가 없습니다.</h2>
    </section>
  </AdminLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import AdminLayout from '../../layout/AdminLayout.vue'
import AdminInquiryCard from './AdminInquiryCard.vue'
import { answerAdminInquiry, fetchAdminInquiries } from '../../../api/adminInquiries'

const inquiryFilters = ['전체', '답변 대기', '답변 완료']
const selectedFilter = ref('전체')
const inquiries = ref([])
const expandedIds = ref(new Set())
const isLoading = ref(false)
const errorMessage = ref('')

const filteredInquiries = computed(() => {
  if (selectedFilter.value === '전체') {
    return inquiries.value
  }

  return inquiries.value.filter((inquiry) => inquiry.status === selectedFilter.value)
})

function formatDateTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)

  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mi = String(date.getMinutes()).padStart(2, '0')

  return `${yyyy}.${mm}.${dd} ${hh}:${mi}`
}

function toUiInquiry(item) {
  const answered = Boolean(item.answer)

  return {
    id: String(item.id),
    status: answered ? '답변 완료' : '답변 대기',
    title: item.category || '기타',
    createdAt: formatDateTime(item.createdAt),
    question: item.content || '',
    answer: answered
      ? {
          author: item.adminNickname || '관리자 답변',
          createdAt: formatDateTime(item.answeredAt),
          content: item.answer,
        }
      : null,
  }
}

async function loadInquiries() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const page = await fetchAdminInquiries({ page: 1, size: 100, order: 'DESC' })
    inquiries.value = Array.isArray(page?.content) ? page.content.map(toUiInquiry) : []
  } catch (error) {
    errorMessage.value = String(error?.message || '요청 처리 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}

function toggleExpanded(id) {
  const next = new Set(expandedIds.value)
  if (next.has(id)) next.delete(id)
  else next.add(id)
  expandedIds.value = next
}

async function submitReply(id, content) {
  try {
    await answerAdminInquiry(Number(id), content)
    await loadInquiries()
    expandedIds.value = new Set([id])
  } catch (error) {
    errorMessage.value = String(error?.message || '답변 등록에 실패했습니다.')
  }
}

onMounted(loadInquiries)
</script>
