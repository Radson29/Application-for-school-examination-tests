# QUIZY - Aplikacja do przeprowadzania testów w szkole

## Opis projektu

QUIZY to aplikacja desktopowa stworzona z myślą o przeprowadzaniu testów w szkołach. Umożliwia ona nauczycielom i dyrektorom zarządzanie testami oraz uczniami, zapewniając uczniom możliwość rozwiązywania testów w intuicyjnym środowisku.

### Kluczowe funkcjonalności:
- **Losowe zestawy pytań**: Każdy uczeń otrzymuje inny zestaw pytań z danej puli, z losową kolejnością odpowiedzi.
- **Rodzaje testów**: Obsługa testów jednokrotnego i wielokrotnego wyboru.
- **Zarządzanie czasem**: Testy są ograniczone czasowo, uruchamiane i kończone zgodnie z ustalonym harmonogramem.
- **Raporty wyników**: Uczniowie mogą przeglądać swoje wyniki, a nauczyciele generować raporty z testów.
- **Zarządzanie użytkownikami**:
  - Dyrektor: zarządza nauczycielami.
  - Nauczyciel: dodaje uczniów, tworzy testy, grupy pytań i zarządza ich harmonogramem.
  - Uczeń: rozwiązuje testy i przegląda swoje wyniki.

### Diagram przypadków użycia
![Diagram przypadków użycia](https://github.com/user-attachments/assets/ef696e80-f8aa-444a-8f65-f525d581e5f5)
### Schemat bazy danych
![Schemat bazy danych](https://github.com/Radson29/Application-for-conducting-tests-at-school/blob/main/backend/ERD_v2.png)

---

## Instalacja

### Krok 1: Pobranie instalatora
Pobierz instalator i uruchom go.

### Krok 2: Wybór konfiguracji
Podczas instalacji wybierz:
- **Baza danych**: lokalna lub zewnętrzna.
- **Opcja bazy danych**: seedowana (z wstępnymi danymi) lub pusta.

### Krok 3: Instalacja
Kliknij przycisk `Install`, a następnie uruchom aplikację z pulpitu.

![Instrukcja instalacji](https://github.com/Radson29/Application-for-conducting-tests-at-school/blob/main/Podrecznik_uzytkowania.pdf)

---

## Użytkowanie aplikacji

### Dostępne konta:
- **Dyrektor**: Login: `root`, Hasło: `root`
- **Nauczyciel**: Login: `teacher`, Hasło: `123`
- **Uczeń**: Login: `bear.b`, `kurt.c`, `patryk.s`, `tomasz.d`, Hasło: `123`

---

### Widoki aplikacji:
#### Panel Dyrektora
- Zarządzanie nauczycielami (dodawanie, edytowanie, usuwanie).

#### Panel Nauczyciela
- Zarządzanie uczniami, grupami, bazami pytań i testami.
- Planowanie harmonogramu testów.
- Przeglądanie wyników uczniów.

#### Panel Ucznia
- Rozwiązywanie testów.
- Podgląd wyników z poprzednich testów.

---

## Podgląd aplikacji

![Zrzuty ekranu](https://github.com/Radson29/Application-for-conducting-tests-at-school/blob/main/Podrecznik_uzytkowania.pdf)

---

## Technologie

Projekt wykorzystuje następujące technologie:

- **Java** - język programowania.
- **Spring** - framework do budowy backendu.
- **React** - biblioteka frontendowa do budowy interfejsu użytkownika.
- **Tailwind CSS** - framework do stylizacji aplikacji.
- **JUnit 5** - do testów jednostkowych.

---

## Licencja
Copyright <2024> <Radosław Kaczka>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



