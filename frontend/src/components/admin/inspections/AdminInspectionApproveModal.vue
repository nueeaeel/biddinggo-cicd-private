<template>
  <div class="modal-backdrop admin-modal-backdrop" @click.self="$emit('close')">
    <section class="admin-approve-modal">
      <header class="admin-approve-modal__head">
        <h2>검수 등급 설정</h2>
        <button class="admin-approve-modal__close" type="button" @click="$emit('close')">
          <v-icon icon="mdi-close" />
        </button>
      </header>

      <form class="admin-approve-modal__form" @submit.prevent="submit">
        <div class="admin-approve-modal__grade-grid">
          <button
            v-for="grade in normalizedGrades"
            :key="grade"
            type="button"
            class="admin-approve-modal__grade-button"
            :class="{ 'is-active': selectedGrade === grade }"
            @click="selectedGrade = grade"
          >
            {{ grade }}
          </button>
        </div>

        <button class="admin-approve-modal__submit" type="submit" :disabled="!selectedGrade">
          검수 통과
        </button>
      </form>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  grades: {
    type: Array,
    default: () => ['새상품', 'S', 'A+', 'A', 'B', 'C'],
  },
})

const emit = defineEmits(['close', 'submit'])

const normalizedGrades = computed(() =>
  Array.isArray(props.grades) && props.grades.length
    ? props.grades
    : ['새상품', 'S', 'A+', 'A', 'B', 'C'],
)

const selectedGrade = ref('A')

function submit() {
  if (!selectedGrade.value) return
  emit('submit', selectedGrade.value)
}
</script>