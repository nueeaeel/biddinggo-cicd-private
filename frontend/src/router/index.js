import { createRouter, createWebHistory } from 'vue-router'
import AddressBookView from '../views/AddressBookView.vue'
import AuctionDetailView from '../views/AuctionDetailView.vue'
import AuctionEditView from '../views/AuctionEditView.vue'
import AuctionInquiryView from '../views/AuctionInquiryView.vue'
import AuctionListView from '../views/AuctionListView.vue'
import AuctionSearchView from '../views/AuctionSearchView.vue'
import AuctionManagementView from '../views/AuctionManagementView.vue'
import AdminInquiriesView from '../views/AdminInquiriesView.vue'
import AdminInspectionsView from '../views/AdminInspectionsView.vue'
import AdminNoticesView from '../views/AdminNoticesView.vue'
import AdminTransactionsView from '../views/AdminTransactionsView.vue'
import AdminUsersView from '../views/AdminUsersView.vue'
import BidHistoryView from '../views/BidHistoryView.vue'
import DashboardView from '../views/DashboardView.vue'
import DirectInquiryView from '../views/DirectInquiryView.vue'
import HomeView from '../views/HomeView.vue'
import InspectionView from '../views/InspectionView.vue'
import LoginView from '../views/LoginView.vue'
import MyPageLayout from '../components/layout/MyPageLayout.vue'
import PointsView from '../views/PointsView.vue'
import ProfileSetupView from '../views/ProfileSetupView.vue'
import ProfileView from '../views/ProfileView.vue'
import PurchaseHistoryView from '../views/PurchaseHistoryView.vue'
import RegisterView from '../views/RegisterView.vue'
import SalesHistoryView from '../views/SalesHistoryView.vue'
import WishlistView from '../views/WishlistView.vue'
import AuthCallbackView from '../views/AuthCallbackView.vue'
import { authState } from '../lib/authSession'
import AdminLoginView from '../views/AdminLoginView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      navSection: 'main',
      navKey: 'home',
    },
  },
  {
    path: '/auctions',
    name: 'auction-list',
    component: AuctionListView,
    meta: {
      navSection: 'main',
      navKey: 'list',
    },
  },
  {
    path: '/auctions/search',
    name: 'auction-search',
    component: AuctionSearchView,
    meta: {
      navSection: 'main',
      navKey: 'list',
    },
  },
  {
    path: '/auctions/:id',
    name: 'auction-detail',
    component: AuctionDetailView,
    meta: {
      navSection: 'main',
      navKey: 'list',
    },
  },
  {
    path: '/auctions/:id/edit',
    name: 'auction-edit',
    component: AuctionEditView,
    meta: {
      navSection: 'main',
      navKey: 'list',
    },
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: {
      navSection: 'main',
      navKey: 'register',
    },
  },
  {
    path: '/inspection',
    name: 'inspection',
    component: InspectionView,
    meta: {
      navSection: 'main',
      navKey: 'inspection',
    },
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/auth/callback',
    name: 'auth-callback',
    component: AuthCallbackView,
  },
  {
    path: '/profile/setup',
    name: 'profile-setup',
    component: ProfileSetupView,
  },
  {
    path: '/mypage',
    component: MyPageLayout,
    meta: {
      navSection: 'mypage',
      requiresAuth: true,
    },
    children: [
      {
        path: '',
        name: 'mypage-dashboard',
        component: DashboardView,
        meta: {
          navKey: 'mypage-dashboard',
        },
      },
      {
        path: 'notifications',
        redirect: '/mypage',
      },
      {
        path: 'bids',
        name: 'bids',
        component: BidHistoryView,
        meta: {
          navKey: 'bids',
        },
      },
      {
        path: 'wishlists',
        name: 'favorites',
        component: WishlistView,
        meta: {
          navKey: 'favorites',
        },
      },
      {
        path: 'purchases',
        name: 'purchases',
        component: PurchaseHistoryView,
        meta: {
          navKey: 'purchases',
        },
      },
      {
        path: 'sales',
        name: 'sales',
        component: SalesHistoryView,
        meta: {
          navKey: 'sales',
        },
      },
      {
        path: 'auctions',
        name: 'auction-management',
        component: AuctionManagementView,
        meta: {
          navKey: 'auction-management',
        },
      },
      {
        path: 'profile',
        name: 'profile',
        component: ProfileView,
        meta: {
          navKey: 'profile',
        },
      },
      {
        path: 'addresses',
        name: 'addresses',
        component: AddressBookView,
        meta: {
          navKey: 'addresses',
        },
      },
      {
        path: 'points',
        name: 'points',
        component: PointsView,
        meta: {
          navKey: 'points',
        },
      },
      {
        path: 'trade-inquiries',
        redirect: '/mypage/auction-inquiries',
      },
      {
        path: 'auction-inquiries',
        name: 'auction-inquiries',
        component: AuctionInquiryView,
        meta: {
          navKey: 'auction-inquiries',
        },
      },
      {
        path: 'direct-inquiries',
        name: 'direct-inquiries',
        component: DirectInquiryView,
        meta: {
          navKey: 'direct-inquiries',
        },
      },
    ],
  },
  {
    path: '/admin',
    redirect: '/admin/transactions',
  },
  {
    path: '/admin/transactions',
    name: 'admin-transactions',
    component: AdminTransactionsView,
    meta: {
      navSection: 'admin',
      navKey: 'transactions',
    },
  },
  {
    path: '/admin/inquiries',
    name: 'admin-inquiries',
    component: AdminInquiriesView,
    meta: {
      navSection: 'admin',
      navKey: 'inquiries',
    },
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: AdminUsersView,
    meta: {
      navSection: 'admin',
      navKey: 'users',
    },
  },
  {
    path: '/admin/inspections',
    name: 'admin-inspections',
    component: AdminInspectionsView,
    meta: {
      navSection: 'admin',
      navKey: 'inspections',
    },
  },
  {
    path: '/admin/notices',
    name: 'admin-notices',
    component: AdminNoticesView,
    meta: {
      navSection: 'admin',
      navKey: 'notices',
    },
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
  {
    path: '/admin/login',
    name: 'admin-login',
    component: AdminLoginView,
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

function hasAdminAuthority(authorities = authState.authorities) {
  if (!Array.isArray(authorities)) {
    return false
  }

  return authorities.some((authority) => {
    const normalized = String(authority || '').toUpperCase()
    return normalized === 'ROLE_ADMIN' || normalized === 'ADMIN'
  })
}

router.beforeEach((to) => {
  const routeName = String(to.name || '')
  const publicAuthRoutes = new Set(['login', 'admin-login', 'auth-callback', 'profile-setup'])
  const isAdminRoute = to.path.startsWith('/admin') && routeName !== 'admin-login'

  if (routeName === 'admin-login') {
    if (authState.isAuthenticated && hasAdminAuthority()) {
      return { name: 'admin-transactions' }
    }

    if (authState.isAuthenticated && !hasAdminAuthority()) {
      return { name: 'home' }
    }
  }

  if (isAdminRoute) {
    if (!authState.isAuthenticated) {
      return {
        name: 'admin-login',
        query: { redirect: to.fullPath },
      }
    }

    if (!hasAdminAuthority()) {
      return { name: 'home' }
    }
  }

  if (to.matched.some((route) => route.meta.requiresAuth) && !authState.isAuthenticated) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath,
      },
    }
  }

  if (
    authState.isAuthenticated
    && authState.status === 'PENDING'
    && !publicAuthRoutes.has(routeName)
    && !hasAdminAuthority()
    && !publicAuthRoutes.has(routeName)
  ) {
    return {
      name: 'profile-setup',
      query: { redirect: to.fullPath },
    }
  }

  if (authState.isAuthenticated && authState.status === 'ACTIVE' && routeName === 'profile-setup') {
    return { name: 'home' }
  }

  return true
})

export default router
