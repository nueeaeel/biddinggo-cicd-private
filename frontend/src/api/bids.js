import { buildQueryString, request, unwrap } from './http'

export async function createBid(memberId, payload) {
  const suffix = buildQueryString({ memberId })

  const data = await request(`/api/v1/bids${suffix}`, {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}

export async function getAuctionBids(auctionId, params = {}) {
  const suffix = buildQueryString({ auctionId, ...params })
  const data = await request(`/api/v1/bids${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}
