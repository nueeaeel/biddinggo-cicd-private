import { request, unwrap } from './http'

export async function getCategoryList() {
  const data = await request('/api/v1/categories', {
    method: 'GET',
  })

  return unwrap(data)
}
