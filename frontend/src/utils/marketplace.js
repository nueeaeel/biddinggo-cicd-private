export function formatNumber(value) {
  return new Intl.NumberFormat('ko-KR', {
    maximumFractionDigits: 0,
  }).format(Number(value || 0))
}

export function formatPrice(value, { suffix = '' } = {}) {
  const formatted = formatNumber(value)

  return suffix ? `${formatted}${suffix}` : formatted
}

export function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

export function getCountdownLabel(value) {
  if (!value) {
    return '-'
  }

  const endTime = new Date(value).getTime()
  const diff = endTime - Date.now()

  if (!Number.isFinite(diff) || diff <= 0) {
    return '마감'
  }

  const totalMinutes = Math.floor(diff / (1000 * 60))
  const totalHours = Math.floor(totalMinutes / 60)
  const totalDays = Math.floor(totalHours / 24)

  if (totalDays > 0) {
    return `${totalDays}일`
  }

  if (totalHours > 0) {
    return `${totalHours}시간`
  }

  return `${Math.max(totalMinutes, 1)}분`
}

export function normalizeAuctionCard(result = {}) {
  const auctionType = normalizeEnumValue(result.type ?? result.auctionType)
  const inspectionYn = normalizeEnumValue(result.inspectionYn ?? result.inspection_yn)

  return {
    id: result.auctionId ? `auction-${result.auctionId}` : `auction-${result.item?.itemId || 'unknown'}`,
    auctionId: result.auctionId,
    auctionType,
    title: result.item?.name || result.name || '등록 상품',
    brand: result.item?.brand || result.brand || '브랜드 미정',
    price: formatPrice(result.startPrice),
    bids: `입찰 ${result.bidCount ?? 0}회`,
    wishCount: Number(result.wishCount || 0),
    isWished: Boolean(result.isWished ?? result.wished ?? false),
    time: getCountdownLabel(result.endDate),
    highlight: result.status === 'ON_GOING',
    isTimeDeal: auctionType === 'TIME_DEAL',
    isInspected: inspectionYn === 'YES',
    image: result.item?.images?.[0]?.url || result.representativeImageUrl || '',
  }
}

export function normalizeBidHistory(rows = []) {
  if (!rows.length) {
    return []
  }

  return rows.map((row) => ({
    id: row.id,
    bidder: `${row.bidderId ?? '-'}번 회원`,
    amount: row.amount === null || row.amount === undefined || row.amount === '' ? '-' : String(row.amount),
    date: formatDateTime(row.createdAt),
  }))
}

export function normalizeInquiries(rows = []) {
  if (!rows.length) {
    return []
  }

  return rows.map((row) => ({
    id: row.id,
    status: row.answer ? '답변 완료' : '답변 대기',
    title: row.title || '문의',
    meta: `${row.writerName || '익명'} | ${formatDateTime(row.createdAt)}`,
    question: row.content || '',
    answeredAt: formatDateTime(row.answeredAt),
    answer: row.answer || '아직 답변이 등록되지 않았습니다.',
  }))
}

export function normalizeSellerReviews(rows = []) {
  return rows.map((row) => ({
    author: row.buyerNickname || '구매자',
    date: formatDateTime(row.reviewDate),
    rating: Math.round(Number(row.rating || 0)),
    content: row.reviewText || '리뷰 내용이 없습니다.',
  }))
}

const PRICE_PREDICTION_REASON_LABELS = {
  PREDICTION_AVAILABLE: '예상 가격을 조회했습니다.',
  NOT_ENOUGH_REFERENCES: '유사 낙찰 데이터가 부족합니다.',
  EMBEDDING_NOT_FOUND: '상품 분석 데이터가 아직 준비되지 않았습니다.',
  CATEGORY_NOT_FOUND: '카테고리 정보를 찾을 수 없습니다.',
  INVALID_CONDITION: '상품 상태 정보가 올바르지 않습니다.',
  SUPABASE_TIMEOUT: '예상가 조회 시간이 초과되었습니다.',
  SUPABASE_UNAVAILABLE: '예상가 조회 서비스를 사용할 수 없습니다.',
  OUTLIER_FILTERED_ALL: '참고 가격 데이터가 모두 이상치로 제외되었습니다.',
  FALLBACK_APPLIED: '대체 예측값을 적용했습니다.',
}

function normalizeEnumValue(value) {
  return String(value || '').trim().toUpperCase()
}

function normalizePricePrediction(pricePrediction = null) {
  if (!pricePrediction) {
    return ''
  }

  const predictedPrice = pricePrediction.predictedPrice ?? pricePrediction.predicted_price

  if (predictedPrice !== null && predictedPrice !== undefined) {
    return `예상 가격: ${formatPrice(predictedPrice)}원`
  }

  const reasonCode = normalizeEnumValue(pricePrediction.reasonCode ?? pricePrediction.reason_code)
  const reasonLabel = PRICE_PREDICTION_REASON_LABELS[reasonCode] || '예상 가격을 조회하지 못했습니다.'

  return `예상 가격 조회 실패: ${reasonLabel}`
}

