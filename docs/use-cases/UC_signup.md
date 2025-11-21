## Use Case #2 – Registrati nel Sistema (Sign-up)

### Brief Description
L’utente si registra nel sistema creando un nuovo User.

### Level
User Goal

### Actors
Admin, Cliente

### Pre-Conditions
L’utente deve essere nella pagina dedicata alla registrazione (Mockup #3).

### Basic Flow
| Step | Descrizione                                                      |
|------|------------------------------------------------------------------|
| 1    | L’utente sceglie la pagina di Sign-up (Mockup #3)                |
| 2    | L’utente inserisce i propri dati                                 |
| 3    | L’utente invia le proprie credenziali                            |
| 4    | Il sistema crea un oggetto User                                  |
| 5    | Il sistema conferma la creazione dell'account (Test da definire) |
| 6    | Il sistema crea una entry User nella tabella del DB              |


### Alternative Flow
| Step | Descrizione                                                                                                                                     |
|------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| 4a   | In caso la creazione dell'oggetto fallisca viene restituito un messaggio di errore e si fornisce la possibilità di ritentare (Test da definire) |
| 6a   | In caso fallisca la transazione sul DB l'oggetto rimane disponibile in memoria e il sistema ritenta la creazione della entry (Test da definire) |


### Post-Conditions
L’utente ha creato un nuovo account.