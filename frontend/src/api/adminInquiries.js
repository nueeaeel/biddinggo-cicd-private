import { buildQueryString, request, unwrap } from "./http";

export async function fetchAdminInquiries(params = {}) {
    const query = buildQueryString({
        page: params.page ?? 1,
        size: params.size ?? 50,
        order: params.order ?? 'DESC',

    })

    return unwrap(
        await request(`/api/v1/admins/direct-inquiries${query}`,{
            auth: true,
            method: 'GET'
        }),
    )

}

export async function answerAdminInquiry(inquiryId, answer) {
    return unwrap(
        await request(`/api/v1/admins/direct-inquiries/${encodeURIComponent(inquiryId)}`, {
            auth: true,
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ answer }),
        }),
    )
}