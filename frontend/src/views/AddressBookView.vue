<template>
  <AddressBookScreen
    :addresses="addressItems"
    :error-message="errorMessage"
    :loading="loading"
    :saving="saving"
    :deleting-id="deletingId"
    :setting-default-id="settingDefaultId"
    :create-address="createAddress"
    :delete-address="deleteAddress"
    :set-default-address="setDefaultAddress"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import {
  createUserAddress,
  deleteUserAddress,
  getUserAddresses,
  setDefaultUserAddress,
} from '../api/users'
import AddressBookScreen from '../components/mypage/addresses/AddressBookScreen.vue'

const addressItems = ref([])
const errorMessage = ref('')
const loading = ref(true)
const saving = ref(false)
const deletingId = ref(null)
const settingDefaultId = ref(null)

function getAddressRows(response) {
  if (Array.isArray(response)) {
    return response
  }

  if (Array.isArray(response?.content)) {
    return response.content
  }

  if (Array.isArray(response?.addresses)) {
    return response.addresses
  }

  if (Array.isArray(response?.addressList)) {
    return response.addressList
  }

  return []
}

function normalizeAddress(address = {}, index = 0) {
  const zip = address.zip
    ?? address.zipcode
    ?? address.zipCode
    ?? address.zonecode
    ?? address.postalCode
    ?? address.postCode
    ?? ''
  const baseAddress = address.address
    ?? address.address1
    ?? address.roadAddress
    ?? address.jibunAddress
    ?? address.basicAddress
    ?? ''
  const detailAddress = address.address2
    ?? address.detailAddress
    ?? address.detail
    ?? ''
  const fullAddress = [baseAddress, detailAddress].filter(Boolean).join(' ')

  return {
    id: address.id ?? address.addressId ?? address.addressNo ?? `address-${index}-${zip}`,
    zip: String(zip || ''),
    address: fullAddress || '-',
    primary: normalizeBoolean(
      address.primary
        ?? address.default_yn
        ?? address.defaultYn
        ?? address.defaultYN
        ?? address.defaultAddress
        ?? address.isDefault
        ?? address.isPrimary,
    ),
    createdAt: address.createdAt ?? address.createdDate ?? address.registeredAt ?? address.updatedAt ?? '',
    order: index,
  }
}

function normalizeBoolean(value) {
  if (typeof value === 'boolean') {
    return value
  }

  if (typeof value === 'string') {
    return ['true', 'y', 'yes', '1'].includes(value.trim().toLowerCase())
  }

  return Number(value) === 1
}

async function loadUserAddresses() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getUserAddresses()
    addressItems.value = getAddressRows(response).map(normalizeAddress)
  } catch (error) {
    addressItems.value = []
    errorMessage.value = error?.message || '배송지 목록을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

async function createAddress(payload) {
  saving.value = true
  errorMessage.value = ''
  const hadNoAddress = addressItems.value.length === 0
  const previousAddressIds = new Set(addressItems.value.map((address) => address.id))

  try {
    const createdAddress = await createUserAddress({
      zipcode: payload.zipcode,
      address: payload.address,
      detailAddress: payload.detailAddress,
    })
    await loadUserAddresses()

    if (hadNoAddress && addressItems.value.length && !addressItems.value.some((address) => address.primary)) {
      const createdAddressId = createdAddress?.id ?? createdAddress?.addressId ?? createdAddress?.addressNo
      const nextDefaultAddress = addressItems.value.find((address) => address.id === createdAddressId)
        || addressItems.value.find((address) => !previousAddressIds.has(address.id))
        || getNewestAddress(addressItems.value)

      if (nextDefaultAddress?.id) {
        await setDefaultUserAddress(nextDefaultAddress.id)
        await loadUserAddresses()
      }
    }
  } catch (error) {
    errorMessage.value = error?.message || '배송지를 등록하지 못했습니다.'
    throw error
  } finally {
    saving.value = false
  }
}

function getNewestAddress(addresses) {
  return [...addresses].sort((first, second) => {
    const firstTime = new Date(first.createdAt || 0).getTime()
    const secondTime = new Date(second.createdAt || 0).getTime()
    const firstId = Number(first.id)
    const secondId = Number(second.id)

    if (!Number.isNaN(firstTime) && !Number.isNaN(secondTime) && firstTime !== secondTime) {
      return secondTime - firstTime
    }

    if (!Number.isNaN(firstTime) && Number.isNaN(secondTime)) {
      return -1
    }

    if (Number.isNaN(firstTime) && !Number.isNaN(secondTime)) {
      return 1
    }

    if (!Number.isNaN(firstId) && !Number.isNaN(secondId) && firstId !== secondId) {
      return secondId - firstId
    }

    return first.order - second.order
  })[0]
}

async function setDefaultAddress(addressId) {
  settingDefaultId.value = addressId
  errorMessage.value = ''

  try {
    await setDefaultUserAddress(addressId)
    await loadUserAddresses()
  } catch (error) {
    errorMessage.value = error?.message || '기본 배송지를 설정하지 못했습니다.'
    throw error
  } finally {
    settingDefaultId.value = null
  }
}

async function deleteAddress(addressId) {
  deletingId.value = addressId
  errorMessage.value = ''

  try {
    await deleteUserAddress(addressId)

    const remainingAddresses = addressItems.value.filter((address) => address.id !== addressId)
    const hasPrimaryAddress = remainingAddresses.some((address) => address.primary)

    if (remainingAddresses.length && !hasPrimaryAddress) {
      const newestAddress = getNewestAddress(remainingAddresses)

      if (newestAddress?.id) {
        await setDefaultUserAddress(newestAddress.id)
      }
    }

    await loadUserAddresses()
  } catch (error) {
    errorMessage.value = error?.message || '배송지를 삭제하지 못했습니다.'
    throw error
  } finally {
    deletingId.value = null
  }
}

onMounted(() => {
  loadUserAddresses()
})
</script>
