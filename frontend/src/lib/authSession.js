import { reactive, readonly } from 'vue'

const STORAGE_KEY = 'biddinggo.auth.session'

const state = reactive({
  initialized: false,
  isAuthenticated: false,
  accessToken: '',
  type: 'Bearer',
  memberId: null,
  username: '',
  name: '',
  nickname: '',
  imageUrl: '',
  status: '',
  authorities: [],
  issuedAt: 0,
  expiredAt: 0,
})

function normalizeAuthorities(authorities) {
  if (!Array.isArray(authorities)) {
    return []
  }

  return authorities
    .map((authority) => {
      if (typeof authority === 'string') {
        return authority
      }

      return authority?.authority || authority?.role || ''
    })
    .filter(Boolean)
}

function readStoredSession() {
  if (typeof window === 'undefined') {
    return null
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)

    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

function readJwtTimeClaims(accessToken) {
  if (typeof atob === 'undefined' || !accessToken) {
    return {}
  }

  const [, payload] = accessToken.split('.')

  if (!payload) {
    return {}
  }

  try {
    const normalizedPayload = payload.replace(/-/g, '+').replace(/_/g, '/')
    const paddedPayload = normalizedPayload.padEnd(Math.ceil(normalizedPayload.length / 4) * 4, '=')
    const claims = JSON.parse(atob(paddedPayload))

    return {
      issuedAt: Number(claims.iat || 0) * 1000,
      expiredAt: Number(claims.exp || 0) * 1000,
      status: String(claims.status || ''),
    }
  } catch {
    return {}
  }
}

function persistSession() {
  if (typeof window === 'undefined') {
    return
  }

  if (!state.accessToken) {
    window.localStorage.removeItem(STORAGE_KEY)
    return
  }

  window.localStorage.setItem(
    STORAGE_KEY,
    JSON.stringify({
      accessToken: state.accessToken,
      type: state.type,
      memberId: state.memberId,
      username: state.username,
      name: state.name,
      nickname: state.nickname,
      imageUrl: state.imageUrl,
      status: state.status,
      authorities: state.authorities,
      issuedAt: state.issuedAt,
      expiredAt: state.expiredAt,
    }),
  )
}

function applySession(snapshot = {}) {
  const source = snapshot && typeof snapshot === 'object' ? snapshot : {}
  const accessToken = String(source.accessToken || '').trim()
  const jwtTimeClaims = readJwtTimeClaims(accessToken)

  state.accessToken = accessToken
  state.type = String(source.type || 'Bearer')
  state.memberId = source.memberId === null || source.memberId === undefined ? null : Number(source.memberId)
  state.username = String(source.username || '')
  state.name = String(source.name || '')
  state.nickname = String(source.nickname || '')
  state.imageUrl = String(source.imageUrl || '')
  state.status = String(source.status || jwtTimeClaims.status || '')
  state.authorities = normalizeAuthorities(source.authorities)
  state.issuedAt = Number(source.issuedAt || jwtTimeClaims.issuedAt || 0)
  state.expiredAt = Number(source.expiredAt || jwtTimeClaims.expiredAt || 0)
  state.isAuthenticated = Boolean(state.accessToken)
}

applySession(readStoredSession())

export const authState = readonly(state)

export function getAccessToken() {
  return state.accessToken
}

export function shouldRefreshAccessToken() {
  if (!state.accessToken) {
    return true
  }

  if (!state.expiredAt) {
    return false
  }

  return state.expiredAt <= Date.now() + 30_000
}

export function setSession(loginResponse = {}) {
  applySession({
    accessToken: loginResponse.accessToken ?? state.accessToken,
    type: loginResponse.type ?? state.type,
    memberId: loginResponse.memberId ?? state.memberId,
    username: loginResponse.username ?? state.username,
    name: loginResponse.name ?? state.name,
    nickname: loginResponse.nickname ?? state.nickname,
    imageUrl: loginResponse.imageUrl ?? state.imageUrl,
    status: loginResponse.status ?? state.status,
    authorities: loginResponse.authorities ?? state.authorities,
    issuedAt: loginResponse.issuedAt ?? state.issuedAt,
    expiredAt: loginResponse.expiredAt ?? state.expiredAt,
  })
  state.initialized = true
  persistSession()
}

export function setUserInfo(userInfo = {}) {
  state.memberId = userInfo.memberId === null || userInfo.memberId === undefined ? state.memberId : Number(userInfo.memberId)
  state.username = String(userInfo.username || state.username || '')
  state.name = String(userInfo.name || state.name || '')
  state.nickname = String(userInfo.nickname || userInfo.nickName || state.nickname || '')
  state.imageUrl = String(userInfo.imageUrl || state.imageUrl || '')
  state.status = String(userInfo.status || state.status || '')
  state.authorities = normalizeAuthorities(userInfo.role ?? state.authorities)
  state.isAuthenticated = Boolean(state.accessToken)
  state.initialized = true
  persistSession()
}

export function clearSession() {
  applySession()
  state.initialized = true
  persistSession()
}

export function markInitialized() {
  state.initialized = true
}
