import { request, unwrap } from './http'

export async function createReport(payload) {
  const data = await request('/api/v1/reports', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return unwrap(data)
}
