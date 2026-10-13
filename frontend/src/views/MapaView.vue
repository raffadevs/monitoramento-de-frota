<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref } from 'vue';
import L from 'leaflet';
import type { Client } from '@stomp/stompjs';
import { buscarTrajeto, listarAlertasNaoLidos, listarCercas, listarVeiculos, marcarAlertaLido } from '../services/api';
import { conectarTempoReal } from '../services/realtime';
import type { Alerta, Posicao, Veiculo } from '../types/api';
import AlertasPanel from '../components/AlertasPanel.vue';

const veiculos = ref<Veiculo[]>([]);
const alertas = ref<Alerta[]>([]);
const carregando = ref(true);

let mapa: L.Map | null = null;
let clienteTempoReal: Client | null = null;
let trajetoLayer: L.Polyline | null = null;
const marcadores = new Map<number, L.Marker>();

function iconePara(velocidade: number | null): L.DivIcon {
  const cor = velocidade !== null && velocidade > 0 ? '#5b4ff2' : '#94a3b8';
  return L.divIcon({
    className: '',
    html: `<div style="background:${cor};width:14px;height:14px;border-radius:50%;border:2px solid white;box-shadow:0 1px 3px rgba(0,0,0,.4)"></div>`,
    iconSize: [14, 14],
  });
}

function atualizarMarcador(veiculoId: number, lat: number, lon: number, velocidade: number) {
  const existente = marcadores.get(veiculoId);
  if (existente) {
    existente.setLatLng([lat, lon]);
    existente.setIcon(iconePara(velocidade));
  } else if (mapa) {
    const marcador = L.marker([lat, lon], { icon: iconePara(velocidade) }).addTo(mapa);
    marcador.on('click', () => selecionarVeiculo(veiculoId));
    marcadores.set(veiculoId, marcador);
  }
}

function nomeVeiculo(veiculoId: number): string {
  return veiculos.value.find((v) => v.id === veiculoId)?.placa ?? `#${veiculoId}`;
}

const cercasPorId = reactive<Record<number, string>>({});
function nomeCerca(cercaId: number): string {
  return cercasPorId[cercaId] ?? `#${cercaId}`;
}

async function selecionarVeiculo(veiculoId: number) {
  if (!mapa) return;
  const agora = new Date();
  const seiHorasAtras = new Date(agora.getTime() - 6 * 60 * 60 * 1000);
  const posicoes = await buscarTrajeto(veiculoId, seiHorasAtras.toISOString(), agora.toISOString());

  if (trajetoLayer) {
    mapa.removeLayer(trajetoLayer);
  }
  if (posicoes.length > 0) {
    const pontos: L.LatLngExpression[] = posicoes.map((p: Posicao) => [p.latitude, p.longitude]);
    trajetoLayer = L.polyline(pontos, { color: '#5b4ff2', weight: 3 }).addTo(mapa);
    mapa.fitBounds(trajetoLayer.getBounds(), { maxZoom: 16 });
  }
}

async function aoMarcarLido(id: number) {
  await marcarAlertaLido(id);
  alertas.value = alertas.value.filter((a) => a.id !== id);
}

onMounted(async () => {
  mapa = L.map('mapa').setView([-23.561, -46.655], 13);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap',
  }).addTo(mapa);

  const [listaVeiculos, listaCercas, listaAlertas] = await Promise.all([
    listarVeiculos(), listarCercas(), listarAlertasNaoLidos(),
  ]);
  veiculos.value = listaVeiculos;
  alertas.value = listaAlertas;
  carregando.value = false;

  for (const cerca of listaCercas) {
    cercasPorId[cerca.id] = cerca.nome;
    const pontos: L.LatLngExpression[] = cerca.pontos.map((p) => [p.latitude, p.longitude]);
    L.polygon(pontos, { color: '#d64545', weight: 2, fillOpacity: 0.08 })
        .addTo(mapa!)
        .bindTooltip(cerca.nome);
  }

  for (const veiculo of listaVeiculos) {
    if (veiculo.latitude != null && veiculo.longitude != null) {
      atualizarMarcador(veiculo.id, veiculo.latitude, veiculo.longitude, Number(veiculo.velocidade ?? 0));
    }
  }

  clienteTempoReal = conectarTempoReal(
      (posicao) => atualizarMarcador(posicao.veiculoId, posicao.latitude, posicao.longitude, Number(posicao.velocidade)),
      (alerta) => { alertas.value = [alerta, ...alertas.value]; },
  );
});

onUnmounted(() => {
  clienteTempoReal?.deactivate();
  mapa?.remove();
});
</script>

<template>
  <div class="h-full flex">
    <div id="mapa" class="flex-1"></div>

    <aside class="w-80 shrink-0 border-l border-slate-200 bg-slate-50 p-3 overflow-y-auto flex flex-col gap-4">
      <AlertasPanel :alertas="alertas" :nome-veiculo="nomeVeiculo" :nome-cerca="nomeCerca" @marcar-lido="aoMarcarLido" />

      <div>
        <h2 class="text-sm font-semibold text-slate-700 mb-2">Veículos ({{ veiculos.length }})</h2>
        <div v-if="carregando" class="text-sm text-slate-500">Carregando...</div>
        <ul v-else class="flex flex-col gap-1">
          <li v-for="veiculo in veiculos" :key="veiculo.id">
            <button type="button" class="w-full text-left text-sm bg-white border border-slate-200 rounded-md px-2 py-1.5 hover:border-indigo-400"
                    @click="selecionarVeiculo(veiculo.id)">
              {{ veiculo.placa }} — {{ veiculo.velocidade ? Number(veiculo.velocidade).toFixed(0) + ' km/h' : 'sem posição' }}
            </button>
          </li>
        </ul>
      </div>
    </aside>
  </div>
</template>
