export interface Beneficio {
  id?: number;
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
  version?: number;
}

export interface Transferencia {
  fromId: number;
  toId: number;
  valor: number;
}
