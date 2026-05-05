import { request, unwrap } from './http'

export async function requestPresignedUpload(file) {
  const data = await request('/api/v1/files/presigned-upload', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      originalFilename: file.name,
      contentType: file.type || 'image/jpeg',
    }),
  })

  return unwrap(data)
}

export async function uploadToPresignedUrl(uploadUrl, file) {
  const response = await fetch(uploadUrl, {
    method: 'PUT',
    headers: {
      'Content-Type': file.type || 'image/jpeg',
    },
    body: file,
  })

  if (!response.ok) {
    throw new Error(`이미지 업로드 실패 (${response.status})`)
  }
}

export async function deleteUploadedFile(fileKey) {
  const data = await request('/api/v1/files/delete', {
    auth: true,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ fileKey }),
  })

  return unwrap(data)
}
