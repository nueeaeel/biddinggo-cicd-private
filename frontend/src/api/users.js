import { buildQueryString, request, unwrap } from './http'

export async function getUserDashboard() {
  const data = await request(`/api/v1/members/me`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function getUserProfile() {
  const data = await request(`/api/v1/members/me/profile`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function updateUserProfile(payload) {
  const data = await request('/api/v1/members/me/profile', {
    auth: true,
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function getUserSellerProfile(userId) {
  const data = await request(`/api/v1/members/${userId}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function deleteMemberAccount() {
  return request('/api/v1/members/me', {
    auth: true,
    method: 'DELETE',
  })
}

export async function getUserBids(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/members/me/bids${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function getUserWishlists(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/wishlists${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function getUserPurchases(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/winner-deals/purchases${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function getUserSales(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/winner-deals/sales${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function getUserManagedAuctions(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/members/me/sales/auctions${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function getWinnerDealDetail(winnerDealId) {
  const data = await request(`/api/v1/winner-deals/${winnerDealId}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function updateWinnerDealShippingAddress(winnerDealId, payload) {
  const data = await request(`/api/v1/winner-deals/${winnerDealId}/shipping-address`, {
    auth: true,
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function updateWinnerDealTrackingNumber(winnerDealId, payload) {
  const data = await request(`/api/v1/winner-deals/${winnerDealId}/tracking-number`, {
    auth: true,
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function confirmWinnerDeal(winnerDealId) {
  const data = await request(`/api/v1/winner-deals/${winnerDealId}/confirm`, {
    auth: true,
    method: 'PATCH',
  })

  return unwrap(data)
}

export async function getUserAuctionInquiries(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/members/me/auction-inquiries${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function getUserDirectInquiries(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/direct-inquiries${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function createUserDirectInquiry(payload) {
  const data = await request('/api/v1/direct-inquiries', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function createAuctionInquiryAnswer(inquiryId, payload) {
  const data = await request(`/api/v1/inquiries/${inquiryId}`, {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function getUserAddresses(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/addresses${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function createUserAddress(payload) {
  const data = await request('/api/v1/addresses', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function deleteUserAddress(addressId) {
  const data = await request(`/api/v1/addresses/${addressId}`, {
    auth: true,
    method: 'DELETE',
  })

  return unwrap(data)
}

export async function setDefaultUserAddress(addressId) {
  const data = await request(`/api/v1/addresses/${addressId}`, {
    auth: true,
    method: 'PATCH',
  })

  return unwrap(data)
}

export async function getUserPoints(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/points/me${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function exchangeUserPoints(payload) {
  const data = await request('/api/v1/points/exchanges', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function createVirtualAccount(payload) {
  const data = await request('/api/v1/payments/virtual-accounts', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function getVirtualAccounts() {
  const data = await request('/api/v1/payments/virtual-accounts', {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}
