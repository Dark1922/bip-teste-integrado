import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';
import { SnackService } from '../../services/snack.service';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './beneficio-form.component.html',
  styleUrls: ['./beneficio-form.component.css']
})
export class BeneficioFormComponent implements OnInit {

  beneficio: Beneficio = { nome: '', descricao: '', valor: 0, ativo: true };
  isEdicao = false;
  salvando = false;

  constructor(
    private beneficioService: BeneficioService,
    private route: ActivatedRoute,
    private router: Router,
    private snack: SnackService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdicao = true;
      this.carregarBeneficio(parseInt(id));
    }
  }

  carregarBeneficio(id: number): void {
    this.beneficioService.buscarPorId(id).subscribe({
      next: (beneficio) => this.beneficio = beneficio,
      error: () => {
        this.snack.error('Erro ao carregar benefício.');
        this.router.navigate(['/beneficios']);
      }
    });
  }

  salvar(): void {
    if (this.salvando) return;
    this.salvando = true;

    const operacao = this.isEdicao
      ? this.beneficioService.atualizar(this.beneficio.id!, this.beneficio)
      : this.beneficioService.criar(this.beneficio);

    operacao.subscribe({
      next: () => {
        this.snack.ok(this.isEdicao ? 'Benefício atualizado!' : 'Benefício criado!');
        this.router.navigate(['/beneficios']);
      },
      error: () => {
        this.snack.error('Erro ao salvar benefício.');
        this.salvando = false;
      }
    });
  }
}
