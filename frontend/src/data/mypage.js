import defaultAvatar from '../assets/default-avatar.svg'
import noImage from '../assets/no-image.svg'

export const navItems = [
  { label: '대시보드', route: '/mypage' },
  { label: '입찰 내역', route: '/mypage/bids' },
  { label: '관심 경매', route: '/mypage/wishlists' },
  { label: '구매 내역', route: '/mypage/purchases' },
  { label: '판매 내역', route: '/mypage/sales' },
  { label: '경매 관리', route: '/mypage/auctions' },
  { label: '프로필 관리', route: '/mypage/profile' },
  { label: '주소록 관리', route: '/mypage/addresses' },
  { label: '포인트 관리', route: '/mypage/points' },
  { label: '구매/판매 문의 내역', route: '/mypage/auction-inquiries' },
  { label: '1:1 문의 내역', route: '/mypage/direct-inquiries' },
]

export const watchImage = noImage

export const overviewUser = {
  name: '회원',
  nickname: '',
  email: '',
  bankCode: '',
  bankAccount: '',
  points: '0원',
  rating: '0.0',
  reviews: '리뷰 0',
  joinedAt: '-',
  grade: 'BRONZE',
  avatar: defaultAvatar,
  imageUrl: defaultAvatar,
}

export const bidItems = []

export const purchaseStatusItems = []

export const pointHistory = []

export const notifications = []
