<template>
  <section class="auth-page auth-page--callback">
    <article class="surface-card auth-card auth-card--callback">
      <h1>로그인 처리 중입니다.</h1>
      <p v-if="!errorMessage" class="auth-card__description">인증 정보를 확인하고 메인 화면으로 이동합니다.</p>
      <p v-else class="auth-card__error">{{ errorMessage }}</p>
      <button v-if="errorMessage" type="button" class="topbar-link-button" @click="router.replace('/login')">
        로그인 화면으로 돌아가기
      </button>
    </article>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import { useToast } from '../composables/useToast'

const router = useRouter()
const { auth, completeLoginFromCallback } = useAuth()
const { showToast } = useToast()
const errorMessage = ref('')

function normalizeMessage(error) {
  return String(error instanceof Error ? error.message : error || '')
    .trim()
    .replace(/\s+/g, ' ')
    .replace(/[.。]+$/g, '')
}

function isInvalidRefreshTokenError(error) {
  const message = normalizeMessage(error)

  return message.includes('리프레쉬 토큰이 유효하지 않습니다')
    || message.includes('리프리쉬 토큰이 유효하지 않습니다')
}

function isWithdrawnMemberError(error) {
  const message = normalizeMessage(error).toLowerCase()

  return message.includes('탈퇴')
    || message.includes('withdrawn')
    || message.includes('deleted member')
    || message.includes('deactivated')
}

onMounted(async () => {
  const currentRoute = router.currentRoute.value

  if (currentRoute.query?.error === 'login_blocked_member') {
    showToast('탈퇴하거나 정지된 회원은 로그인할 수 없습니다.', { color: 'error' })
    await router.replace('/login')
    return
  }

  try {
    await completeLoginFromCallback()
    await router.replace(auth.status === 'PENDING' ? '/profile/setup' : '/')
  } catch (error) {
    if (isWithdrawnMemberError(error) || isInvalidRefreshTokenError(error)) {
      showToast('탈퇴하거나 정지된 회원은 로그인할 수 없습니다.', { color: 'error' })
      await router.replace('/login')
      return
    }

    errorMessage.value = error instanceof Error ? error.message : '로그인 처리에 실패했습니다.'
  }
})
</script>
