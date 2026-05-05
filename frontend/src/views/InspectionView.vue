<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getInspectionList, updateInspectionShippingInfo } from '../api/inspections'
import InspectionScreen from '../components/InspectionScreen.vue'
import { assets } from '../data/marketplaceData'
import { buildInspectionSummary, normalizeInspectionListItem } from '../utils/marketplace'

const router = useRouter()
const errorMessage = ref('')
const inspectionItems = ref([])
const loading = ref(false)

const inspectionSummary = computed(() => buildInspectionSummary(inspectionItems.value))

async function loadInspections() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getInspectionList({ page: 1, size: 100, order: 'DESC' })
    inspectionItems.value = (response?.content || []).map(normalizeInspectionListItem)
  } catch (error) {
    inspectionItems.value = []
    errorMessage.value = error?.message || '사전 검수 상품을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

function openRegister(mode = 'inspection') {
  router.push({
    path: '/register',
    query: {
      mode,
    },
  })
}

function openAuctionRegister(item) {
  if (!item?.inspectionId) {
    errorMessage.value = '경매 등록할 검수 상품을 찾을 수 없습니다.'
    return
  }

  router.push({
    path: '/register',
    query: {
      mode: 'direct-auction',
      inspectionId: String(item.inspectionId),
    },
  })
}

async function submitShipping({ item, form }) {
  if (!item?.inspectionId) {
    errorMessage.value = '배송 정보를 등록할 검수 상품을 찾을 수 없습니다.'
    return false
  }

  const carrier = form.company.trim()
  const trackingNumber = form.invoiceNumber.trim()

  if (!carrier || !trackingNumber) {
    errorMessage.value = '택배사와 송장 번호를 입력해주세요.'
    return false
  }

  try {
    await updateInspectionShippingInfo(item.inspectionId, {
      carrier,
      trackingNumber,
    })

    window.location.reload()
    return true
  } catch (error) {
    errorMessage.value = error?.message || '배송 정보 등록에 실패했습니다.'
    return false
  }
}

onMounted(loadInspections)
</script>

<template>
  <InspectionScreen
    :assets="assets"
    :error-message="errorMessage"
    :items="inspectionItems"
    :loading="loading"
    :summary="inspectionSummary"
    @open-auction-register="openAuctionRegister"
    @open-register="openRegister"
    @submit-shipping="submitShipping"
  />
</template>
