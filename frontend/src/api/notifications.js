import { buildQueryString, request, unwrap } from "./http";

export async function fetchNotifications(params ={}) {
    const query = buildQueryString({
        page: params.page ?? 1,
        size: params.size ?? 20,
        order: params.order ?? 'DESC',
    })

    const data = await request(`/api/v1/notifications/${query}`, {
        auth: true,
        method: 'GET',
    })

    return unwrap(data)
}

export async function fetchUnreadNotificationCount() {
    const data = await request(`/api/v1/notifications/unread-count`, {
        auth: true,
        method: 'GET',
    })

    return unwrap(data)
}

export async function readNotification(notificationId) {
    const data = await request(`/api/v1/notifications/${encodeURIComponent(notificationId)}/read`, {
        auth: true,
        method: 'PATCH'
    })

    return unwrap(data)
    
}

export async function readAllNotifications() {
    const data = await request(`/api/v1/notifications/read-all`, {
        auth: true,
        method: 'PATCH',
    })

    return unwrap(data)
}