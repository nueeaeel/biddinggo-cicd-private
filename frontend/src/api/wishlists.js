import { request, unwrap } from './http'

export async function getWishlistStatus(auctionId) {
  const data = await request(`/api/v1/wishlists/status?auctionId=${encodeURIComponent(auctionId)}`, {
    auth: true,
    method: 'GET',
  })

  return unwrap(data)
}

export async function createWishlist(auctionId) {
  const data = await request('/api/v1/wishlists', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ auctionId }),
  })

  return unwrap(data)
}

export async function deleteWishlist(auctionId) {
  const data = await request('/api/v1/wishlists', {
    auth: true,
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ auctionId }),
  })

  return unwrap(data)
}
