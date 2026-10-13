import { describe, expect, it } from 'vitest';
import { mount } from '@vue/test-utils';
import AlertasPanel from '../components/AlertasPanel.vue';
import type { Alerta } from '../types/api';

function alertaDeExemplo(overrides: Partial<Alerta> = {}): Alerta {
  return {
    id: 1, veiculoId: 10, cercaId: 20, tipo: 'ENTROU',
    ocorridoEm: new Date().toISOString(), lido: false,
    ...overrides,
  };
}

describe('AlertasPanel', () => {
  it('mostra a contagem e o texto de cada alerta', () => {
    const wrapper = mount(AlertasPanel, {
      props: {
        alertas: [alertaDeExemplo()],
        nomeVeiculo: () => 'ABC-1234',
        nomeCerca: () => 'Região Central',
      },
    });

    expect(wrapper.text()).toContain('Alertas (1)');
    expect(wrapper.text()).toContain('ABC-1234');
    expect(wrapper.text()).toContain('entrou em');
    expect(wrapper.text()).toContain('Região Central');
  });

  it('emite marcarLido com o id do alerta clicado', async () => {
    const wrapper = mount(AlertasPanel, {
      props: {
        alertas: [alertaDeExemplo({ id: 42 })],
        nomeVeiculo: () => 'ABC-1234',
        nomeCerca: () => 'Região Central',
      },
    });

    await wrapper.find('button').trigger('click');

    expect(wrapper.emitted('marcarLido')).toEqual([[42]]);
  });

  it('mostra mensagem quando não há alertas', () => {
    const wrapper = mount(AlertasPanel, {
      props: { alertas: [], nomeVeiculo: () => '', nomeCerca: () => '' },
    });

    expect(wrapper.text()).toContain('Nenhum alerta no momento');
  });
});
