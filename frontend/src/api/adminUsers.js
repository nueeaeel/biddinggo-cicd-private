import { buildQueryString, request, unwrap } from "./http"

export const ADMIN_MEMBER_STATUS = Object.freeze({
    PENDING: 'PENDING',
    ACTIVE: 'ACTIVE',
    INACTIVE: 'INACTIVE',
    DELETED: 'DELETED'
})

export async function fetchAdminUsers(params = {}) {
    const query = buildQueryString({
        page: params.page ?? 1,
        size: params.size ?? 20,
        order: params.order ?? 'DESC',
        keyword: params.keyword || undefined,
        status: params.status || undefined,
    })

    const data = await request(`/api/v1/admins/members${query}`, {
        auth: true,
        method: 'GET',
    })

    return unwrap(data)
}

export async function updateAdminUserStatus(memberId, status) {
    const data = await request(`/api/v1/admins/members/${encodeURIComponent(memberId)}`, {
        auth: true,
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({status})

    })
    
    return unwrap(data)
}