import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment'
import { Observable } from 'rxjs';
import { Beneficio, Transferencia } from '../models/beneficio.model';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private apiUrl = `${environment.apiBaseUrl}/api/v1/beneficios`;

  constructor(private http: HttpClient) { }

  listar(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`);
  }

  criar(beneficio: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, beneficio);
  }

  atualizar(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.apiUrl}/${id}`, beneficio);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  transferir(transferencia: Transferencia): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/transferir`, transferencia, {
      responseType: 'text' as 'json'
    });
  }

  buscarPorNome(nome: string): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.apiUrl}/buscar?nome=${encodeURIComponent(nome)}`);
  }
}
