import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container">
        <a class="navbar-brand" href="#">
          <i class="fas fa-gift me-2"></i>
          Sistema de Benefícios
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <a class="nav-link" routerLink="/beneficios" routerLinkActive="active">
                <i class="fas fa-list me-1"></i>
                Benefícios
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/beneficios/novo" routerLinkActive="active">
                <i class="fas fa-plus me-1"></i>
                Novo Benefício
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/transferencia" routerLinkActive="active">
                <i class="fas fa-exchange-alt me-1"></i>
                Transferência
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <main class="container mt-4">
      <router-outlet></router-outlet>
    </main>

    <footer class="bg-light text-center py-3 mt-5">
      <div class="container">
        <p class="text-muted mb-0">
          <i class="fas fa-code me-1"></i>
          Sistema de Gestão de Benefícios - Desenvolvido com Angular & Spring Boot
        </p>
      </div>
    </footer>
  `,
  styles: [`
    .navbar-brand {
      font-weight: 600;
      font-size: 1.2rem;
    }
    
    .nav-link {
      font-weight: 500;
      transition: all 0.3s ease;
    }
    
    .nav-link:hover {
      transform: translateY(-1px);
    }
    
    .nav-link.active {
      background-color: rgba(255,255,255,0.1);
      border-radius: 4px;
    }
    
    footer {
      border-top: 1px solid #e9ecef;
      margin-top: auto;
    }
  `]
})
export class AppComponent {
  title = 'beneficio-frontend';
}
