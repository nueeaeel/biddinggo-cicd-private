import { buildQueryString, request, unwrap } from "./http"

export const ADMIN_INSPECTION_STATUS = Object.freeze({
    PENDING: 'PENDING',
    PASSED: 'PASSED',
    FAILED: 'FAILED',
})

export async function fetchAdminInspections(params = {}) {
    const query = buildQueryString({
        page: params.page ?? 1,
        size: params.size ?? 20,
        order: params.order ?? 'DESC',
        status: params.status || undefined,
        name: params.name || undefined,
    })

    const data = await request(`/api/v1/admins/inspections${query}`, {
        auth: true,
        method: 'GET',
    })

    return unwrap(data)
}

export async function processAdminInspection(inspectionId, payload = {}) {
    const data = await request(`/api/v1/admins/inspections/${encodeURIComponent(inspectionId)}`, {

        auth: true,
        method: 'PATCH',
        headers : {
        
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            approved: Boolean(payload.approved),
            quality: payload.quality ?? null,
            failureReason: payload.failureReason ?? null,
        }),

    })

    return unwrap(data)
}

export async function approveAdminInspection(inspectionId, quality) {
    return processAdminInspection(inspectionId, {
        approved: true,
        quality,
        failureReason: null,
    })
}

export async function rejectAdminInspection(inspectionId, failureReason) {
    return processAdminInspection(inspectionId, {
        approved: false,
        quality: null,
        failureReason: failureReason ?? null,
    })
}