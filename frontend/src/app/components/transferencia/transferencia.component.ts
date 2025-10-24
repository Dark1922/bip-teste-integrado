import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { SnackService } from '../../services/snack.service';
import { Beneficio, Transferencia } from '../../models/beneficio.model';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './transferencia.component.html',
  styleUrls: ['./transferencia.component.css']
})
export class TransferenciaComponent implements OnInit {

  beneficios: Beneficio[] = [];
  transferencia: Transferencia = { fromId: 0, toId: 0, valor: 0 };
  processando = false;

  constructor(
    private beneficioService: BeneficioService,
    private snack: SnackService
  ) {}

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.beneficioService.listar().subscribe({
      next: (dados) => this.beneficios = dados,
      error: () => this.snack.error('Erro ao carregar benefícios.')
    });
  }

  get beneficioOrigem(): Beneficio | undefined {
    return this.beneficios.find(b => b.id === this.transferencia.fromId);
  }

  get beneficioDestino(): Beneficio | undefined {
    return this.beneficios.find(b => b.id === this.transferencia.toId);
  }

  transferir(): void {
    if (this.processando) return;
    this.processando = true;

    this.beneficioService.transferir(this.transferencia).subscribe({
      next: (msg) => {
        this.snack.ok(msg);
        this.transferencia = { fromId: 0, toId: 0, valor: 0 };
        this.carregarBeneficios();
        this.processando = false;
      },
      error: (err) => {
        this.snack.error('Erro na transferência: ' + err.error);
        this.processando = false;
      }
    });
  }
}
