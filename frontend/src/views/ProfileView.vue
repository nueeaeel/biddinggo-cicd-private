<template>
  <ProfileScreen
    :profile="profile"
    @open-seller-modal="openSellerModal"
    @select-avatar="selectAvatar"
    @submit="updateProfile"
  />

  <SellerProfileModal
    v-if="isSellerModalOpen"
    :assets="sellerModalAssets"
    :item="sellerModalItem"
    :seller-id="sellerModalUserId"
    :seller-profile="sellerModalProfile"
    @close="closeSellerModal"
  />
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { deleteUploadedFile, requestPresignedUpload, uploadToPresignedUrl } from '../api/files'
import { getUserProfile, getUserSellerProfile, updateUserProfile } from '../api/users'
import defaultAvatar from '../assets/default-avatar.svg'
import SellerProfileModal from '../components/auction-detail/SellerProfileModal.vue'
import ProfileScreen from '../components/mypage/profile/ProfileScreen.vue'
import { useToast } from '../composables/useToast'
import { overviewUser } from '../data/mypage'
import { authState, setUserInfo } from '../lib/authSession'
import { getGradeBadge } from '../utils/gradeBadge'
import { formatShortDate } from '../utils/marketplace'

const { showToast } = useToast()

const isSellerModalOpen = ref(false)
const sellerModalState = ref(null)
const sellerModalUserId = ref(null)
const uploadedAvatarUrl = ref('')
const uploadedAvatarFileKey = ref('')

const profile = ref({
  name: overviewUser.name,
  nickname: overviewUser.nickname ?? '',
  email: overviewUser.email ?? '',
  bankCode: overviewUser.bankCode ?? '',
  bankAccount: overviewUser.bankAccount ?? '',
  addressLine: '',
  avatar: overviewUser.avatar || defaultAvatar,
  imageUrl: overviewUser.imageUrl ?? overviewUser.avatar ?? defaultAvatar,
  rating: overviewUser.rating,
  reviews: overviewUser.reviews,
  joinedAt: overviewUser.joinedAt,
  grade: overviewUser.grade ?? 'BRONZE',
})

const sellerModalAssets = {}

const sellerModalItem = computed(() => ({
  seller: sellerModalState.value?.nickname || profile.value.nickname || profile.value.name || '-',
  sellerGrade: sellerModalState.value?.grade || profile.value.grade || '-',
  sellerId: authState.memberId || '-',
}))

const sellerModalProfile = computed(() => ({
  avatar: sellerModalState.value?.avatar || profile.value.avatar || defaultAvatar,
  badge: getGradeBadge(sellerModalState.value?.grade || profile.value.grade),
  rating: sellerModalState.value?.rating ?? profile.value.rating ?? 0,
  reviewCount: sellerModalState.value?.reviewCount ?? profile.value.reviews ?? 0,
  joinedAt: sellerModalState.value?.joinedAt || profile.value.joinedAt || '-',
  totalSalesCount: sellerModalState.value?.totalSalesCount ?? '-',
  cancelCount: sellerModalState.value?.cancelCount ?? '-',
  responseRate: sellerModalState.value?.responseRate ?? '-',
  reviews: [],
}))

function normalizeUserProfile(user = {}) {
  const addressLine = [user.address, user.detailAddress].filter(Boolean).join(' ').trim()

  return {
    name: user.name || user.username || overviewUser.name,
    nickname: user.nickname || overviewUser.nickname || '',
    email: user.email || overviewUser.email || '',
    bankCode: user.bankCode || user.bank || user.bankName || overviewUser.bankCode || '',
    bankAccount: user.bankAccount || overviewUser.bankAccount || '',
    addressLine,
    avatar: user.imageUrl || user.profileImageUrl || overviewUser.avatar || defaultAvatar,
    imageUrl: user.imageUrl || user.profileImageUrl || overviewUser.imageUrl || overviewUser.avatar || defaultAvatar,
    rating: user.ratingAvg ?? user.rating ?? overviewUser.rating,
    reviews: user.reviewCount ?? user.reviews ?? overviewUser.reviews,
    joinedAt: formatShortDate(user.createdAt) || overviewUser.joinedAt,
    grade: user.grade || overviewUser.grade || 'BRONZE',
  }
}

