<script setup>
defineProps({
  assets: {
    type: Object,
    required: true,
  },
  badgeClass: {
    type: Function,
    required: true,
  },
  items: {
    type: Array,
    required: true,
  },
})

defineEmits(['open-detail'])
</script>

<template>
  <div class="inspection-card-grid">
    <article
      v-for="(item, index) in items"
      :key="`${item.title}-${index}`"
      class="inspection-product-card"
      @click="$emit('open-detail', item)"
    >
      <div class="inspection-product-image-wrap">
        <img :src="item.image || assets.listWatchImage" :alt="item.title" class="inspection-product-image" />
      </div>

      <div class="inspection-product-body">
        <span class="inspection-card-status" :class="badgeClass(item.status)">
          {{ item.statusLabel || item.status }}
        </span>
        <h3>{{ item.title }}</h3>
        <div class="inspection-product-meta">
          <div>
            <span>검수 등급</span>
            <strong>{{ item.inspectionGrade }}</strong>
          </div>
          <span>{{ item.inspectionDate }}</span>
        </div>
      </div>
    </article>
  </div>
</template>
