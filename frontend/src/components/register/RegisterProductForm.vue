<script setup>
import { computed, ref } from 'vue'
import { buildCategoryPathLabel } from '../../utils/category'

const MAX_IMAGE_COUNT = 10

const props = defineProps({
  categoryOptions: {
    type: Array,
    required: true,
  },
  currentMode: {
    type: String,
    required: true,
  },
  errorMessage: {
    type: String,
    default: '',
  },
  form: {
    type: Object,
    required: true,
  },
  processing: {
    type: Boolean,
    default: false,
  },
  submitted: {
    type: Boolean,
    required: true,
  },
  successMessage: {
    type: String,
    required: true,
  },
  thumbnailPlaceholders: {
    type: Number,
    required: true,
  },
  uploadInProgress: {
    type: Boolean,
    default: false,
  },
  uploadedImages: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['cancel', 'files-selected', 'remove-image', 'set-primary-image', 'submit'])

const fileInput = ref(null)
const thumbnailScroller = ref(null)
const isCategoryModalOpen = ref(false)
const selectedMainCategoryId = ref(null)
const selectedMiddleCategoryId = ref(null)
const selectedSubCategoryId = ref(null)
const remainingImageCount = computed(() => Math.max(0, MAX_IMAGE_COUNT - props.uploadedImages.length))
const showThumbnailNav = computed(() => props.uploadedImages.length + (remainingImageCount.value > 0 ? 1 : 0) > 3)
const imageHelperText = computed(() => {
  if (props.uploadInProgress) {
    return '이미지를 업로드하고 있습니다.'
  }

  return `최대 ${MAX_IMAGE_COUNT}장, 각 20MB까지 업로드 가능 · 현재 ${props.uploadedImages.length}장`
})
const categoryRows = computed(() => props.categoryOptions || [])
const categoryById = computed(() => new Map(categoryRows.value.map((category) => [Number(category.id), category])))
const categoriesByParentId = computed(() => {
  const groups = new Map()

  categoryRows.value.forEach((category) => {
    const key = category.parentId === null || category.parentId === undefined ? 'root' : String(category.parentId)
    const values = groups.get(key) || []
    values.push(category)
    groups.set(key, values)
  })

  return groups
})
const mainCategories = computed(() =>
  categoryRows.value.filter((category) => category.parentId === null || Number(category.level) === 1),
)
const middleCategories = computed(() => getChildCategories(selectedMainCategoryId.value))
const subCategories = computed(() => getChildCategories(selectedMiddleCategoryId.value))
const selectedCategoryLabel = computed(() => {
  const selected = categoryById.value.get(Number(props.form.categoryId))

  return selected ? buildCategoryPathLabel(selected, categoryById.value) : ''
})
const canConfirmCategory = computed(() => {
  const selected = categoryById.value.get(Number(selectedSubCategoryId.value))

  return Boolean(selected?.selectable || selected?.hasChildren === false || Number(selected?.level) === 3)
})

function getChildCategories(parentId) {
  if (parentId === null || parentId === undefined || parentId === '') {
    return []
  }

  return categoriesByParentId.value.get(String(parentId)) || []
}

function hydrateCategorySelection() {
  const selected = categoryById.value.get(Number(props.form.categoryId))
  const ancestors = []
  let current = selected || mainCategories.value[0] || null

  while (current) {
    ancestors.unshift(current)

    if (current.parentId === null || current.parentId === undefined) {
      break
    }

    current = categoryById.value.get(Number(current.parentId)) || null
  }

  selectedMainCategoryId.value = ancestors.find((category) => Number(category.level) === 1)?.id
    ?? ancestors[0]?.id
    ?? mainCategories.value[0]?.id
    ?? null
  selectedMiddleCategoryId.value = ancestors.find((category) => Number(category.level) === 2)?.id
    ?? getChildCategories(selectedMainCategoryId.value)[0]?.id
    ?? null
  selectedSubCategoryId.value = selected?.id && Number(selected.level) >= 3
    ? selected.id
    : getChildCategories(selectedMiddleCategoryId.value)[0]?.id ?? null
}

function openCategoryModal() {
  hydrateCategorySelection()
  isCategoryModalOpen.value = true
}

function closeCategoryModal() {
  isCategoryModalOpen.value = false
}

function selectMainCategory(category) {
  selectedMainCategoryId.value = category.id
  selectedMiddleCategoryId.value = getChildCategories(category.id)[0]?.id ?? null
  selectedSubCategoryId.value = getChildCategories(selectedMiddleCategoryId.value)[0]?.id ?? null
}

function selectMiddleCategory(category) {
  selectedMiddleCategoryId.value = category.id
  selectedSubCategoryId.value = getChildCategories(category.id)[0]?.id ?? null
}

function selectSubCategory(category) {
  selectedSubCategoryId.value = category.id
}

function confirmCategorySelection() {
  if (!canConfirmCategory.value) {
    return
  }

  props.form.categoryId = String(selectedSubCategoryId.value)
  closeCategoryModal()
}

function openFilePicker() {
  if (remainingImageCount.value <= 0 || props.uploadInProgress) {
    return
  }

  fileInput.value?.click()
}

function setPrimaryImage(index) {
  emit('set-primary-image', index)
}

function removeImage(index) {
  emit('remove-image', index)
}

function slideThumbnails(direction) {
  thumbnailScroller.value?.scrollBy({
    left: direction * 138,
    behavior: 'smooth',
  })
}
</script>

<template>
  <section class="register-form-card">
    <div class="register-form-grid">
      <div class="register-upload-column">
        <label class="register-label">상품 이미지 <span>*</span></label>
        <input
          ref="fileInput"
          class="register-hidden-input"
          type="file"
          accept="image/*"
          multiple
          @change="$emit('files-selected', $event)"
        />

        <button
          type="button"
          class="register-upload-box"
          :disabled="uploadInProgress"
          @click="openFilePicker"
        >
          <img
            v-if="uploadedImages[0]"
            :src="uploadedImages[0].previewUrl"
            alt=""
            class="register-main-preview"
          />
          <span v-if="uploadedImages[0]" class="register-upload-overlay">
            {{ remainingImageCount > 0 ? '사진 추가' : '최대 업로드 완료' }}
          </span>
          <template v-else>
            <div class="register-upload-placeholder">
              <div class="register-upload-icon"><v-icon icon="mdi-camera-plus-outline" size="28" /></div>
              <span>{{ uploadInProgress ? '업로드 중...' : '이미지 업로드' }}</span>
            </div>
          </template>
        </button>

        <div class="register-thumb-strip">
          <button
            v-if="showThumbnailNav"
            type="button"
            class="register-thumb-nav is-left"
            aria-label="이전 사진 보기"
            @click="slideThumbnails(-1)"
          >
            <v-icon icon="mdi-chevron-left" size="18" />
          </button>

          <div ref="thumbnailScroller" class="register-thumb-row">
            <div
              v-for="(image, index) in uploadedImages"
              :key="`${image.name}-${index}`"
              class="register-thumb register-thumb-image"
              :class="{ 'is-primary': index === 0 }"
            >
              <button type="button" class="register-thumb-preview" @click="setPrimaryImage(index)">
                <img :src="image.previewUrl" :alt="image.name" />
                <span v-if="index === 0" class="register-thumb-badge">대표</span>
              </button>
              <button
                type="button"
                class="register-thumb-remove"
                :aria-label="`${image.name} 삭제`"
                @click="removeImage(index)"
              >
                <v-icon icon="mdi-close" size="16" />
              </button>
            </div>
            <button
              v-if="remainingImageCount > 0"
              type="button"
              class="register-thumb register-thumb-add"
              :disabled="uploadInProgress"
              @click="openFilePicker"
            >
              <span><v-icon icon="mdi-plus" size="18" /></span>
              <em>사진 추가</em>
            </button>
          </div>

          <button
            v-if="showThumbnailNav"
            type="button"
            class="register-thumb-nav is-right"
            aria-label="다음 사진 보기"
            @click="slideThumbnails(1)"
          >
            <v-icon icon="mdi-chevron-right" size="18" />
          </button>
        </div>

        <p class="register-helper-text">
          {{ imageHelperText }}
        </p>
      </div>

      <div class="register-fields-column">
        <label class="register-field">
          <span class="register-label">상품명 <span>*</span></span>
          <input
            v-model="form.name"
            type="text"
            :placeholder="currentMode === 'inspection' ? '상품명을 입력해 주세요.' : '상품명을 입력해주세요.'"
          />
        </label>

        <label class="register-field">
          <span class="register-label">카테고리 <span>*</span></span>
          <button type="button" class="register-category-trigger" @click="openCategoryModal">
            <span :class="{ 'is-placeholder': !selectedCategoryLabel }">
              {{ selectedCategoryLabel || (currentMode === 'inspection' ? '카테고리를 선택해 주세요.' : '카테고리를 선택해주세요.') }}
            </span>
          </button>
        </label>

        <label v-if="currentMode === 'direct'" class="register-field">
          <span class="register-label">상품 상태 <span>*</span></span>
          <div class="register-select-wrap">
            <select v-model="form.condition">
              <option value="" disabled>상품의 상태를 선택해주세요.</option>
              <option value="새상품">새상품</option>
              <option value="S">S</option>
              <option value="A+">A+</option>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
            </select>
          </div>
        </label>

        <label class="register-field">
          <span class="register-label">브랜드</span>
          <input
            v-model="form.brand"
            type="text"
            :placeholder="currentMode === 'inspection' ? '브랜드를 입력해 주세요.' : '브랜드를 입력해주세요.'"
          />
        </label>

        <label class="register-field">
          <span class="register-label">상세 설명 <span>*</span></span>
          <textarea
            v-model="form.description"
            placeholder="구매 시기, 보증서 유무, 하자 여부 등 상세한 내용을 입력해주세요."
          />
        </label>
      </div>
    </div>

    <div v-if="errorMessage" class="register-success-banner is-error">
      {{ errorMessage }}
    </div>

    <div v-else-if="submitted" class="register-success-banner">
      {{ successMessage }}
    </div>

    <div class="register-actions">
      <button type="button" class="register-secondary-button" :disabled="processing || uploadInProgress" @click="$emit('cancel')">
        취소
      </button>
      <button type="button" class="register-primary-button" :disabled="processing || uploadInProgress" @click="$emit('submit')">
        {{ processing ? '처리 중...' : '확인' }}
      </button>
    </div>

    <div v-if="isCategoryModalOpen" class="register-category-overlay" role="presentation">
      <div class="register-category-modal" role="dialog" aria-modal="true" aria-label="카테고리 선택">
        <div class="register-category-close-row">
          <button type="button" class="register-category-close" aria-label="카테고리 선택 닫기" @click="closeCategoryModal">
            ×
          </button>
        </div>

        <div class="register-category-columns">
          <div class="register-category-column is-bordered" aria-label="대분류">
            <button
              v-for="category in mainCategories"
              :key="category.id"
              type="button"
              class="register-category-option"
              :class="{ 'is-selected': selectedMainCategoryId === category.id }"
              @click="selectMainCategory(category)"
            >
              <span>{{ category.name }}</span>
              <span v-if="selectedMainCategoryId === category.id" class="register-category-chevron">›</span>
            </button>
          </div>

          <div class="register-category-column is-bordered" aria-label="중분류">
            <button
              v-for="category in middleCategories"
              :key="category.id"
              type="button"
              class="register-category-option"
              :class="{ 'is-selected': selectedMiddleCategoryId === category.id }"
              @click="selectMiddleCategory(category)"
            >
              <span>{{ category.name }}</span>
              <span v-if="selectedMiddleCategoryId === category.id" class="register-category-chevron">›</span>
            </button>
            <p v-if="!middleCategories.length" class="register-category-empty">
              대분류를 선택해주세요.
            </p>
          </div>

          <div class="register-category-column" aria-label="소분류">
            <button
              v-for="category in subCategories"
              :key="category.id"
              type="button"
              class="register-category-option"
              :class="{ 'is-selected': selectedSubCategoryId === category.id }"
              @click="selectSubCategory(category)"
            >
              <span>{{ category.name }}</span>
            </button>
            <p v-if="!subCategories.length" class="register-category-empty">
              중분류를 선택해주세요.
            </p>
          </div>
        </div>

        <div class="register-category-actions">
          <button type="button" class="register-category-cancel" @click="closeCategoryModal">
            취소
          </button>
          <button
            type="button"
            class="register-category-confirm"
            :disabled="!canConfirmCategory"
            @click="confirmCategorySelection"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  </section>
</template>
