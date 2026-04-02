# Use Case #8 - Inserimento dati passeggeri

### Brief Description
L’utente inserisce i dati dei passeggeri associati alla prenotazione.

### Level
User Goal

### Actors
User, Admin

### Pre-Conditions
L'utente deve essere autenticato e aver selezionato un volo e almeno un posto.

### Basic Flow
| Step | Descrizione                                                                 |
|------|-----------------------------------------------------------------------------|
| 1    | Il sistema carica la pagina di inserimento dati dei passeggeri (Mock-up #6) |
| 2    | L'utente inserisce, negli appositi campi, i dati dei passeggeri             |
| 3    | L'utente conferma i dati dei passeggeri cliccando sul pulsante "Continua"   |
| 4    | Il sistema salva nella sessione i dati dei passeggeri                       |
| 5    | Il sistema indirizza l'utente allo step di conferma (Mock-up #7)            |

### Alternative Flow
1.  L’utente sceglie di salvare il passeggero come companion.
    1. Il sistema memorizza il passeggero nella lista dei companion associati all’utente.

### Post-Conditions
L'utente ha inserito i dati dei passeggeri.