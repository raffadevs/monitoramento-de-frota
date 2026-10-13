<script setup lang="ts">
import type { Alerta } from '../types/api';

defineProps<{ alertas: Alerta[]; nomeVeiculo: (veiculoId: number) => string; nomeCerca: (cercaId: number) => string }>();
const emit = defineEmits<{ marcarLido: [id: number] }>();

const ROTULOS: Record<Alerta['tipo'], string> = {
  ENTROU: 'entrou em',
  SAIU: 'saiu de',
  EXCESSO_VELOCIDADE: 'excesso de velocidade em',
};

function formatarHorario(iso: string): string {
  return new Date(iso).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
}
</script>

<template>
  <div class="flex flex-col gap-2">
    <h2 class="text-sm font-semibold text-slate-700">Alertas ({{ alertas.length }})</h2>
    <ul class="flex flex-col gap-1.5 overflow-y-auto">
      <li v-for="alerta in alertas" :key="alerta.id"
          class="bg-white border border-slate-200 rounded-md p-2 text-sm flex items-start justify-between gap-2"
          :class="{ 'border-red-300 bg-red-50': alerta.tipo === 'EXCESSO_VELOCIDADE' }">
        <div>
          <p class="text-slate-900">
            {{ nomeVeiculo(alerta.veiculoId) }} {{ ROTULOS[alerta.tipo] }} {{ nomeCerca(alerta.cercaId) }}
          </p>
          <p class="text-xs text-slate-400">{{ formatarHorario(alerta.ocorridoEm) }}</p>
        </div>
        <button type="button" class="text-xs text-indigo-600 shrink-0" @click="emit('marcarLido', alerta.id)">
          ok
        </button>
      </li>
      <li v-if="alertas.length === 0" class="text-sm text-slate-400">Nenhum alerta no momento.</li>
    </ul>
  </div>
</template>
