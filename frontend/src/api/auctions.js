import { buildQueryString, request, unwrap } from './http'

export async function getAuctionList(params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/auctions${suffix}`, {
    method: 'GET',
  })

  return unwrap(data)
}

export async function searchAuctions(query, params = {}) {
  const suffix = buildQueryString({
    q: query,
    ...params,
  })
  const data = await request(`/api/v1/auctions/search/semantic${suffix}`, {
    method: 'GET',
  })

  return unwrap(data)
}

export async function getAuctionDetail(auctionId) {
  const data = await request(`/api/v1/auctions/${auctionId}`, {
    method: 'GET',
  })

  return unwrap(data)
}

export async function createAuction(payload) {
  const data = await request('/api/v1/auctions', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function updateAuction(auctionId, payload) {
  const data = await request(`/api/v1/auctions/${auctionId}`, {
    auth: true,
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function cancelAuction(auctionId) {
  const data = await request(`/api/v1/auctions/${auctionId}/cancel`, {
    auth: true,
    method: 'PATCH',
  })

  return unwrap(data)
}

export async function buyNowAuction(auctionId) {
  const data = await request(`/api/v1/auctions/${auctionId}/buy-now`, {
    auth: true,
    method: 'POST',
  })

  return unwrap(data)
}

export async function createAuctionFromInspectionItem(payload) {
  const data = await request('/api/v1/auctions/inspection-items', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}