export function normalizeAuctionDetail(
  detail = {},
  {
    bidHistory = [],
    categoryPathLabel = '',
    inquiries = [],
    isAuthenticated = false,
    sellerProfileData = null,
    sellerReviews = [],
    wishlistStatus = {},
  } = {},
) {
  const status = normalizeEnumValue(detail.status)
  const rawStartPrice = Number(detail.startPrice ?? 0) || 0
  const rawVickreyPrice = Number(detail.vickreyPrice ?? detail.vickrey_price ?? 0) || 0
  const rawBidUnit = Number(detail.bidUnit ?? 0) || 0
  const rawBidCount = Number(detail.bidCount ?? 0) || 0
  const currentPrice = rawVickreyPrice || rawStartPrice
  const sellerName = detail.sellerNickname || (detail.sellerId ? `판매자 ${detail.sellerId}` : '판매자')
  const sellerRating = Number(sellerProfileData?.rating ?? detail.sellerRating ?? 0)
  const sellerReviewCount = Number(sellerProfileData?.reviewCount ?? detail.sellerReviewCount ?? 0)
  const auctionType = normalizeEnumValue(detail.type ?? detail.auctionType)
  const inspectionYn = normalizeEnumValue(detail.inspectionYn ?? detail.inspection_yn)
  const isInspected = inspectionYn === 'YES'
  const isExtendedAuction = [
    detail.extensionYn,
    detail.extension_yn,
    detail.extendAuction,
    detail.extend_auction,
    detail.extendedAuction,
    detail.extended_auction,
    detail.extendedYn,
    detail.extended_yn,
  ].some((value) => ['N', 'NO', 'FALSE', '0'].includes(normalizeEnumValue(value)))
  const category = detail.item?.category || {}
  const history = normalizeBidHistory(bidHistory)
  const sellerTotalSalesCount = sellerProfileData?.totalSales
    ?? sellerProfileData?.totalSalesCount
    ?? detail.sellerTotalSalesCount
    ?? detail.totalSalesCount
    ?? detail.sellerSaleCount
    ?? detail.saleCount
    ?? null
  const sellerCancelCount = sellerProfileData?.cancelCount
    ?? detail.sellerCancelCount
    ?? detail.cancelSalesCount
    ?? detail.cancelCount
    ?? detail.sellerCancelledCount
    ?? null
  const sellerResponseRate = sellerProfileData?.responseRate
    ?? detail.sellerResponseRate
    ?? detail.responseRate
    ?? detail.sellerReplyRate
    ?? null

  return {
    id: detail.auctionId ? `auction-${detail.auctionId}` : 'auction-detail',
    auctionId: detail.auctionId,
    auctionType,
    status,
    categoryId: category.id,
    categoryPathLabel: categoryPathLabel || category.name || '',
    itemId: detail.item?.itemId,
    sellerId: detail.sellerMemberId ?? detail.memberId ?? detail.userId ?? detail.ownerId ?? detail.sellerId,
    sellerAvatar: sellerProfileData?.imageUrl || detail.sellerImageUrl || '',
    sellerJoinedAt: formatShortDate(sellerProfileData?.createdAt || detail.sellerCreatedAt),
    sellerRating: sellerRating ? sellerRating.toFixed(1) : '0.0',
    sellerReviewCount,
    sellerTotalSalesCount,
    sellerCancelCount,
    sellerResponseRate,
    sellerReviews: normalizeSellerReviews(sellerReviews),
    title: detail.item?.name || '상품명 없음',
    brand: detail.item?.brand || '브랜드 미정',
    price: formatPrice(currentPrice),
    startPrice: formatPrice(detail.startPrice),
    rawStartPrice,
    rawVickreyPrice,
    rawBidUnit,
    rawBidCount,
    pricePredictionLabel: normalizePricePrediction(detail.pricePrediction ?? detail.price_prediction),
    bids: `입찰 ${rawBidCount}회`,
    wishCount: Number(detail.wishCount || 0),
    isWished: Boolean(wishlistStatus?.wished),
    time: getCountdownLabel(detail.endDate),
    highlight: status === 'ON_GOING',
    isTimeDeal: auctionType === 'TIME_DEAL',
    isExtendedAuction,
    isInspected,
    seller: sellerProfileData?.nickname || sellerName,
    sellerGrade: sellerProfileData?.grade || detail.sellerGrade || (isInspected ? 'CERTIFIED' : 'STANDARD'),
    inspectionGrade:
      detail.quality
      || detail.item?.quality
      || detail.inspectionQuality
      || detail.inspection_grade
      || '-',
    description: detail.item?.description || '상품 설명이 없습니다.',
    inspectionLabel: isInspected ? '검수 완료 상품' : '일반 등록 상품',
    inspectionDescription:
      isInspected
        ? '전문가 검수를 통과한 상품입니다.'
        : '판매자가 직접 등록한 상품입니다.',
    buyNowPrice: `${formatPrice(detail.buyNowPrice)}원`,
    bidUnit: `${formatPrice(detail.bidUnit)}원`,
    startDate: formatDateTime(detail.startDate),
    endDate: formatDateTime(detail.endDate),
    bidHistoryRequiresLogin: !isAuthenticated,
    history,
    historyPreview: history.slice(0, 3),
    inquiries: normalizeInquiries(inquiries),
    images: detail.item?.images || [],
    image: detail.item?.images?.[0]?.url || detail.representativeImageUrl || '',
  }
}

