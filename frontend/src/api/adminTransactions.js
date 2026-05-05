import { buildQueryString, request, unwrap } from "./http";

export async function fetchAdminTransactions(params ={}) {
    const query = buildQueryString({
        page: params.page ?? 1,
        size: params.size ?? 20,
        order: params.order ?? 'DESC',
        status: params.status || undefined,
        dealNumber: params.dealNumber || undefined,
    })

    const data = await request(`/api/v1/admins/winner-deals${query}`, {
        auth: true,
        method: 'GET',
    })

    return unwrap(data)
}

export async function fetchAdminTransactionDetail(winnerDealId) {
    const data = await request(`/api/v1/admins/winner-deals/${encodeURIComponent(winnerDealId)}`, {
        auth: true,
        method: 'GET',
    })

    return unwrap(data)
}

export async function registerAdminTrackingNumber(winnerDealId, payload={}) {
    const data = await request(`/api/v1/admins/winner-deals/${encodeURIComponent(winnerDealId)}/tracking-number`, {
        auth: true,
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            carrier: payload.carrier ?? '',
            trackingNumber: payload.trackingNumber ?? ''
        })
    }) 

    return unwrap(data)
    
}