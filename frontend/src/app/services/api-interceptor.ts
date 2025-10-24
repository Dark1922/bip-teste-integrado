import { Injectable, inject } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SnackService } from './snack.service';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
  private snack = inject(SnackService);
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        const msg = (err.error && (err.error.erro || err.error.message)) || err.message || 'Erro inesperado';
        this.snack.error(msg);
        return throwError(() => err);
      })
    );
  }
}
