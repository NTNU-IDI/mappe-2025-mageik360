# Dagbok-applikasjon (IDATx1003)

Dette er en tekstbasert dagbok-applikasjon utviklet som mappeleveranse i emnet IDATx1003 Programmering 1 (Høst 2025). Applikasjonen følger objektorienterte prinsipper med en lagdelt arkitektur.

## Funksjonalitet
* **Dagbok:** Opprette, lese, redigere og slette dagbokinnlegg.
* **Søk:** Søk etter innlegg basert på dato, tidsrom eller ord.
* **Tilgangskontroll:** Innlogging med skille mellom vanlige brukere og administrator.
* **Statistikk:** Visning av statistikk for egne innlegg.
* **Admin:** Egen administrator-rolle med tilgang til globale data og sletting av brukere.

| Rolle | Brukernavn | Passord | Beskrivelse |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Har tilgang til "Global Statistics" og "System Reset". Kan se alle innlegg. |
| **Bruker** | `Lars` | `password` | Vanlig bruker. Har noen forhåndsskrevne innlegg. |
| **Bruker** | `Lisa` | `password` | Vanlig bruker. Har noen forhåndsskrevne innlegg. |

## Forutsetninger
* Java 21
* Maven

## Hvordan kjøre programmet
Kjør programmet i IntelliJ fra Main- klassen.

Du kan også kjøre programmet direkte fra terminalen ved hjelp av Maven:

```bash
mvn clean compile exec:java

