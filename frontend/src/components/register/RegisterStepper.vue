<script setup>
defineProps({
  firstStepLabel: {
    type: String,
    required: true,
  },
  isAuctionStep: {
    type: Boolean,
    required: true,
  },
})
</script>

<template>
  <div class="register-stepper">
    <div class="register-step" :class="{ 'is-active': !isAuctionStep }">
      <span class="register-step-index" :class="{ 'is-muted': isAuctionStep }">1</span>
      <span>{{ firstStepLabel }}</span>
    </div>
    <span class="register-step-line" />
    <div class="register-step" :class="{ 'is-active': isAuctionStep }">
      <span class="register-step-index" :class="{ 'is-muted': !isAuctionStep }">2</span>
      <span>경매 등록</span>
    </div>
  </div>
</template>
