`<template>
  <main class="login-page profile-setup-page">
    <div class="login-page__orb login-page__orb--top" aria-hidden="true"></div>
    <div class="login-page__orb login-page__orb--bottom" aria-hidden="true"></div>

    <section class="login-layout profile-setup-layout" aria-label="필수 프로필 설정">
      <aside class="login-visual" :style="{ '--login-visual-image': `url('${visualImageUrl}')` }" aria-hidden="true">
        <p class="login-visual__brand">Biddinggo</p>

        <div class="login-visual__message">
          <span class="login-visual__label">THE DIGITAL CURATOR</span>
          <h1>
            대한민국 경매
            <strong>한 곳에서 확인 하세요</strong>
          </h1>
          <p>놓치면 아까운 오늘 최저가 매물, 지금 확인하세요.</p>
        </div>

        <div class="login-visual__pagination">
          <span></span>
          <span></span>
          <span></span>
          <span></span>
        </div>
      </aside>

      <section class="profile-setup-card" aria-labelledby="profile-setup-title">
        <div class="profile-setup-copy">
          <h1 id="profile-setup-title">필수 프로필 설정</h1>
          <p>가입을 위해 필수 정보를 입력해주세요.</p>
        </div>

        <form class="profile-setup-form" @submit.prevent="submitProfile">
          <div class="profile-setup-avatar-block">
            <input
              ref="imageInput"
              class="profile-setup-file"
              type="file"
              accept="image/*"
              @change="handleImageSelected"
            />
            <button
              type="button"
              class="profile-setup-avatar"
              :disabled="uploadingImage || submitting"
              @click="imageInput?.click()"
            >
              <img v-if="imagePreviewUrl" :src="imagePreviewUrl" alt="프로필 이미지 미리보기" />
              <span v-else>{{ avatarInitial }}</span>
            </button>
            <p>프로필 사진 설정</p>
          </div>

          <label class="profile-setup-field">
            <span>이름 <b>*</b></span>
            <input v-model.trim="form.name" type="text" placeholder="실명을 입력하세요" autocomplete="name" />
          </label>

          <label class="profile-setup-field">
            <span>닉네임 <b>*</b></span>
            <input v-model.trim="form.nickname" type="text" placeholder="사용하실 닉네임을 입력하세요" autocomplete="nickname" />
          </label>

          <div class="profile-setup-agreements" aria-label="필수 약관 동의">
            <label>
              <input v-model="agreements.terms" type="checkbox" />
              <span>이용 약관 동의 (필수)</span>
            </label>
            <label>
              <input v-model="agreements.privacy" type="checkbox" />
              <span>개인정보 처리방침 동의 (필수)</span>
            </label>
          </div>

          <p v-if="errorMessage" class="profile-setup-error">{{ errorMessage }}</p>

          <button type="submit" class="profile-setup-submit" :disabled="submitting || uploadingImage || !canSubmit">
            {{ submitting ? '저장 중...' : '가입 완료' }}
          </button>
        </form>
      </section>
    </section>

    <p class="login-footer">© 2026 team biddingmate</p>
  </main>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { checkAuthStatus, refreshAccessToken, registerRequiredUserInfo } from '../api/auth'
import { requestPresignedUpload, uploadToPresignedUrl } from '../api/files'
import { useAuth } from '../composables/useAuth'
import { setSession, setUserInfo } from '../lib/authSession'

const visualImageUrl = 'https://www.figma.com/api/mcp/asset/518d0ee7-8b01-4d37-b678-cc5f74b81574'
const router = useRouter()
const route = useRoute()
const { auth } = useAuth()
const imageInput = ref(null)
const imagePreviewUrl = ref(auth.imageUrl || '')
const errorMessage = ref('')
const submitting = ref(false)
const uploadingImage = ref(false)

const form = reactive({
  name: auth.name || '',
  nickname: auth.nickname || '',
  imageUrl: auth.imageUrl || '',
})

const agreements = reactive({
  terms: false,
  privacy: false,
})

const avatarInitial = computed(() => (
  form.nickname?.charAt(0) || form.name?.charAt(0) || auth.username?.charAt(0) || 'B'
).toUpperCase())

const canSubmit = computed(() => (
  Boolean(form.name && form.nickname && agreements.terms && agreements.privacy)
))

async function handleImageSelected(event) {
  const [file] = Array.from(event?.target?.files || [])

  if (!file) {
    return
  }

  if (!file.type.startsWith('image/')) {
    errorMessage.value = '이미지 파일만 업로드할 수 있습니다.'
    return
  }

  uploadingImage.value = true
  errorMessage.value = ''

  try {
    const presigned = await requestPresignedUpload(file)
    await uploadToPresignedUrl(presigned.uploadUrl, file)
    form.imageUrl = presigned.publicUrl
    imagePreviewUrl.value = URL.createObjectURL(file)
  } catch (error) {
    errorMessage.value = error?.message || '프로필 이미지 업로드에 실패했습니다.'
  } finally {
    uploadingImage.value = false
    if (imageInput.value) {
      imageInput.value.value = ''
    }
  }
}

async function submitProfile() {
  if (!canSubmit.value) {
    errorMessage.value = '필수 정보를 모두 입력하고 약관에 동의해주세요.'
    return
  }

  submitting.value = true
  errorMessage.value = ''

  try {
    await ensureAvatarUrl()

    await registerRequiredUserInfo({
      name: form.name,
      nickname: form.nickname,
      imageUrl: form.imageUrl,
    })

    try {
      setSession(await refreshAccessToken())
    } catch {
      setUserInfo(await checkAuthStatus())
    }

    const redirect = typeof route.query.redirect === 'string'
      && route.query.redirect.startsWith('/')
      && route.query.redirect !== '/profile/setup'
      ? route.query.redirect
      : '/'

    await router.replace(redirect)
  } catch (error) {
    errorMessage.value = error?.message || '필수 정보 저장에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}

async function createInitialAvatarFile(initial) {
  const letter = String(initial || 'B').slice(0, 1).toUpperCase()
  const seed = String(form.nickname || form.name || auth.username || letter)
  const bgColor = pickAvatarColor(seed)
  const size = 256

  const canvas = document.createElement('canvas')
  canvas.width = size
  canvas.height = size

  const ctx = canvas.getContext('2d')
  if (!ctx) {
    throw new Error('아바타 이미지를 생성할 수 없습니다.')
  }

  ctx.fillStyle = bgColor
  ctx.fillRect(0, 0, size, size)

  ctx.fillStyle = '#ffffff'
  ctx.font = '700 120px Pretendard, Arial, sans-serif'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(letter, size / 2, size / 2 + 8)

  const blob = await new Promise((resolve, reject) => {
    canvas.toBlob((value) => {
      if (value) {
        resolve(value)
      } else {
        reject(new Error('PNG 변환에 실패했습니다.'))
      }
    }, 'image/png')
  })

  return new File([blob], `avatar-${Date.now()}.png`, { type: 'image/png' })
}

async function ensureAvatarUrl() {
  if (form.imageUrl) return

  const fallbackFile = await createInitialAvatarFile(avatarInitial.value)
  imagePreviewUrl.value = URL.createObjectURL(fallbackFile)

  const presigned = await requestPresignedUpload(fallbackFile)
  await uploadToPresignedUrl(presigned.uploadUrl, fallbackFile)
  form.imageUrl = presigned.publicUrl
}

const AVATAR_COLORS = [
  '#FFFFFF', '#8FA6FF', '#B8A4F4', '#6D5BD0'
]

function pickAvatarColor(seed = '') {
  let hash = 0
  for (let i = 0; i < seed.length; i += 1) {
    hash = (hash * 31 + seed.charCodeAt(i)) | 0
  }
  return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length]
}

</script>