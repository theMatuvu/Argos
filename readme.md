# Argos

Supervision d'équipements et suivi d'incidents — projet ALTN72 (2026-27), Juba LOUDAHI et Matthieu JUIN.

- `Argos-backend` : API REST Spring Boot (Java 25, MySQL)
- `Argos-frontend` : interface Angular

Les choix techniques, les tests et le lot alpha sont détaillés dans [journal.md](journal.md).

## Version en ligne

Rien à installer :

- Application : https://argos-rd5f.onrender.com
- Swagger : https://argos-rd5f.onrender.com/api/swagger-ui.html

## Comptes de test

| Profil | Login | Mot de passe |
|---|---|---|
| Responsable d'exploitation | `responsable` | `Argos2026!` |
| Agent de terrain | `agent` | `Argos2026!` |

## Lancer en local

### Prérequis

- Java 25
- MySQL 8
- Node.js 22 (ou plus récent) et npm

Maven n'a pas besoin d'être installé : le wrapper `mvnw` est fourni.

### 1. Créer la base

```sql
CREATE DATABASE app;
```

C'est la seule étape manuelle : à chaque démarrage, le backend recrée les tables et insère les données de démonstration (stations, équipements, prestataires, incidents). Pour remettre la base à zéro, il suffit de relancer le backend.

### 2. Lancer le backend

Les accès à MySQL sont lus dans des variables d'environnement. Il n'y a besoin de les définir que si votre configuration est différente des valeurs par défaut :

| Variable | Valeur par défaut |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/app` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(vide)* |
| `PORT` | `8080` |

Bash (Linux, macOS, Git Bash) :

```bash
cd Argos-backend
export DB_PASSWORD=votre_mot_de_passe   # si besoin
./mvnw spring-boot:run
```

PowerShell (Windows) :

```powershell
cd Argos-backend
$env:DB_PASSWORD="votre_mot_de_passe"   # si besoin
.\mvnw.cmd spring-boot:run
```

Le backend démarre sur http://localhost:8080/api.

### 3. Lancer le frontend

Dans un second terminal :

```bash
cd Argos-frontend
npm install
npm start
```

L'application est accessible sur **http://localhost:4200**. Les appels à `/api` sont redirigés vers le backend (`proxy.conf.json`).

## Tests

```bash
cd Argos-backend
./mvnw verify
```

Les tests n'ont pas besoin de base de données.

## Documentation de l'API

Swagger : http://localhost:8080/api/swagger-ui.html

L'API est protégée par session : appelez d'abord `POST /auth/login` depuis Swagger avec l'un des comptes de test, les autres routes sont ensuite accessibles.