# Use Case #6 - Prenotazione del volo

### Brief Description
L’utente sceglie il volo da prenotare in seguito ad una ricerca.

### Level
User Goal

### Actors
User, Admin

### Pre-Conditions
L'utente deve essere autenticato.

### Basic Flow
| Step | Descrizione                                                                    |
|------|--------------------------------------------------------------------------------|
| 1    | Il sistema carica la pagina di ricerca voli (Mock-up #1)                       |
| 2    | L'utente inserisce, negli appositi campi, i dati per la ricerca                |
| 3    | Il sistema mostra i voli disponibili in base ai criteri di ricerca             |
| 4    | L'utente scorre può scorrere i voli                                            |
| 5    | L'utente sceglie il volo cliccando sul pulsante "Prenota"                      |
| 6    | Il sistema salva il volo scelto nella sessione corrente                        |
| 7    | Il sistema indirizza l'utente verso lo step di prenotazione posti (Mock-up #5) |

### Alternative Flow
1. Se l'utente non è autenticato viene reindirizzato alla pagina di log in (Mock-up #2).
2. Se l'utente non esegue una ricerca può comunque scegliere un volo ma il numero di passeggeri massimo sarà 1.
3. Se il volo cercato non ha posti disponibili verrà mostrato un avviso: "Nessun volo disponibile per la ricerca"

### Post-Conditions
L'utente ha scelto il volo da prenotare.