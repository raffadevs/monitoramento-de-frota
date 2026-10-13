import { Client } from '@stomp/stompjs';
import type { Alerta, Posicao } from '../types/api';

export function conectarTempoReal(
    aoReceberPosicao: (posicao: Posicao) => void,
    aoReceberAlerta: (alerta: Alerta) => void,
): Client {
  const client = new Client({
    brokerURL: import.meta.env.VITE_WS_URL ?? 'ws://localhost:8077/ws',
    reconnectDelay: 3000,
    onConnect: () => {
      client.subscribe('/topic/posicoes', (mensagem) => {
        aoReceberPosicao(JSON.parse(mensagem.body) as Posicao);
      });
      client.subscribe('/topic/alertas', (mensagem) => {
        aoReceberAlerta(JSON.parse(mensagem.body) as Alerta);
      });
    },
  });
  client.activate();
  return client;
}
