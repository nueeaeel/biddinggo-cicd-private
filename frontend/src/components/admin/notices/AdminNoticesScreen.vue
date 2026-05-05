<template>
  <AdminLayout>
    <section class="page-header-inline">
      <h1>공지사항 관리</h1>
      <button class="primary-button" type="button" @click="openCreateModal">+ 공지사항 등록</button>
    </section>

    <section class="filter-bar admin-filter-bar admin-filter-bar--compact">
      <div class="filter-chips">
        <button
          v-for="status in noticeFilters"
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
      <h2>공지사항 목록을 불러오지 못했습니다.</h2>
      <p>{{ errorMessage }}</p>
      <button class="primary-button" type="button" @click="loadNotices">다시 시도</button>
    </section>

    <section v-else class="admin-table-card surface-card">
      <table class="admin-transaction-table">
        <thead>
          <tr>
            <th>공지 번호</th>
            <th>제목</th>
            <th>등록일</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="notice in filteredNotices" :key="notice.noticeNo">
            <td class="admin-transaction-table__strong">{{ notice.noticeNo }}</td>
            <td>{{ notice.title }}</td>
            <td>{{ notice.createdAt }}</td>
            <td><AdminStatusBadge :status="notice.status" /></td>
            <td>
              <div class="admin-inline-actions">
                <button
                  class="ghost-button admin-inline-button admin-inline-button--notice-edit"
                  type="button"
                  :disabled="notice.rawStatus === 'DELETED'"
                  @click="openEditModal(notice)"
                >
                  수정
                </button>
                <button
                  class="ghost-button admin-inline-button admin-inline-button--notice-private"
                  type="button"
                  :disabled="notice.rawStatus === 'DELETED'"
                  @click="toggleNoticeStatus(notice)"
                >
                  비공개
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredNotices.length === 0">
            <td class="admin-transaction-table__empty" colspan="5">조건에 맞는 공지사항이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </section>

    <AdminNoticeModal
      v-if="isModalOpen"
      :notice="editingNotice"
      @close="closeModal"
      @submit="saveNotice"
    />
  </AdminLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import AdminLayout from '../../layout/AdminLayout.vue'
import AdminStatusBadge from '../shared/AdminStatusBadge.vue'
import AdminNoticeModal from './AdminNoticeModal.vue'
import {
  createAdminNotice,
  deleteAdminNotice,
  fetchAdminNotices,
  updateAdminNotice,
} from '../../../api/adminNotices'

const noticeFilters = ['전체', '게시중', '비공개']
const selectedFilter = ref('전체')
const notices = ref([])
const isModalOpen = ref(false)
const editingNotice = ref(null)
const isLoading = ref(false)
const errorMessage = ref('')

const filteredNotices = computed(() => {
  if (selectedFilter.value === '전체') {
    return notices.value
  }

  return notices.value.filter((notice) => notice.status === selectedFilter.value)
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

function toUiStatus(rawStatus) {
  if (rawStatus === 'ACTIVE') return '게시중'
  return '비공개'
}

function toUiNotice(item) {
  return {
    noticeNo: item.id,
    title: item.title || '',
    content: item.content || '',
    createdAt: formatDateTime(item.createdAt),
    status: toUiStatus(item.status),
    rawStatus: item.status,
  }
}

async function loadNotices() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const page = await fetchAdminNotices({ page: 1, size: 100, order: 'DESC' })
    notices.value = Array.isArray(page?.content) ? page.content.map(toUiNotice) : []
  } catch (error) {
    errorMessage.value = String(error?.message || '요청 처리 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}

function openCreateModal() {
  editingNotice.value = null
  isModalOpen.value = true
}

function openEditModal(notice) {
  editingNotice.value = {
    noticeNo: notice.noticeNo,
    title: notice.title,
    content: notice.content,
  }
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
  editingNotice.value = null
}

async function saveNotice(payload) {
  try {
    if (editingNotice.value?.noticeNo) {
      await updateAdminNotice(editingNotice.value.noticeNo, payload)
    } else {
      await createAdminNotice(payload)
    }

    await loadNotices()
    closeModal()
  } catch (error) {
    errorMessage.value = String(error?.message || '공지사항 저장에 실패했습니다.')
  }
}

async function toggleNoticeStatus(notice) {
  if (notice.rawStatus === 'DELETED') {
    return
  }

  try {
    await deleteAdminNotice(notice.noticeNo)
    await loadNotices()
  } catch (error) {
    errorMessage.value = String(error?.message || '공지사항 상태 변경에 실패했습니다.')
  }
}

onMounted(loadNotices)
</script>
