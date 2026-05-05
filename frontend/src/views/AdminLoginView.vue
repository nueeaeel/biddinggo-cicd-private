<template>
  <main class="admin-login-page">
    <div class="admin-login-page__orb admin-login-page__orb--top" aria-hidden="true"></div>
    <div class="admin-login-page__orb admin-login-page__orb--bottom" aria-hidden="true"></div>

    <section class="admin-login-page__card" aria-labelledby="admin-login-title">
      <header class="admin-login-page__header">
        <p class="admin-login-page__brand">Biddinggo</p>
        <h1 id="admin-login-title">관리자 로그인</h1>
      </header>

      <form class="admin-login-page__form" @submit.prevent="submit">
        <label class="admin-login-page__field">
          <span>아이디</span>
          <div class="admin-login-page__input-wrap">
            <span class="admin-login-page__prefix"><i class="mdi mdi-at"></i></span>
            <input
              v-model.trim="form.username"
              type="text"
              autocomplete="username"
              placeholder="curator123"
              required
            />
          </div>
        </label>

        <label class="admin-login-page__field">
          <span>패스워드</span>
          <div class="admin-login-page__input-wrap">
            <span class="admin-login-page__prefix"><i class="mdi mdi-lock-outline"></i></span>
            <input
              v-model="form.password"
              :type="isPasswordVisible ? 'text' : 'password'"
              autocomplete="current-password"
              placeholder="••••••••••••"
              required
            />
            <button
              type="button"
              class="admin-login-page__toggle"
              :aria-label="isPasswordVisible ? '비밀번호 숨기기' : '비밀번호 보기'"
              @click="isPasswordVisible = !isPasswordVisible"
            >
              <i :class="isPasswordVisible ? 'mdi mdi-eye-off-outline' : 'mdi mdi-eye-outline'"></i>
            </button>
          </div>
        </label>

        <p class="admin-login-page__help">아이디 및 패스워드 분실시 영업지원팀에 문의해주세요.</p>
        <p v-if="errorMessage" class="admin-login-page__error">{{ errorMessage }}</p>

        <button class="admin-login-page__submit" type="submit" :disabled="isSubmitting || !canSubmit">
          {{ isSubmitting ? '로그인 중...' : '로그인하기' }}
        </button>

        <div class="admin-login-page__divider" aria-hidden="true"></div>
      </form>
    </section>
  </main>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loginAdmin } from '../api/auth'
import { setSession } from '../lib/authSession'

const router = useRouter()
const route = useRoute()

const form = reactive({
  username: '',
  password: '',
})

const isSubmitting = ref(false)
const isPasswordVisible = ref(false)
const errorMessage = ref('')

const canSubmit = computed(() => form.username.trim().length > 0 && form.password.length > 0)

function hasAdminAuthority(authorities = []) {
  if (!Array.isArray(authorities)) {
    return false
  }

  return authorities.some((authority) => {
    const normalized = String(authority || '').toUpperCase()
    return normalized === 'ROLE_ADMIN' || normalized === 'ADMIN'
  })
}

async function submit() {
  if (!canSubmit.value || isSubmitting.value) {
    return
  }

  isSubmitting.value = true
  errorMessage.value = ''

  try {
    const loginResponse = await loginAdmin({
      username: form.username.trim(),
      password: form.password,
    })

    if (!hasAdminAuthority(loginResponse?.authorities)) {
      errorMessage.value = '관리자 계정만 접근할 수 있습니다.'
      return
    }

    setSession(loginResponse)

    const redirectPath = typeof route.query.redirect === 'string'
      ? route.query.redirect
      : '/admin/transactions'

    router.replace(redirectPath)
  } catch (error) {
    errorMessage.value = String(error?.message || '관리자 로그인에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  position: relative;
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  overflow: hidden;
  background: #fdf7ff;
}

.admin-login-page__orb {
  position: absolute;
  border-radius: 9999px;
  pointer-events: none;
}

.admin-login-page__orb--top {
  top: -102px;
  left: -128px;
  width: 512px;
  height: 410px;
  background: #f1ebf9;
  opacity: 0.6;
  filter: blur(60px);
}

.admin-login-page__orb--bottom {
  right: -64px;
  bottom: -40px;
  width: 384px;
  height: 307px;
  background: rgba(40, 0, 123, 0.05);
  filter: blur(50px);
}

.admin-login-page__card {
  position: relative;
  z-index: 1;
  width: min(548px, 100%);
  min-height: 722px;
  border-radius: 32px;
  background: #fff;
  box-shadow: 0 20px 40px rgba(40, 0, 123, 0.06);
  padding: 64px;
}

.admin-login-page__header {
  display: grid;
  gap: 16px;
  padding-bottom: 40px;
}

.admin-login-page__brand {
  margin: 0;
  color: #000;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 30px;
  font-weight: 900;
  line-height: 36px;
  letter-spacing: 0.4px;
}

.admin-login-page__header h1 {
  margin: 0;
  color: #1c1a23;
  font-size: 40px;
  font-weight: 800;
  line-height: 36px;
  letter-spacing: 0.4px;
}

.admin-login-page__form {
  display: grid;
  gap: 24px;
}

.admin-login-page__field {
  display: grid;
  gap: 8px;
}

.admin-login-page__field > span {
  color: #484554;
  font-size: 14px;
  line-height: 20px;
  padding: 0 4px;
}

.admin-login-page__input-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  border-radius: 12px;
  background: #f7f1fe;
  padding: 0 12px;
}

.admin-login-page__prefix {
  color: #484554;
  font-size: 18px;
}

.admin-login-page__input-wrap input {
  border: 0;
  outline: 0;
  background: transparent;
  flex: 1;
  height: 56px;
  color: #1c1a23;
  font-size: 16px;
}

.admin-login-page__input-wrap input::placeholder {
  color: rgba(121, 117, 134, 0.5);
}

.admin-login-page__toggle {
  border: 0;
  background: transparent;
  color: #6d6a79;
  cursor: pointer;
  font-size: 18px;
}

.admin-login-page__help {
  margin: 0;
  color: #484554;
  font-size: 11px;
  line-height: 20px;
  text-align: center;
}

.admin-login-page__error {
  margin: 0;
  color: #b42318;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
}

.admin-login-page__submit {
  border: 0;
  border-radius: 12px;
  min-height: 56px;
  background: linear-gradient(135deg, #28007b 0%, #3f00b5 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow:
    0 10px 15px -3px rgba(40, 0, 123, 0.2),
    0 4px 6px -4px rgba(40, 0, 123, 0.2);
}

.admin-login-page__submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.admin-login-page__divider {
  margin-top: 12px;
  border-top: 1px solid rgba(202, 195, 215, 0.3);
}

@media (max-width: 640px) {
  .admin-login-page {
    padding: 20px;
  }

  .admin-login-page__card {
    padding: 36px 24px;
    min-height: auto;
  }

  .admin-login-page__header h1 {
    font-size: 32px;
  }
}
</style>
