export const assets = {
  heroBackground: 'https://www.figma.com/api/mcp/asset/2c032067-2d24-4567-b3e8-93b8314cf536',
  watchImage: 'https://www.figma.com/api/mcp/asset/efb38393-6d05-42e3-bdbd-49d9c50c580f',
  homeIcon: 'https://www.figma.com/api/mcp/asset/cd652fb8-c0c4-4340-b0fd-ab57c46cacc2',
  categoryIcon: 'https://www.figma.com/api/mcp/asset/74cf0ac8-a459-4052-b743-87dee931bff0',
  registerIcon: 'https://www.figma.com/api/mcp/asset/cfd6b14c-3cb6-4eb7-99a8-5f03989fc719',
  inspectionIcon: 'https://www.figma.com/api/mcp/asset/d48c2f9f-af52-43c5-a115-5c3eb00869bf',
  listWatchImage: 'https://www.figma.com/api/mcp/asset/e8c9af76-2653-419a-8da5-b7a65a36822d',
}

export const heroSlides = [
  {
    key: 'all-auctions',
    title: ['대한민국 경매,', '한 곳에서 확인하세요'],
    description: ['실시간으로 진행되는 프리미엄 아이템 경매. Biddinggo', '에서 가장 빠르고 안전하게 비딩을 시작하세요.'],
    buttonLabel: '지금 둘러보기',
    backgroundImage: 'https://pub-a42a8c25bf464f499cab51ef3c3d0136.r2.dev/item-image/Luxurybackground.png',
  },
  {
    key: 'premium-auctions',
    title: ['검수 완료 경매,', '더 안심하고 입찰하세요'],
    description: ['검수 정보와 상태가 확인된 상품만 모아', '프리미엄 경매를 더 신뢰 있게 만나보세요.'],
    buttonLabel: '추천 경매 보기',
    backgroundImage: 'https://pub-a42a8c25bf464f499cab51ef3c3d0136.r2.dev/item-image/auction-image.jpeg',
  },
  {
    key: 'closing-soon',
    title: ['마감 임박 경매,', '지금 바로 확인하세요'],
    description: ['놓치기 쉬운 인기 경매를 빠르게 확인하고', '원하는 상품에 마지막 기회를 잡아보세요.'],
    buttonLabel: '전체 경매 보기',
    backgroundImage: 'https://pub-a42a8c25bf464f499cab51ef3c3d0136.r2.dev/item-image/pingu3.jpeg',
  },
]

export const navigationItems = [
  { label: '홈', key: 'home', route: '/' },
  { label: '카테고리', key: 'list', route: '/auctions' },
  { label: '경매 등록', key: 'register', route: '/register' },
  { label: '검수', key: 'inspection', route: '/inspection' },
]
