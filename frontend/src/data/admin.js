export const adminNavItems = [
  { key: 'transactions', label: '거래 내역 조회', route: '/admin/transactions' },
  { key: 'users', label: '사용자 관리', route: '/admin/users' },
  { key: 'inspections', label: '검수 관리', route: '/admin/inspections' },
  { key: 'notices', label: '공지사항 관리', route: '/admin/notices' },
  { key: 'inquiries', label: '1:1 문의 관리', route: '/admin/inquiries' },
]

export const adminTransactionStatusFilters = ['전체', '거래 완료', '배송 대기', '배송 중', '배송 완료']
export const shippingCompanies = ['CJ대한통운', '우체국택배', '롯데택배', '한진택배']

export const adminTransactionRows = []

export const adminInquiryFilters = ['전체', '답변 대기', '답변 완료']
export const adminInquiries = []

export const adminUserStatusFilters = ['전체', '활성', '정지']
export const adminUsers = []

export const adminInspectionFilters = ['전체', '검수 대기', '승인', '반려']
export const adminInspections = []

export const adminNoticeStatusFilters = ['전체', '게시중', '비공개']
export const adminNotices = []

export const adminSettlementFilters = ['전체', '정산 대기', '정산 완료']
export const adminSettlements = []
