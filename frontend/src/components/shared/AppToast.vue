<template>
  <v-snackbar
    v-model="toast.visible"
    :color="toast.color"
    :timeout="toast.timeout"
    location="bottom right"
  >
    {{ toast.message }}

    <template #actions>
      <v-btn variant="text" @click="hideToast">닫기</v-btn>
    </template>
  </v-snackbar>
</template>

<script setup>
import { useToast } from '../../composables/useToast'

const { hideToast, toast } = useToast()
</script>
