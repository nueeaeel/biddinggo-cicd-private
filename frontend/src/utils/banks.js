export const bankOptions = [
  { label: '선택 안 함', code: '' },
  { label: '경남은행', code: '039' },
  { label: '광주은행', code: '034' },
  { label: '단위농협', code: '012' },
  { label: '부산은행', code: '032' },
  { label: '새마을금고', code: '045' },
  { label: '산림조합', code: '064' },
  { label: '신한은행', code: '088' },
  { label: '신협', code: '048' },
  { label: '씨티은행', code: '027' },
  { label: '우리은행', code: '020' },
  { label: '우체국예금보험', code: '071' },
  { label: '저축은행중앙회', code: '050' },
  { label: '전북은행', code: '037' },
  { label: '제주은행', code: '035' },
  { label: '카카오뱅크', code: '090' },
  { label: '케이뱅크', code: '089' },
  { label: '토스뱅크', code: '092' },
  { label: '하나은행', code: '081' },
  { label: '홍콩상하이은행', code: '054' },
  { label: 'Bank of America', code: '060' },
  { label: 'IBK기업은행', code: '003' },
  { label: 'KB국민은행', code: '004' },
  { label: 'iM뱅크(대구)', code: '031' },
  { label: 'KDB산업은행', code: '002' },
  { label: 'NH농협은행', code: '011' },
  { label: 'SC제일은행', code: '023' },
  { label: 'Sh수협은행', code: '007' },
  { label: '수협중앙회', code: '030' },
]

export function normalizeBankCode(bankCode) {
  if (!bankCode) {
    return ''
  }

  const value = String(bankCode)

  if (/^\d+$/.test(value)) {
    return value.padStart(3, '0')
  }

  return value
}

export function getBankLabel(bankCode) {
  const normalizedCode = normalizeBankCode(bankCode)
  const bank = bankOptions.find((option) => option.code === normalizedCode || option.label === bankCode)

  return bank?.label || String(bankCode || '-')
}
