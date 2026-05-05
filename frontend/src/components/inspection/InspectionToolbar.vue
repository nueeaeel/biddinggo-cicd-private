<script setup>
defineProps({
  activeFilter: {
    type: String,
    required: true,
  },
  filterOptions: {
    type: Array,
    required: true,
  },
  searchQuery: {
    type: String,
    default: '',
  },
})

defineEmits(['update:activeFilter', 'update:searchQuery'])
</script>

<template>
  <div class="inspection-toolbar">
    <div class="inspection-filters">
      <button
        v-for="filter in filterOptions"
        :key="filter"
        type="button"
        class="inspection-filter"
        :class="{ 'is-active': activeFilter === filter }"
        @click="$emit('update:activeFilter', filter)"
      >
        {{ filter }}
      </button>
    </div>

    <div class="inspection-search">
      <v-icon icon="mdi-magnify" class="inspection-search-icon" size="18" />
      <input
        :value="searchQuery"
        type="search"
        class="inspection-search-input"
        placeholder="상품명, 브랜드 검색"
        @input="$emit('update:searchQuery', $event.target.value)"
      />
    </div>
  </div>
</template>
