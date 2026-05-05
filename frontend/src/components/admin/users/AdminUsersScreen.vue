<template>
  <AdminLayout>
    <section class="page-header-block">
      <h1>사용자 관리</h1>
    </section>

    <section class="filter-bar admin-filter-bar">
      <div class="filter-chips">
        <button
          v-for="status in adminUserStatusFilters"
          :key="status"
          class="chip admin-chip"
          :class="{ active: selectedFilter === status }"
          type="button"
          @click="selectedFilter = status"
        >
          {{ status }}
        </button>
      </div>

      <v-text-field
        v-model="searchQuery"
        class="page-search-field admin-search-field"
        density="comfortable"
        hide-details
        placeholder="닉네임, 이메일 검색"
        prepend-inner-icon="mdi-magnify"
        variant="solo"
      />
    </section>

    <section class="admin-table-card surface-card">
      <table class="admin-transaction-table">
        <thead>
          <tr>
            <th>회원 번호</th>
            <th>닉네임</th>
            <th>이메일</th>
            <th>가입일</th>
            <th>거래 수</th>
            <th>신고 수</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in displayedUsers" :key="user.userNo">
            <td class="admin-transaction-table__strong">{{ user.userNo }}</td>
            <td>{{ user.nickname }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.joinedAt }}</td>
            <td>{{ user.tradeCount }}</td>
            <td>{{ user.reportCount }}</td>
            <td><AdminStatusBadge :status="user.status" /></td>
            <td>
              <button
                class="ghost-button admin-inline-button admin-inline-button--status"
                type="button"
                :disabled="isProcessing || !canToggleStatus(user.statusRaw)"
                @click="toggleStatus(user.userNo)"
              >
                {{ user.statusRaw === 'ACTIVE' ? '정지' : user.statusRaw === 'INACTIVE' ? '복구' : '-' }}
              </button>
          </td>
          </tr>
          <tr v-if="displayedUsers.length === 0">
            <td class="admin-transaction-table__empty" colspan="8">조건에 맞는 사용자가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </section>
  </AdminLayout>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import AdminLayout from '../../layout/AdminLayout.vue'
import AdminStatusBadge from '../shared/AdminStatusBadge.vue'
import { fetchAdminUsers, updateAdminUserStatus } from '../../../api/adminUsers'

const adminUserStatusFilters = ['전체', '활성', '정지']

const selectedFilter = ref('전체')
const searchQuery = ref('')
const users = ref([])
const isLoading = ref(false)
const isProcessing = ref(false)
const errorMessage = ref('')
const totalElements = ref(0)

let searchTimer = null
let requestSeq = 0

const filterToApiStatus = {
  전체: undefined,
  활성: 'ACTIVE',
  정지: 'INACTIVE',
}

const displayedUsers = computed(() => {
  const keyword = searchQuery.value.trim().toLowerCase()

  return users.value.filter((user) => {
    const matchesStatus = selectedFilter.value === '전체' || user.status === selectedFilter.value
    const matchesKeyword =
      !keyword ||
      String(user.nickname || '').toLowerCase().includes(keyword) ||
      String(user.email || '').toLowerCase().includes(keyword)

    return matchesStatus && matchesKeyword
  })
})

function statusToLabel(status) {
  if (status === 'ACTIVE') return '활성'
  if (status === 'INACTIVE') return '정지'
  if (status === 'PENDING') return '대기'
  if (status === 'DELETED') return '탈퇴'
  return String(status || '-')
}

function formatDateTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  return `${yyyy}.${mm}.${dd}`
}

function toUiUser(item) {
  return {
    userNo: item.id,
    nickname: item.nickname || '-',
    email: item.email || '-',
    joinedAt: formatDateTime(item.createdAt),
    tradeCount: '-',
    reportCount: '-',
    status: statusToLabel(item.status),
    statusRaw: item.status,
  }
}

function canToggleStatus(statusRaw) {
  return statusRaw === 'ACTIVE' || statusRaw === 'INACTIVE'
}

async function toggleStatus(userNo) {
  const target = users.value.find((u) => u.userNo === userNo)
  if (!target || !canToggleStatus(target.statusRaw) || isProcessing.value) return

  const nextStatus = target.statusRaw === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'

  isProcessing.value = true
  errorMessage.value = ''

  try {
    await updateAdminUserStatus(userNo, nextStatus)
    await loadUsers()
  } catch (error) {
    errorMessage.value = String(error?.message || '사용자 상태 변경에 실패했습니다.')
  } finally {
    isProcessing.value = false
  }
}

async function loadUsers() {
  const seq = ++requestSeq
  isLoading.value = true
  errorMessage.value = ''

  try {
    const page = await fetchAdminUsers({
      page: 1,
      size: 100,
      order: 'DESC',
      keyword: searchQuery.value.trim() || undefined,
      status: filterToApiStatus[selectedFilter.value],
    })

    if (seq !== requestSeq) return
    users.value = Array.isArray(page?.content) ? page.content.map(toUiUser) : []
    totalElements.value = Number(page?.totalElements || 0)
  } catch (error) {
    if (seq !== requestSeq) return
    errorMessage.value = String(error?.message || '사용자 목록 조회에 실패했습니다.')
  } finally {
    if (seq === requestSeq) isLoading.value = false
  }
}

watch([selectedFilter, searchQuery], () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(loadUsers, 300)
})

onMounted(loadUsers)

</script>
