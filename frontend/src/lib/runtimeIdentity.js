function readNumber(value, fallback) {
  const parsed = Number.parseInt(String(value ?? ''), 10)

  return Number.isFinite(parsed) ? parsed : fallback
}

export const runtimeIdentity = Object.freeze({
  memberId: readNumber(import.meta.env.VITE_DEFAULT_MEMBER_ID, 1),
})
