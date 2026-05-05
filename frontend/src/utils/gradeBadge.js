const gradeBadges = {
  bronze: new URL('../assets/grades/bronze.png', import.meta.url).href,
  gold: new URL('../assets/grades/gold.png', import.meta.url).href,
  silver: new URL('../assets/grades/silver.png', import.meta.url).href,
}

export function getGradeBadge(grade) {
  const key = String(grade || '').trim().toLowerCase()

  return gradeBadges[key] || ''
}
