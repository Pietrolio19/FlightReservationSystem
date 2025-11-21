## Use Case #3 – Modica del profilo utente

### Brief Description
L’utente modifica i dati associati al suo User.

### Level
User Goal

### Actors
User

### Pre-Conditions
L’utente deve essere autenticato ed essere nella pagina dedicata alla modifica del profilo (Mockup #5) accessibile dalla pagina del profilo (Mockup #4).

### Basic Flow
| Step | Descrizione                                                                     |
|------|---------------------------------------------------------------------------------|
| 1    | L’utente sceglie la pagina di modifica del profilo                              |
| 2    | L’utente inserisce o modifica i dati del suo profilo tramite gli appositi campi |
| 3    | L’utente invia i nuovi dati modificati                                          |
| 4    | Il sistema valida i dati inseriti (test da definire)                            |
| 5    | Il sistema aggiorna i dati associati all'utente nel DB                          |
| 6    | Il sistema notifica il successo dell'operazione                                 |

### Alternative Flow
| Step | Descrizione                                                                                                                                  |
|------|----------------------------------------------------------------------------------------------------------------------------------------------|
| 4a   | In caso la creazione dell'oggetto fallisca viene restituito un messaggio di errore e richiede di inserire i dati corretti (Test da definire) |
| 5a   | In caso fallisca la transazione sul DB il sistema restituisce un messaggio di errore e chiede di riprovare (Test da definire)                |


### Post-Conditions
I dati dell'utente risultano modificati nel sistema.