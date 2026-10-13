import axios from 'axios';
import type { Alerta, Cerca, Posicao, Veiculo } from '../types/api';

const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL ?? '' });

export async function listarVeiculos(): Promise<Veiculo[]> {
  const { data } = await api.get<Veiculo[]>('/api/veiculos');
  return data;
}

export async function listarCercas(): Promise<Cerca[]> {
  const { data } = await api.get<Cerca[]>('/api/cercas');
  return data;
}

export async function listarAlertasNaoLidos(): Promise<Alerta[]> {
  const { data } = await api.get<Alerta[]>('/api/alertas');
  return data;
}

export async function marcarAlertaLido(id: number): Promise<void> {
  await api.patch(`/api/alertas/${id}/lido`);
}

export async function buscarTrajeto(veiculoId: number, inicio: string, fim: string): Promise<Posicao[]> {
  const { data } = await api.get<Posicao[]>(`/api/veiculos/${veiculoId}/trajeto`, { params: { inicio, fim } });
  return data;
}
