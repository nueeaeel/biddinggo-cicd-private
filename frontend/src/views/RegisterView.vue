<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import RegisterScreen from '../components/RegisterScreen.vue'

const route = useRoute()
const allowedModes = ['select', 'inspection', 'inspection-pick', 'direct', 'direct-auction']

const initialMode = computed(() => {
  const mode = typeof route.query.mode === 'string' ? route.query.mode : 'select'
  return allowedModes.includes(mode) ? mode : 'select'
})

const initialInspectionId = computed(() => {
  const value = Number(route.query.inspectionId)
  return Number.isFinite(value) && value > 0 ? value : null
})
</script>

<template>
  <RegisterScreen :initial-inspection-id="initialInspectionId" :initial-mode="initialMode" />
</template>
