export interface Veiculo {
  id: number;
  placa: string;
  descricao: string;
  ativo: boolean;
  latitude: number | null;
  longitude: number | null;
  velocidade: string | null;
  registradoEm: string | null;
}

export interface Posicao {
  id: number;
  veiculoId: number;
  latitude: number;
  longitude: number;
  velocidade: string;
  registradoEm: string;
}

export type TipoAlertaCerca = 'ENTRADA' | 'SAIDA' | 'AMBOS';

export interface Cerca {
  id: number;
  nome: string;
  tipoAlerta: TipoAlertaCerca;
  velocidadeMaxima: string | null;
  pontos: { latitude: number; longitude: number }[];
}

export type TipoAlerta = 'ENTROU' | 'SAIU' | 'EXCESSO_VELOCIDADE';

export interface Alerta {
  id: number;
  veiculoId: number;
  cercaId: number;
  tipo: TipoAlerta;
  ocorridoEm: string;
  lido: boolean;
}
