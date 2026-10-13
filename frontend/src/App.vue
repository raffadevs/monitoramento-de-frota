<script setup lang="ts">
import MapaView from './views/MapaView.vue';
</script>

<template>
  <div class="h-full flex flex-col">
    <header class="border-b border-slate-200 bg-white shrink-0">
      <div class="px-5 py-2.5">
        <span class="font-bold text-slate-900">monitoramento-de-frota</span>
        <span class="text-sm text-slate-500 ml-2">rastreamento em tempo real</span>
      </div>
    </header>

    <main class="flex-1 min-h-0">
      <MapaView />
    </main>
  </div>
</template>
