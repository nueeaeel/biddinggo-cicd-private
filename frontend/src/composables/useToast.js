import { reactive } from 'vue'

const toast = reactive({
  color: 'success',
  message: '',
  timeout: 2500,
  visible: false,
})

let lastToastKey = ''
let lastToastAt = 0

function shouldSuppressToast(message) {
  const normalized = String(message || '')
    .trim()
    .replace(/\s+/g, ' ')
    .replace(/[.。]+$/g, '')

  return normalized.includes('리프레쉬 토큰이 유효하지 않습니다')
    || normalized.includes('리프리쉬 토큰이 유효하지 않습니다')
}

export function useToast() {
  function showToast(message, options = {}) {
    const resolvedMessage = String(message || '').trim()

    if (!resolvedMessage) {
      return
    }

    if (shouldSuppressToast(resolvedMessage)) {
      return
    }

    const resolvedColor = options.color || 'success'
    const resolvedTimeout = options.timeout ?? 2500
    const now = Date.now()
    const toastKey = `${resolvedColor}:${resolvedMessage}`

    if (toast.visible && toastKey === lastToastKey && now - lastToastAt < 1200) {
      return
    }

    toast.message = resolvedMessage
    toast.color = resolvedColor
    toast.timeout = resolvedTimeout
    toast.visible = true
    lastToastKey = toastKey
    lastToastAt = now
  }

  function hideToast() {
    toast.visible = false
  }

  return {
    hideToast,
    showToast,
    toast,
  }
}
