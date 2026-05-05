const FALLBACK_CATEGORIES = [
  { id: 10, parentId: null, name: '시계', level: 1, hasChildren: true, selectable: false },
  { id: 11, parentId: 10, name: '명품 시계', level: 2, hasChildren: true, selectable: false },
  { id: 12, parentId: 11, name: '남성용 시계', level: 3, hasChildren: false, selectable: true },
  { id: 20, parentId: null, name: '가방', level: 1, hasChildren: true, selectable: false },
  { id: 21, parentId: 20, name: '럭셔리 백', level: 2, hasChildren: true, selectable: false },
  { id: 22, parentId: 21, name: '토트백', level: 3, hasChildren: false, selectable: true },
]

export function getFallbackCategories() {
  return FALLBACK_CATEGORIES
}

export function buildCategoryGroups(categories = [], selectedCategoryId = null) {
  const rows = normalizeCategoryRows(categories.length ? categories : FALLBACK_CATEGORIES)
  const selectedId = Number(selectedCategoryId)

  return rows.map((category) => ({
    id: Number(category.id),
    label: category.name,
    indent: Math.max(Number(category.level || 1) - 1, 0),
    active: Number(category.id) === selectedId,
    selectable: category.selectable === true || category.hasChildren === false || Number(category.level) === 3,
  }))
}

export function buildCategoryTreeItems(categories = [], selectedCategoryId = null, expandedCategoryIds = new Set()) {
  const rows = normalizeCategoryRows(categories.length ? categories : FALLBACK_CATEGORIES)
  const selectedId = Number(selectedCategoryId)
  const childrenByParentId = new Map()

  rows.forEach((category) => {
    const key = category.parentId === null || category.parentId === undefined ? 'root' : String(category.parentId)
    const values = childrenByParentId.get(key) || []
    values.push(category)
    childrenByParentId.set(key, values)
  })

  const treeItems = []

  function appendChildren(parentId, depth) {
    const key = parentId === null || parentId === undefined ? 'root' : String(parentId)
    const children = childrenByParentId.get(key) || []

    children.forEach((category) => {
      const childCount = (childrenByParentId.get(String(category.id)) || []).length
      const hasChildren = childCount > 0 || category.hasChildren === true
      const expanded = expandedCategoryIds.has(Number(category.id))
      const selectable = category.selectable === true || (!hasChildren && Number(category.level) >= 3)

      treeItems.push({
        id: Number(category.id),
        label: category.name,
        depth,
        active: Number(category.id) === selectedId,
        selectable,
        hasChildren,
        expanded,
      })

      if (hasChildren && expanded) {
        appendChildren(category.id, depth + 1)
      }
    })
  }

  appendChildren(null, 0)

  return treeItems
}

export function buildLeafCategoryOptions(categories = []) {
  const rows = normalizeCategoryRows(categories.length ? categories : FALLBACK_CATEGORIES)
  const byId = new Map(rows.map((category) => [Number(category.id), category]))

  return rows
    .filter((category) => category.selectable === true || category.hasChildren === false || Number(category.level) === 3)
    .map((category) => ({
      id: Number(category.id),
      label: buildCategoryPathLabel(category, byId),
    }))
    .sort((left, right) => left.label.localeCompare(right.label, 'ko'))
}

export function normalizeCategoryRows(categories = []) {
  const rows = categories.length ? categories : FALLBACK_CATEGORIES

  return rows
    .map((category) => {
      const hasChildren = Boolean(category.hasChildren ?? category.has_children)

      return {
        id: Number(category.id),
        parentId: category.parentId ?? category.parent_id ?? null,
        name: String(category.name || category.label || ''),
        level: Number(category.level || 1),
        hasChildren,
        selectable: Boolean(category.selectable ?? !hasChildren),
      }
    })
    .filter((category) => Number.isFinite(category.id) && category.name)
    .map((category) => ({
      ...category,
      parentId: category.parentId === null || category.parentId === undefined ? null : Number(category.parentId),
    }))
}

export function buildCategoryPathLabel(category, byId) {
  const names = []
  let current = category

  while (current) {
    names.unshift(current.name)

    if (!current.parentId) {
      break
    }

    current = byId.get(Number(current.parentId)) || null
  }

  return names.join(' > ')
}

export function buildCategoryPathLabelById(categories = [], categoryId = null) {
  const rows = normalizeCategoryRows(categories.length ? categories : FALLBACK_CATEGORIES)
  const byId = new Map(rows.map((category) => [Number(category.id), category]))
  const category = byId.get(Number(categoryId))

  return category ? buildCategoryPathLabel(category, byId) : ''
}