export function formatShortDate(value) {
  if (!value) {
    return '-'
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
    .format(new Date(value))
    .replace(/\. /g, ' / ')
    .replace('.', '')
}

export function normalizeInspectionPickItem(result = {}) {
  const inspectionStatus = normalizeEnumValue(result.inspectionStatus ?? result.status)
  const auctionItemStatus = normalizeEnumValue(result.auctionItemStatus ?? result.itemStatus)
  const images = Array.isArray(result.images)
    ? result.images
    : Array.isArray(result.item?.images)
      ? result.item.images
      : []
  const representativeImage = result.representativeImageUrl || images[0]?.url || ''

  return {
    inspectionId: result.inspectionId,
    itemId: result.itemId,
    inspectionStatus,
    auctionItemStatus,
    title: result.name || '검수 상품',
    brand: result.brand || '브랜드 미정',
    inspectionGrade: result.quality || '-',
    inspectionDate: formatShortDate(result.createdAt),
    description: `${result.brand || '브랜드 미정'} ${result.name || ''}`.trim(),
    image: representativeImage,
    images: images.length ? images : (representativeImage ? [{ url: representativeImage }] : []),
    carrier: result.carrier || '',
    trackingNumber: result.trackingNumber || '',
    categoryLabel: '',
  }
}

export function mergeInspectionItemDetail(item = {}, detail = {}) {
  const detailItem = detail.item || {}
  const detailCategory = detailItem.category || {}
  const detailImage = detailItem.images?.[0]?.url || ''
  const detailImages = Array.isArray(detailItem.images) ? detailItem.images : []
  const baseImages = Array.isArray(item.images) ? item.images : []

  return {
    ...item,
    brand: detailItem.brand || item.brand,
    title: detailItem.name || item.title,
    description: detailItem.description || item.description,
    categoryLabel: detailCategory.name || item.categoryLabel,
    image: detailImage || item.image,
    images: detailImages.length ? detailImages : baseImages,
    carrier: detail.carrier || item.carrier,
    trackingNumber: detail.trackingNumber || item.trackingNumber,
  }
}

export function normalizeInspectionListItem(result = {}) {
  const status = normalizeEnumValue(result.inspectionStatus ?? result.status)
  const auctionItemStatus = normalizeEnumValue(result.auctionItemStatus ?? result.itemStatus)
  const item = result.item || {}
  const category = item.category || result.category || {}
  const categoryLabel = category.name ? `${category.name}` : ''
  const images = Array.isArray(item.images) ? item.images : []
  const representativeImage = result.representativeImageUrl || images[0]?.url || ''

  return {
    inspectionId: result.inspectionId,
    itemId: result.itemId ?? item.itemId,
    title: result.name || item.name || '검수 상품',
    brand: result.brand || item.brand || '브랜드 미정',
    status,
    auctionItemStatus,
    statusLabel: INSPECTION_STATUS_LABELS[status] || '검수 상태 없음',
    inspectionGrade: result.quality || item.quality || '-',
    inspectionDate: formatShortDate(result.createdAt),
    description: item.description || `${result.brand || item.brand || '브랜드 미정'} ${result.name || item.name || ''}`.trim(),
    image: representativeImage,
    images: images.length ? images : (representativeImage ? [{ url: representativeImage }] : []),
    carrier: result.carrier || '',
    trackingNumber: result.trackingNumber || '',
    categoryLabel,
  }
}

export function buildInspectionSummary(items = []) {
  const counts = items.reduce(
    (acc, item) => {
      acc.total += 1
      acc[item.status] = (acc[item.status] || 0) + 1
      return acc
    },
    { total: 0 },
  )

  return [
    { label: '총 검수 신청', value: formatNumber(counts.total), tone: 'total' },
    { label: '검수 대기', value: formatNumber(counts.PENDING || 0), tone: 'review' },
    { label: '검수 통과', value: formatNumber(counts.PASSED || 0), tone: 'approve' },
    { label: '검수 반려', value: formatNumber(counts.FAILED || 0), tone: 'auction' },
  ]
}

export const INSPECTION_STATUS_LABELS = {
  PENDING: '검수 대기',
  PASSED: '검수 통과',
  FAILED: '검수 반려',
}
