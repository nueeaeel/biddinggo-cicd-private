<script setup>
defineProps({
  methods: {
    type: Array,
    required: true,
  },
})

defineEmits(['select-mode'])
</script>

<template>
  <section class="register-method-grid">
    <article
      v-for="method in methods"
      :key="method.title"
      class="register-method-card"
      :class="method.cardClass"
    >
      <div class="register-method-icon-wrap">
        <v-icon :icon="method.icon" class="register-method-icon" />
      </div>

      <h3>{{ method.title }}</h3>

      <ul class="register-method-points">
        <li
          v-for="point in method.points"
          :key="point"
          class="register-method-point"
        >
          <v-icon :icon="method.bulletIcon" class="register-method-bullet" size="16" />
          <span>{{ point }}</span>
        </li>
      </ul>

      <button
        type="button"
        class="register-method-button"
        :class="method.buttonClass"
        @click="$emit('select-mode', method.key)"
      >
        {{ method.buttonLabel }}
      </button>
    </article>
  </section>
</template>