function normalizeSellerProfile(user = {}) {
  return {
    nickname: user.nickname || profile.value.nickname || profile.value.name || '-',
    avatar: user.imageUrl || profile.value.avatar || defaultAvatar,
    grade: user.grade || profile.value.grade || 'BRONZE',
    joinedAt: formatShortDate(user.createdAt) || profile.value.joinedAt || '-',
    rating: user.rating ?? 0,
    reviewCount: user.reviewCount ?? 0,
    totalSalesCount: user.totalSales ?? '-',
    cancelCount: user.cancelCount ?? '-',
    responseRate: user.responseRate === null || user.responseRate === undefined ? '-' : `${user.responseRate}%`,
  }
}

async function loadUserProfile() {
  try {
    const userProfile = await getUserProfile()
    profile.value = normalizeUserProfile(userProfile)
  } catch {
    profile.value = normalizeUserProfile()
  }
}

async function openSellerModal() {
  const userId = Number(authState.memberId)

  if (!Number.isFinite(userId)) {
    showToast('판매자 정보를 불러올 수 없습니다.', { color: 'error' })
    return
  }

  try {
    const sellerProfile = await getUserSellerProfile(userId)
    sellerModalUserId.value = userId
    sellerModalState.value = normalizeSellerProfile(sellerProfile)
    isSellerModalOpen.value = true
  } catch (error) {
    showToast(error?.message || '판매자 정보를 불러오지 못했습니다.', { color: 'error' })
  }
}

function closeSellerModal() {
  isSellerModalOpen.value = false
  sellerModalUserId.value = null
}

async function selectAvatar(payload) {
  if (!payload?.file || !payload?.previewUrl) {
    return
  }

  const previousAvatar = profile.value.avatar

  profile.value = {
    ...profile.value,
    avatar: payload.previewUrl,
  }

  try {
    if (uploadedAvatarFileKey.value) {
      await deleteUploadedFile(uploadedAvatarFileKey.value)
      uploadedAvatarFileKey.value = ''
      uploadedAvatarUrl.value = ''
    }

    const presigned = await requestPresignedUpload(payload.file)
    await uploadToPresignedUrl(presigned.uploadUrl, payload.file)

    uploadedAvatarFileKey.value = presigned.fileKey || ''
    uploadedAvatarUrl.value = presigned.publicUrl || payload.previewUrl
    profile.value = {
      ...profile.value,
      avatar: payload.previewUrl,
    }
    showToast('프로필 이미지가 업로드되었습니다.')
  } catch (error) {
    profile.value = {
      ...profile.value,
      avatar: previousAvatar,
    }
    uploadedAvatarFileKey.value = ''
    uploadedAvatarUrl.value = ''
    showToast(error?.message || '프로필 이미지 업로드에 실패했습니다.', { color: 'error' })
  }
}

async function updateProfile(payload) {
  try {
    const nextNickname = String(payload?.nickname || '').trim()
    const shouldSyncHeaderNickname = Boolean(nextNickname) && nextNickname !== String(authState.nickname || '').trim()
    const nextPayload = {
      ...payload,
      ...(uploadedAvatarUrl.value ? { url: uploadedAvatarUrl.value, imageUrl: uploadedAvatarUrl.value } : {}),
    }
    const updatedProfile = await updateUserProfile(nextPayload)
    profile.value = normalizeUserProfile({
      ...profile.value,
      ...nextPayload,
      ...updatedProfile,
    })
    if (shouldSyncHeaderNickname) {
      setUserInfo({ nickname: nextNickname })
    }
    uploadedAvatarUrl.value = ''
    showToast('프로필 정보가 수정되었습니다.')
  } catch (error) {
    showToast(error?.message || '프로필 정보 수정에 실패했습니다.', { color: 'error' })
  }
}

onMounted(() => {
  loadUserProfile()
})
</script>
