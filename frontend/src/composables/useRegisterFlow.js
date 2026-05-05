import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { createAuction, createAuctionFromInspectionItem } from '../api/auctions'
import { getCategoryList } from '../api/categories'
import { requestPresignedUpload, uploadToPresignedUrl, deleteUploadedFile } from '../api/files'
import { createInspection, getInspectionDetail, getInspectionList } from '../api/inspections'
import { normalizeCategoryRows, getFallbackCategories } from '../utils/category'
import { mergeInspectionItemDetail, normalizeInspectionPickItem } from '../utils/marketplace'

function createEmptyForm() {
  return {
    name: '',
    categoryId: '',
    brand: '',
    condition: '',
    description: '',
  }
}

function createEmptyAuctionForm() {
  return {
    extendAuction: true,
    timeDeal: false,
    startPrice: '',
    buyNowPrice: '',
    startDateInput: '',
    startTimeInput: '',
    startDate: '',
    startTime: '',
    endDate: '',
    endTime: '',
  }
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

function parseMoney(value) {
  return Number(String(value || '').replace(/[^\d]/g, '')) || 0
}

function normalizeBidUnit(option) {
  return parseMoney(option)
}

function getAuctionId(result) {
  return result?.auctionId
    ?? result?.id
    ?? result?.auction?.auctionId
    ?? result?.auction?.id
    ?? null
}

function getInspectionId(result) {
  return result?.inspectionId
    ?? result?.id
    ?? result?.inspection?.inspectionId
    ?? result?.inspection?.id
    ?? null
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

function toLocalDateTimeString(value) {
  const year = value.getFullYear()
  const month = String(value.getMonth() + 1).padStart(2, '0')
  const day = String(value.getDate()).padStart(2, '0')
  const hours = String(value.getHours()).padStart(2, '0')
  const minutes = String(value.getMinutes()).padStart(2, '0')
  const seconds = String(value.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`
}

function toDateInputValue(value) {
  const year = value.getFullYear()
  const month = String(value.getMonth() + 1).padStart(2, '0')
  const day = String(value.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

function toTimeInputValue(value) {
  const hours = String(value.getHours()).padStart(2, '0')
  const minutes = String(value.getMinutes()).padStart(2, '0')

  return `${hours}:${minutes}`
}

export function useRegisterFlow(initialMode, initialInspectionId) {
  const allowedModes = ['select', 'inspection', 'inspection-pick', 'direct', 'direct-auction']
  const currentMode = ref(allowedModes.includes(initialMode.value) ? initialMode.value : 'select')
  const registrationType = ref(
    initialMode.value === 'inspection' || initialMode.value === 'inspection-pick' ? 'inspection' : 'direct',
  )
  const submitted = ref(false)
  const processing = ref(false)
  const uploadInProgress = ref(false)
  const uploadedImages = ref([])
  const selectedBidUnit = ref('10,000')
  const selectedDuration = ref('5일')
  const selectedInspectionId = ref(0)
  const selectedInspectionDetail = ref(null)
  const isInspectionDetailOpen = ref(false)
  const categoryOptions = ref([])
  const inspectionPickItems = ref([])
  const inspectionSearchQuery = ref('')
  const successMessage = ref('')
  const errorMessage = ref('')

  const form = ref(createEmptyForm())
  const auctionForm = ref(createEmptyAuctionForm())

  const headerTitle = computed(() => {
    if (currentMode.value === 'inspection') return '검수 상품 신청'
    if (currentMode.value === 'inspection-pick') return '사전 검수 상품 등록'
    if (currentMode.value === 'direct') return '직접 상품 등록'
    if (currentMode.value === 'direct-auction') return '경매 등록'
    return '경매 등록 방법 선택'
  })

  const headerDescription = computed(() => {
    if (currentMode.value === 'inspection') return '검수 상품 정보를 입력해주세요.'
    if (currentMode.value === 'inspection-pick') return '검수가 완료된 상품을 선택해 경매에 등록해주세요.'
    if (currentMode.value === 'direct') return '경매에 등록할 상품 정보를 입력해주세요.'
    if (currentMode.value === 'direct-auction') return '경매 정보를 입력해주세요.'
    return '경매 등록하실 방법을 선택해주세요.'
  })

  const thumbnailPlaceholders = computed(() => Math.max(0, 3 - Math.max(0, uploadedImages.value.length - 1)))
  const showStepper = computed(() =>
    currentMode.value === 'inspection-pick' || currentMode.value === 'direct' || currentMode.value === 'direct-auction',
  )
  const isAuctionStep = computed(() => currentMode.value === 'direct-auction')
  const isInspectionAuctionRegistration = computed(
    () => currentMode.value === 'direct-auction' && registrationType.value === 'inspection',
  )
  const firstStepLabel = computed(() =>
    registrationType.value === 'inspection' ? '사전 검수 상품 등록' : '직접 상품 등록',
  )
  const bidUnitOptions = ['1,000', '5,000', '10,000', '50,000', '100,000']
  const durationOptions = computed(() =>
    auctionForm.value.timeDeal
      ? ['4시간', '8시간', '12시간', '16시간', '20시간', '24시간', '28시간', '32시간', '36시간', '40시간', '44시간', '48시간']
      : ['3일', '4일', '5일', '6일', '7일', '8일', '9일', '10일'],
  )
  const selectedInspectionItem = computed(() => {
    const baseItem = inspectionPickItems.value.find((item) => item.displayId === selectedInspectionId.value) || null

    if (!baseItem) {
      return null
    }

    if (selectedInspectionDetail.value?.inspectionId !== baseItem.inspectionId) {
      return baseItem
    }

    return mergeInspectionItemDetail(baseItem, selectedInspectionDetail.value)
  })
  const filteredInspectionPickItems = computed(() => {
    const keyword = inspectionSearchQuery.value.trim().toLowerCase()

    if (!keyword) {
      return inspectionPickItems.value
    }

    return inspectionPickItems.value.filter((item) => {
      const haystack = [
        item.title,
        item.brand,
        item.description,
        item.categoryLabel,
        item.inspectionGrade,
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()

      return haystack.includes(keyword)
    })
  })

  function clearMessages() {
    submitted.value = false
    successMessage.value = ''
    errorMessage.value = ''
  }

  function setInspectionSearchQuery(value) {
    inspectionSearchQuery.value = String(value || '')
  }

  function getAuctionStartDate() {
    if (!auctionForm.value.timeDeal) {
      return new Date()
    }

    const { startDateInput, startTimeInput } = auctionForm.value

    if (!startDateInput || !startTimeInput) {
      return null
    }

    const startDate = new Date(`${startDateInput}T${startTimeInput}:00`)

    return Number.isNaN(startDate.getTime()) ? null : startDate
  }

  function syncAuctionSchedule() {
    let startDate = getAuctionStartDate()

    if (!startDate) {
      startDate = new Date()

      if (auctionForm.value.timeDeal) {
        auctionForm.value.startDateInput = toDateInputValue(startDate)
        auctionForm.value.startTimeInput = toTimeInputValue(startDate)
      }
    }

    const endDate = computeEndDate(startDate, selectedDuration.value)

    auctionForm.value.startDate = formatDatePart(startDate)
    auctionForm.value.startTime = formatTimePart(startDate)
    auctionForm.value.endDate = formatDatePart(endDate)
    auctionForm.value.endTime = formatTimePart(endDate)
  }

  async function loadCategoryOptions() {
    try {
      const response = await getCategoryList()
      const rows = response?.categories || response || []
      categoryOptions.value = normalizeCategoryRows(Array.isArray(rows) ? rows : [])
    } catch {
      categoryOptions.value = normalizeCategoryRows(getFallbackCategories())
    }
  }

  async function loadInspectionPickItems() {
    try {
      const page = await getInspectionList({
        page: 1,
        size: 24,
        status: 'PASSED',
        order: 'DESC',
      })

      inspectionPickItems.value = (page?.content || [])
        .map((item) => normalizeInspectionPickItem(item))
        .filter((item) => item.inspectionStatus === 'PASSED' && item.auctionItemStatus === 'PENDING')
        .map((item, index) => ({
          ...item,
          displayId: index,
        }))
      selectedInspectionDetail.value = null

      const requestedInspectionId = Number(initialInspectionId?.value)
      const preselectedItem = Number.isFinite(requestedInspectionId) && requestedInspectionId > 0
        ? inspectionPickItems.value.find((item) => item.inspectionId === requestedInspectionId)
        : null

      selectedInspectionId.value = preselectedItem?.displayId || inspectionPickItems.value[0]?.displayId || 0

      if (preselectedItem) {
        registrationType.value = 'inspection'
        currentMode.value = 'direct-auction'
        auctionForm.value.timeDeal = false
        syncAuctionSchedule()

        try {
          selectedInspectionDetail.value = await getInspectionDetail(preselectedItem.inspectionId)
        } catch {
          // 상세 조회 실패 시 목록 응답 기반 정보로만 진행한다.
        }
      }
    } catch (error) {
      inspectionPickItems.value = []

      if (currentMode.value === 'inspection-pick' || currentMode.value === 'direct-auction') {
        errorMessage.value = error?.message || '검수 완료 상품을 불러오지 못했습니다.'
      }
    }
  }

  async function uploadSingleFile(file) {
    const presigned = await requestPresignedUpload(file)
    await uploadToPresignedUrl(presigned.uploadUrl, file)

    uploadedImages.value.push({
      name: file.name,
      previewUrl: URL.createObjectURL(file),
      fileKey: presigned.fileKey,
      publicUrl: presigned.publicUrl,
      type: file.type,
      size: file.size,
    })
  }

  async function handleFiles(event) {
    const files = Array.from(event?.target?.files || [])
    const remain = Math.max(0, 10 - uploadedImages.value.length)
    const selected = files.slice(0, remain)

    if (!selected.length) {
      return
    }

    uploadInProgress.value = true
    errorMessage.value = ''

    try {
      for (const file of selected) {
        await uploadSingleFile(file)
      }
    } catch (error) {
      errorMessage.value = error?.message || '이미지 업로드에 실패했습니다.'
    } finally {
      uploadInProgress.value = false

      if (event?.target) {
        event.target.value = ''
      }
    }
  }

  async function removeImage(index) {
    const [image] = uploadedImages.value.splice(index, 1)

    if (!image) {
      return
    }

    if (image.previewUrl) {
      URL.revokeObjectURL(image.previewUrl)
    }

    if (image.fileKey) {
      try {
        await deleteUploadedFile(image.fileKey)
      } catch {
        // 삭제 실패는 사용자 입력 흐름을 막지 않는다.
      }
    }
  }

  function setPrimaryImage(index) {
    if (index <= 0 || index >= uploadedImages.value.length) {
      return
    }

    const [image] = uploadedImages.value.splice(index, 1)

    if (image) {
      uploadedImages.value.unshift(image)
    }
  }

  function resetForm() {
    uploadedImages.value.forEach((image) => {
      if (image.previewUrl) {
        URL.revokeObjectURL(image.previewUrl)
      }
    })

    uploadedImages.value = []
    form.value = createEmptyForm()
    auctionForm.value = createEmptyAuctionForm()
    selectedBidUnit.value = '10,000'
    selectedDuration.value = '5일'
    inspectionSearchQuery.value = ''
    selectedInspectionDetail.value = null
    syncAuctionSchedule()
    clearMessages()
  }

  function openMode(mode) {
    registrationType.value = mode === 'inspection' || mode === 'inspection-pick' ? 'inspection' : 'direct'
    currentMode.value = mode
    inspectionSearchQuery.value = ''
    clearMessages()

    if (mode === 'inspection-pick') {
      loadInspectionPickItems()
    }
  }

  function openInspectionRequest() {
    registrationType.value = 'inspection'
    currentMode.value = 'inspection'
    inspectionSearchQuery.value = ''
    clearMessages()
  }

  function goBackToSelect() {
    resetForm()
    currentMode.value = 'select'
  }

  function validateProductForm({ requireCondition }) {
    if (!uploadedImages.value.length) {
      throw new Error('상품 이미지를 1장 이상 업로드해주세요.')
    }

    if (!form.value.name.trim()) {
      throw new Error('상품명을 입력해주세요.')
    }

    if (!form.value.categoryId) {
      throw new Error('카테고리를 선택해주세요.')
    }

    if (requireCondition && !form.value.condition) {
      throw new Error('상품 상태를 선택해주세요.')
    }

    if (!form.value.description.trim()) {
      throw new Error('상세 설명을 입력해주세요.')
    }
  }

  function validateAuctionForm() {
    if (!auctionForm.value.startPrice) {
      throw new Error('경매 시작가를 입력해주세요.')
    }

    if (!selectedBidUnit.value) {
      throw new Error('입찰 단위를 선택해주세요.')
    }

    if (auctionForm.value.timeDeal && (!auctionForm.value.startDateInput || !auctionForm.value.startTimeInput)) {
      throw new Error('타임딜 시작 일시를 선택해주세요.')
    }
  }

  function buildImagePayload() {
    return uploadedImages.value.map((image, index) => ({
      fileKey: image.fileKey,
      displayOrder: index + 1,
    }))
  }

  function buildAuctionPayload(type) {
    const startDate = getAuctionStartDate()

    if (!startDate) {
      throw new Error('유효한 경매 시작 일시를 선택해주세요.')
    }

    const endDate = computeEndDate(startDate, selectedDuration.value)

    return {
      type,
      startPrice: parseMoney(auctionForm.value.startPrice),
      bidUnit: normalizeBidUnit(selectedBidUnit.value),
      vickreyPrice: null,
      buyNowPrice: parseMoney(auctionForm.value.buyNowPrice) || null,
      startDate: toLocalDateTimeString(startDate),
      endDate: toLocalDateTimeString(endDate),
    }
  }

  async function submitInspectionRegistration() {
    validateProductForm({ requireCondition: false })

    const payload = {
      item: {
        categoryId: Number(form.value.categoryId),
        brand: form.value.brand.trim() || null,
        name: form.value.name.trim(),
        quality: form.value.condition || null,
        description: form.value.description.trim(),
        images: buildImagePayload(),
      },
      inspection: {
        carrier: null,
        trackingNumber: null,
      },
    }

    const result = await createInspection(payload)
    const inspectionId = getInspectionId(result)

    resetForm()
    currentMode.value = 'select'
    return { type: 'inspection', inspectionId }
  }

  async function submitDirectAuctionRegistration() {
    validateProductForm({ requireCondition: true })
    validateAuctionForm()

    const payload = {
      item: {
        categoryId: Number(form.value.categoryId),
        brand: form.value.brand.trim() || null,
        name: form.value.name.trim(),
        quality: form.value.condition || null,
        description: form.value.description.trim(),
        images: buildImagePayload(),
      },
      auction: buildAuctionPayload(auctionForm.value.timeDeal ? 'TIME_DEAL' : 'NORMAL'),
    }

    const result = await createAuction(payload)
    const auctionId = getAuctionId(result)

    if (auctionId) {
      resetForm()
      currentMode.value = 'select'
      return { type: 'auction', auctionId }
    }

    submitted.value = true
    successMessage.value = '경매 등록이 완료되었습니다.'
    return { type: 'auction', auctionId: null }
  }

  async function submitInspectionAuctionRegistration() {
    validateAuctionForm()

    if (!selectedInspectionItem.value?.itemId) {
      throw new Error('경매 등록할 검수 완료 상품을 선택해주세요.')
    }

    const payload = {
      itemId: selectedInspectionItem.value.itemId,
      auction: buildAuctionPayload(auctionForm.value.timeDeal ? 'TIME_DEAL' : 'NORMAL'),
    }

    const result = await createAuctionFromInspectionItem(payload)
    const auctionId = getAuctionId(result)

    if (auctionId) {
      resetForm()
      currentMode.value = 'select'
      return { type: 'auction', auctionId }
    }

    submitted.value = true
    successMessage.value = '검수 완료 상품 경매 등록이 완료되었습니다.'
    return { type: 'auction', auctionId: null }
  }

  async function submitForm() {
    clearMessages()

    if (currentMode.value === 'direct') {
      try {
        validateProductForm({ requireCondition: true })
        currentMode.value = 'direct-auction'
        syncAuctionSchedule()
        return { type: 'step', mode: 'direct-auction' }
      } catch (error) {
        errorMessage.value = error?.message || '입력값을 확인해주세요.'
      }
      return
    }

    processing.value = true

    try {
      if (currentMode.value === 'inspection') {
        return await submitInspectionRegistration()
      } else if (currentMode.value === 'direct-auction') {
        if (registrationType.value === 'inspection') {
          return await submitInspectionAuctionRegistration()
        }

        return await submitDirectAuctionRegistration()
      }
    } catch (error) {
      errorMessage.value = error?.message || '등록 처리 중 오류가 발생했습니다.'
    } finally {
      processing.value = false
    }
  }

  function returnFromAuctionStep() {
    currentMode.value = registrationType.value === 'inspection' ? 'inspection-pick' : 'direct'
    clearMessages()
  }

  function toggleAuctionField(field) {
    auctionForm.value[field] = !auctionForm.value[field]

    if (field === 'timeDeal') {
      selectedDuration.value = auctionForm.value.timeDeal ? '12시간' : '5일'
      syncAuctionSchedule()
    }
  }

  function setAuctionStartDate(value) {
    auctionForm.value.startDateInput = value
    syncAuctionSchedule()
  }

  function setAuctionStartTime(value) {
    auctionForm.value.startTimeInput = value
    syncAuctionSchedule()
  }

  async function selectInspectionItem(index) {
    selectedInspectionId.value = index
    selectedInspectionDetail.value = null
    isInspectionDetailOpen.value = true

    const item = inspectionPickItems.value.find((candidate) => candidate.displayId === index)

    if (!item?.inspectionId) {
      return
    }

    try {
      selectedInspectionDetail.value = await getInspectionDetail(item.inspectionId)
    } catch {
      // 상세 조회 실패 시 목록 응답으로만 모달을 표시한다.
    }
  }

  function closeInspectionDetail() {
    isInspectionDetailOpen.value = false
  }

  function startAuctionFromInspection() {
    if (!selectedInspectionItem.value) {
      errorMessage.value = '경매 등록할 검수 완료 상품을 선택해주세요.'
      return
    }

    isInspectionDetailOpen.value = false
    currentMode.value = 'direct-auction'
    auctionForm.value.timeDeal = false
    clearMessages()
    syncAuctionSchedule()
  }

  watch(
    initialMode,
    (mode) => {
      currentMode.value = allowedModes.includes(mode) ? mode : 'select'
      registrationType.value = mode === 'inspection' || mode === 'inspection-pick' ? 'inspection' : 'direct'
      clearMessages()

      if (currentMode.value === 'inspection-pick' || (currentMode.value === 'direct-auction' && initialInspectionId?.value)) {
        loadInspectionPickItems()
      }
    },
    { immediate: true },
  )

  watch(selectedDuration, () => {
    syncAuctionSchedule()
  })

  watch(
    currentMode,
    (mode) => {
      if (mode === 'inspection-pick') {
        loadInspectionPickItems()
      }
    },
  )

  loadCategoryOptions()
  syncAuctionSchedule()

  onBeforeUnmount(() => {
    uploadedImages.value.forEach((image) => {
      if (image.previewUrl) {
        URL.revokeObjectURL(image.previewUrl)
      }
    })
  })

  return {
    auctionForm,
    bidUnitOptions,
    categoryOptions,
    closeInspectionDetail,
    currentMode,
    durationOptions,
    errorMessage,
    firstStepLabel,
    filteredInspectionPickItems,
    form,
    goBackToSelect,
    handleFiles,
    headerDescription,
    headerTitle,
    inspectionPickItems,
    isInspectionAuctionRegistration,
    isAuctionStep,
    isInspectionDetailOpen,
    openInspectionRequest,
    openMode,
    processing,
    removeImage,
    returnFromAuctionStep,
    selectedBidUnit,
    selectedDuration,
    selectedInspectionId,
    selectedInspectionItem,
    inspectionSearchQuery,
    selectInspectionItem,
    setInspectionSearchQuery,
    setAuctionStartDate,
    setAuctionStartTime,
    setPrimaryImage,
    showStepper,
    startAuctionFromInspection,
    submitted,
    submitForm,
    successMessage,
    thumbnailPlaceholders,
    toggleAuctionField,
    uploadInProgress,
    uploadedImages,
  }
}
