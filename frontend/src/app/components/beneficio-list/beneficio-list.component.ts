import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { SnackService } from '../../services/snack.service';
import { Beneficio } from '../../models/beneficio.model';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './beneficio-list.component.html',
  styleUrls: ['./beneficio-list.component.css']
})
export class BeneficioListComponent implements OnInit {

  beneficios: Beneficio[] = [];
  filtroNome = '';
  carregando = false;

  constructor(
    private beneficioService: BeneficioService,
    private snack: SnackService
  ) {}

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.carregando = true;
    this.beneficioService.listar().subscribe({
      next: (dados) => {
        this.beneficios = dados;
        this.carregando = false;
      },
      error: () => {
        this.snack.error('Erro ao carregar benefícios.');
        this.carregando = false;
      }
    });
  }

  buscar(): void {
    if (!this.filtroNome.trim()) {
      this.carregarBeneficios();
      return;
    }

    this.carregando = true;
    this.beneficioService.buscarPorNome(this.filtroNome).subscribe({
      next: (dados) => {
        this.beneficios = dados;
        this.carregando = false;
      },
      error: () => {
        this.snack.error('Erro na busca.');
        this.carregando = false;
      }
    });
  }

  excluir(id: number): void {
    if (!confirm('Deseja excluir este benefício?')) return;
    this.beneficioService.excluir(id).subscribe({
      next: () => {
        this.snack.ok('Benefício excluído!');
        this.carregarBeneficios();
      },
      error: () => this.snack.error('Erro ao excluir benefício.')
    });
  }
}
