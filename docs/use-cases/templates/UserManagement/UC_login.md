## Use Case #1 – Accedi al Sistema (Log-in)

### Brief Description
L’ospite accede al sistema utilizzando Email e Password

### Level
Guest Goal

### Actors
Guest

### Pre-Conditions
L'utente deve essersi registrato in passato.

### Basic Flow
| Step | Descrizione                                 |
|------|---------------------------------------------|
| 1    | L’ospite clicca sul pulsante "Accedi"       |
| 2    | Il sistema mostra la finestra per il log in |
| 3    | L'ospite inserisce i dati necessari         |
| 4    | L'ospite clicca su Login                    |
| 5    | Il sistema carica la finestra del profilo   |

### Alternative Flow
1. Il sistema non trova nessun utente con le credenziali inserite:
   1. Mostra un messaggio di errore

### Post-Conditions
L'utente è autenticato nel sistema