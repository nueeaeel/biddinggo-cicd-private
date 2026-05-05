<template>
  <BaseModal
    :open="open"
    panel-class="purchase-modal address-add-modal"
    header-label="배송지 추가"
    @close="handleClose"
  >
    <form class="address-add-form" @submit.prevent="submitAddress">
      <div class="address-add-form__lookup">
        <label>
          <span>우편번호</span>
          <input v-model="addressForm.zip" type="text" readonly placeholder="주소 검색을 눌러주세요" />
        </label>
        <button class="secondary-button" type="button" @click="openPostcodePopup">주소 검색</button>
      </div>

      <label>
        <span>주소</span>
        <input v-model="addressForm.baseAddress" type="text" readonly placeholder="주소 검색 결과가 들어갑니다" />
      </label>

      <label>
        <span>상세주소</span>
        <input ref="detailAddressInput" v-model="addressForm.detailAddress" type="text" placeholder="상세주소를 입력해주세요" />
      </label>

      <label class="address-add-form__check">
        <input v-model="addressForm.primary" type="checkbox" />
        <span>기본 배송지로 설정</span>
      </label>

      <div class="purchase-modal__actions">
        <button class="secondary-button purchase-modal__action" type="button" @click="handleClose">취소</button>
        <button class="primary-button purchase-modal__action" type="submit" :disabled="saving">
          {{ saving ? '추가 중...' : '추가' }}
        </button>
      </div>
    </form>
  </BaseModal>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useToast } from '../../../composables/useToast'
import BaseModal from '../../BaseModal.vue'

const POSTCODE_SCRIPT_URL = 'https://t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'

let postcodeScriptPromise = null

const props = defineProps({
  errorMessage: {
    type: String,
    default: '',
  },
  open: {
    type: Boolean,
    default: false,
  },
  saving: {
    type: Boolean,
    default: false,
  },
  suggestPrimary: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['close', 'submit'])
const { showToast } = useToast()
const detailAddressInput = ref(null)

const addressForm = reactive({
  zip: '',
  baseAddress: '',
  detailAddress: '',
  primary: false,
})

watch(
  () => props.open,
  (isOpen) => {
    if (isOpen) {
      resetAddressForm()
    }
  },
  { immediate: true },
)

watch(
  () => props.errorMessage,
  (message) => {
    if (message) {
      showToast(message, { color: 'error' })
    }
  },
)

function resetAddressForm() {
  addressForm.zip = ''
  addressForm.baseAddress = ''
  addressForm.detailAddress = ''
  addressForm.primary = props.suggestPrimary
}

function handleClose() {
  emit('close')
}

function loadPostcodeScript() {
  if (window.kakao?.Postcode) {
    return Promise.resolve()
  }

  if (postcodeScriptPromise) {
    return postcodeScriptPromise
  }

  postcodeScriptPromise = new Promise((resolve, reject) => {
    const existingScript = document.querySelector(`script[src="${POSTCODE_SCRIPT_URL}"]`)

    if (existingScript) {
      if (window.kakao?.Postcode) {
        resolve()
        return
      }

      existingScript.addEventListener('load', () => resolve(), { once: true })
      existingScript.addEventListener('error', () => reject(new Error('postcode-load-failed')), { once: true })
      return
    }

    const script = document.createElement('script')
    script.src = POSTCODE_SCRIPT_URL
    script.async = true
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('postcode-load-failed'))
    document.head.appendChild(script)
  })

  return postcodeScriptPromise
}

function buildExtraAddress(data) {
  const extraAddressParts = []

  if (data.bname && /[동로가]$/.test(data.bname)) {
    extraAddressParts.push(data.bname)
  }

  if (data.buildingName && data.apartment === 'Y') {
    extraAddressParts.push(data.buildingName)
  }

  return extraAddressParts.length ? ` (${extraAddressParts.join(', ')})` : ''
}

async function openPostcodePopup() {
  try {
    await loadPostcodeScript()

    new window.kakao.Postcode({
      oncomplete(data) {
        const selectedAddress = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress
        const extraAddress = data.userSelectedType === 'R' ? buildExtraAddress(data) : ''

        addressForm.zip = data.zonecode
        addressForm.baseAddress = `${selectedAddress}${extraAddress}`

        nextTick(() => {
          detailAddressInput.value?.focus()
        })
      },
    }).open()
  } catch {
    showToast('주소 검색 서비스를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.', { color: 'error' })
  }
}

function submitAddress() {
  if (!addressForm.zip || !addressForm.baseAddress) {
    showToast('주소 검색으로 배송지를 먼저 선택해주세요.', { color: 'error' })
    return
  }

  emit('submit', {
    zipcode: addressForm.zip,
    address: addressForm.baseAddress,
    detailAddress: addressForm.detailAddress.trim(),
    primary: addressForm.primary,
  })
}

onMounted(() => {
  loadPostcodeScript().catch(() => {
    postcodeScriptPromise = null
  })
})
</script>
