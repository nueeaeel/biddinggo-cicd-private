import { API_BASE_URL, request, unwrap } from './http'

export function buildSocialLoginUrl(provider) {
  return `${API_BASE_URL}/oauth2/authorization/${provider}`
}

export async function checkAuthStatus() {
  return unwrap(
    await request('/api/v1/auth/check', {
      auth: true,
      headers: {
        Accept: 'application/json',
      },
    }),
  )
}

export async function refreshAccessToken() {
  return unwrap(
    await request('/api/v1/auth/refresh', {
      method: 'POST',
    }),
  )
}

export async function logoutAuth() {
  return request('/api/v1/auth/logout', {
    auth: true,
    method: 'POST',
  })
}

export async function registerRequiredUserInfo(payload) {
  return unwrap(
    await request('/api/v1/auth/register', {
      auth: true,
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    }),
  )
}

export async function loginAdmin(payload) {

  return unwrap(
    await request('/api/v1/admin/auth/login', {
      method : 'POST',
      headers : {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(payload),
    }),
  )

}
