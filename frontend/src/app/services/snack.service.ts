import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class SnackService {
  private snackbar = inject(MatSnackBar);

  ok(message: string) {
    this.snackbar.open(message, 'OK', { duration: 2500, panelClass: ['snack-success'] });
  }
  error(message: string) {
    this.snackbar.open(message, 'Fechar', { duration: 4000, panelClass: ['snack-error'] });
  }
}
