<template>
  <section class="page-header-block">
    <h1>프로필 관리</h1>
  </section>

  <SurfaceCard as="section" class="panel-card form-panel">
    <div class="profile-summary">
      <div class="profile-summary__avatar">
        <img :src="avatarPreview" :alt="displayNickname" class="avatar" @error="setDefaultAvatar" />
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          hidden
          @change="handleAvatarChange"
        />
        <button type="button" aria-label="프로필 이미지 선택" @click="openAvatarPicker">+</button>
      </div>
      <div class="profile-summary__content">
        <div class="profile-summary__name">
          <img v-if="badgeImage" :src="badgeImage" alt="회원 등급 뱃지" class="profile-grade-badge" />
          <button class="profile-summary__name-button" type="button" @click="emit('open-seller-modal')">
            <strong>{{ displayNickname }}</strong>
          </button>
        </div>
        <div class="profile-summary__meta profile-summary__meta--compact">
          <div class="profile-rating__stars" aria-label="사용자 평점">
            <v-icon
              v-for="star in ratingStars"
              :key="star"
              :icon="star <= roundedRating ? 'mdi-star' : 'mdi-star-outline'"
              :color="star <= roundedRating ? 'primary' : 'grey'"
              size="20"
            />
          </div>
          <span class="profile-rating__value">{{ displayRating }}</span>
          <span class="profile-rating__reviews">리뷰 {{ profile.reviews }}개</span>
          <span>가입일: {{ profile.joinedAt }}</span>
        </div>
      </div>
    </div>

    <div class="profile-section profile-section--readonly">
      <div class="form-grid">
        <label class="profile-field profile-field--readonly">
          <span>이름</span>
          <div class="form-grid__readonly form-grid__readonly--strong">{{ form.name || '-' }}</div>
        </label>
        <label class="profile-field profile-field--readonly">
          <span>이메일</span>
          <div class="form-grid__readonly form-grid__readonly--strong">{{ form.email || '-' }}</div>
        </label>
        <label class="profile-field profile-field--readonly profile-field--wide">
          <span>주소</span>
          <div class="form-grid__readonly form-grid__readonly--strong">{{ profile.addressLine || '-' }}</div>
        </label>
      </div>
    </div>

    <div class="profile-section profile-section--editable">
      <div class="form-grid">
        <label class="profile-field">
          <span>닉네임</span>
          <input v-model="form.nickname" type="text" />
        </label>
        <label class="profile-field bank-row">
          <span>계좌번호</span>
          <div class="bank-row__inputs">
            <v-menu location="bottom start" offset="12">
              <template #activator="{ props: menuProps }">
                <button class="sort-menu__trigger bank-menu__trigger" type="button" v-bind="menuProps">
                  <span>{{ currentBankLabel }}</span>
                  <v-icon icon="mdi-chevron-down" />
                </button>
              </template>

              <div class="sort-menu__panel bank-menu__panel">
                <button
                  v-for="bank in bankOptions"
                  :key="bank.code || 'none'"
                  class="sort-menu__item"
                  :class="{ 'sort-menu__item--active': form.bankCode === bank.code }"
                  type="button"
                  @click="selectBank(bank.code)"
                >
                  {{ bank.label }}
                </button>
              </div>
            </v-menu>
            <input v-model="form.bankAccount" type="text" placeholder="계좌번호를 입력해주세요" />
          </div>
        </label>
      </div>
    </div>

    <div class="form-actions">
      <button class="primary-button" type="button" @click="saveProfile">수정</button>
    </div>
  </SurfaceCard>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import defaultAvatar from '../../../assets/default-avatar.svg'
import SurfaceCard from '../../SurfaceCard.vue'
import { overviewUser } from '../../../data/mypage'
import { bankOptions, getBankLabel, normalizeBankCode } from '../../../utils/banks'
import { getGradeBadge } from '../../../utils/gradeBadge'

const props = defineProps({
  profile: {
    type: Object,
    default: () => overviewUser,
  },
})

const emit = defineEmits(['open-seller-modal', 'select-avatar', 'submit'])

const form = reactive({
  name: '',
  nickname: '',
  email: '',
  bankCode: '',
  bankAccount: '',
})

const avatarInput = ref(null)
const avatarPreview = ref(defaultAvatar)
const ratingStars = [1, 2, 3, 4, 5]

const badgeImage = computed(() => getGradeBadge(props.profile?.grade))

const roundedRating = computed(() => {
  const rating = Number(props.profile?.rating ?? 0)

  if (Number.isNaN(rating)) {
    return 0
  }

  return Math.max(0, Math.min(5, Math.round(rating)))
})

const displayRating = computed(() => {
  const rating = Number(props.profile?.rating ?? 0)

  if (Number.isNaN(rating)) {
    return '0.0'
  }

  return Math.max(0, Math.min(5, rating)).toFixed(1)
})

const currentBankLabel = computed(() => getBankLabel(form.bankCode) || '은행 선택')

const displayNickname = computed(() => form.nickname || props.profile.nickname || form.name)

function syncForm(profile = {}) {
  form.name = profile.name || ''
  form.nickname = profile.nickname || ''
  form.email = profile.email || ''
  form.bankCode = normalizeBankCode(profile.bankCode || profile.bank)
  form.bankAccount = profile.bankAccount || profile.accountNumber || ''
  avatarPreview.value = profile.avatar || defaultAvatar
}

function selectBank(bankCode) {
  form.bankCode = bankCode
}

function openAvatarPicker() {
  avatarInput.value?.click()
}

function handleAvatarChange(event) {
  const file = event.target?.files?.[0]

  if (!file) {
    return
  }

  const reader = new FileReader()

  reader.onload = () => {
    const previewUrl = typeof reader.result === 'string' ? reader.result : defaultAvatar
    avatarPreview.value = previewUrl
    emit('select-avatar', { file, previewUrl })
  }

  reader.readAsDataURL(file)
  event.target.value = ''
}

function setDefaultAvatar(event) {
  event.target.src = defaultAvatar
}

watch(
  () => props.profile,
  (profile) => {
    syncForm(profile)
  },
  { immediate: true },
)

function saveProfile() {
  emit('submit', {
    nickname: form.nickname.trim(),
    bankCode: form.bankCode,
    bankAccount: form.bankAccount.trim(),
  })
}
</script>
