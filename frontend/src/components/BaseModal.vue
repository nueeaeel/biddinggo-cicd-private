<template>
  <div v-if="open" class="modal-backdrop" @click="handleBackdropClick">
    <section :class="panelClass" @click.stop>
      <div class="base-modal__chrome">
        <p class="base-modal__label">{{ headerLabel || '\u00A0' }}</p>
        <slot name="action">
          <button class="base-modal__close" type="button" @click="emit('close')">
            <v-icon icon="mdi-close" />
          </button>
        </slot>
      </div>

      <div :class="bodyClass">
        <slot />
      </div>
    </section>
  </div>
</template>

<script setup>
const props = defineProps({
  open: {
    type: Boolean,
    default: true,
  },
  panelClass: {
    type: [String, Array, Object],
    default: '',
  },
  bodyClass: {
    type: [String, Array, Object],
    default: 'base-modal__body modal-scroll',
  },
  closeOnBackdrop: {
    type: Boolean,
    default: true,
  },
  headerLabel: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['close'])

function handleBackdropClick() {
  if (props.closeOnBackdrop) {
    emit('close')
  }
}
</script>
