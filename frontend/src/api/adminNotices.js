import { buildQueryString, request, unwrap } from "./http";

export async function fetchAdminNotices(params = {}) {
    const query = buildQueryString({
        page: params.page ?? 1,
        size: params.size ?? 50,
        order: params.order ?? 'DESC',
    })

    return unwrap(
        await request(`/api/v1/admins/notices${query}`, {
            auth:true,
            method: 'GET',
        })
    )
}

export async function createAdminNotice(payload) {
    return unwrap(
        await request('/api/v1/admins/notices', {
            auth: true,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload),
        }),
    )
}

export async function updateAdminNotice(noticeId, payload) {
    return unwrap(
    await request(`/api/v1/admins/notices/${encodeURIComponent(noticeId)}`, {

        auth: true,
        method: 'PUT',
        headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
    }),
    )
}

export async function deleteAdminNotice(noticeId) {
    await request(`/api/v1/admins/notices/${encodeURIComponent(noticeId)}`, {
    auth: true,
    method: 'DELETE',
    })
}

