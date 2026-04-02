## Use Case #2 – Registrazione nel Sistema (Sign-up)

### Brief Description
L’ospite si registra nel sistema fornendo Username, Email e Password.

### Level
Guest Goal

### Actors
Guest

### Basic Flow
| Step | Descrizione                                  |
|------|----------------------------------------------|
| 1    | L’ospite clicca sul pulsante "Accedi"        |
| 2    | Il sistema mostra la finestra per il log in  |
| 3    | L’ospite clicca sul pulsante "Resistrati"    |
| 4    | Il sistema mostra la finestra per la sing up |
| 5    | L'ospite inserisce i dati necessari          |
| 6    | L'ospite clicca su "Registrati"               |
| 7 | Il sistema carica la finestra del profilo    |

### Alternative Flow
1. Il sistema rileva la presenza di un account già esistente:
    1. Mostra un messaggio di errore: "Username già in uso"
    2. Mostra un messaggio di errore: "Email già in uso"
2. Il sistema rileva che la lunghezza della password è errata:
    1. Mostra un messaggio di errore: "La password deve contenere almeno 8 caratteri"

### Post-Conditions
L'utente è registrato nel sistema e autenticato