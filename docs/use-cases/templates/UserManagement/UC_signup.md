## Use Case #2 – Registrazione nel Sistema (Sign-up)

### Brief Description
L’utente si registra nel sistema creando un nuovo User.

### Level
User Goal

### Actors
User

### Pre-Conditions
L’utente deve essere nella pagina dedicata alla registrazione (Mockup #3), la quale è raggiungibile tramite la pagine di login (Mockup #2).

### Basic Flow
| Step | Descrizione                                                      |
|------|------------------------------------------------------------------|
| 1    | L’utente sceglie la pagina di Sign-up (Mockup #3)                |
| 2    | L’utente inserisce i propri dati                                 |
| 3    | L’utente invia le proprie credenziali                            |
| 4    | Il sistema valida i dati                                         |
| 5    | Il sistema crea una entry User nella tabella del DB              |
| 6    | Il sistema conferma la creazione dell'account (Test da definire) |



### Alternative Flow
| Step | Descrizione                                                                                                                                     |
|------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| 4a   | In caso la creazione dell'oggetto fallisca viene restituito un messaggio di errore e si fornisce la possibilità di ritentare (Test da definire) |
| 5a   | In caso fallisca la transazione sul DB il sistema restituisce un messaggio di errore e chiede di riprovare (Test da definire)                   |


### Post-Conditions
L’utente ha creato un nuovo account.