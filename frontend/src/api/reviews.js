import { buildQueryString, request, unwrap } from './http'

export async function getSellerReviews(sellerId, params = {}) {
  const suffix = buildQueryString(params)
  const data = await request(`/api/v1/auctions/${sellerId}/reviews${suffix}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function createAuctionReview(auctionId, payload) {
  const data = await request(`/api/v1/auctions/${auctionId}/reviews`, {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}
