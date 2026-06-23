# HR Application
**Digitalni Sistem za Upravljanje Zahtjevima Zaposlenika**

Ovaj repozitorij sadrži izvorni kod za **HR Application**, modernu Android aplikaciju dizajniranu za digitalizaciju i automatizaciju podnošenja, obrade i odobravanja zahtjeva za odsustvo unutar obrazovnih institucija.

---

## 🛠️ Tehnološki Stack
* **Jezik:** Kotlin
* **Korisnički Interfejs:** Jetpack Compose (uz Material Design 3 komponente)
* **Backend infrastruktura:** Firebase Firestore (NoSQL Baza u oblaku)
* **Upravljanje asinhronim procesima:** Kotlin Coroutines & Flows
* **Struktura navigacije:** Compose Navigation

---

## 🚀 Upute za pokretanje projekta
Pratite ove korake kako biste uspješno postavili i pokrenuli aplikaciju na svom računaru:

### 1. Priprema okruženja
* Instalirajte najnoviju verziju **Android Studio**.
* Provjerite imate li instaliran **JDK 17** ili noviji.
* Osigurajte stabilnu internet vezu za sinhronizaciju biblioteka i povezivanje sa Firebase-om.

### 2. Konfiguracija Firebase-a (Ključni korak)
Aplikacija koristi Firebase Firestore za pohranu podataka. Potrebno je povezati projekt sa vašim Google računom:
1. Otvorite Firebase Console.
2. Kliknite na **Add Project** i dajte mu ime (npr. "HR-Application").
3. Dodajte Android aplikaciju u projekt. Package name mora biti identičan onom u kodu (`com.example.hrapplication`).
4. Preuzmite fajl `google-services.json`.
5. U Android Studiju, prebacite pogled na **Project** (iznad stabla fajlova) i ubacite preuzeti fajl direktno u folder `app/`.

### 3. Podešavanje baze podataka (Firestore)
1. U Firebase meniju s lijeve strane izaberite **Build > Firestore Database**. Kliknite na **Create Database**.
2. Odaberite lokaciju servera i pokrenite bazu u **Test Mode** (kako biste omogućili čitanje i pisanje bez početne autentifikacije).
3. Ručno kreirajte kolekciju pod nazivom `requests` (ili pustite da je aplikacija sama kreira pri prvom slanju zahtjeva).

### 4. Import i sinhronizacija projekta
1. Otvorite Android Studio i odaberite **File > Open**, pa pronađite folder svog projekta.
2. Sačekajte da Android Studio automatski pokrene Gradle Sync.

> **Napomena:** Ako se sinhronizacija ne pokrene ili prijavi grešku, kliknite na ikonu slona (**Sync Project with Gradle Files**) u gornjem desnom uglu.

### 5. Pokretanje aplikacije
* **Emulator:** Otvorite Device Manager, kreirajte i pokrenite virtualni uređaj (preporučeno Pixel 6, API 24 ili veći).
* Kliknite na zelenu **Run** ikonu (Play dugme) u gornjem baru Android Studija.

---

## 🧪 Testiranje Funkcionalnosti (QA Protocol)
Nakon pokretanja aplikacije, preporučuje se provođenje sljedećih testnih scenarija:

* **Slanje zahtjeva:** Kliknite na crvenu ikonu sa olovkom u donjem desnom uglu. Popunite formu. Obratite pažnju da dugme "Submit" postane aktivno tek kada odaberete oba datuma.
* **Provjera u bazi:** Nakon klika na "Submit", otvorite Firebase konzolu. Trebali biste vidjeti novi dokument u kolekciji `requests`.
* **Pregled detalja:** Kliknite na bilo koji zahtjev u listi na početnom ekranu. Provjerite da li su svi podaci (uključujući napomenu) ispravno preneseni.