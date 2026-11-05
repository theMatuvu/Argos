- java spring boot (security, data JPA, ...)
- front en angular 
- DB Mysql
- Architecture hexagonale

Argos le géant aux cent yeux de la mythologie grecque, qui ne dormait jamais complètement. C’est l’outil qui surveille tout le réseau en permanence.

sonarqube
render + aiven


profil : 
- admin (tous les droits et peut gerer les droits des autres)
- responsable exploitation (vision claire de la situation : dashboard, assigner)
- agent (siganle les pannes)
- utilisateurs (peut consulter les pannes via une map peut etre inshallah)
- prestataire (liste d'equipement a reparer)    


choix des sessions avec session coté serveur et cokkie jsession id avec protection CSRF pour plusieurs raisons : 
- demander dans le sujet 
- angular le gere seul
    -> moins de code
## Évolution séance finale

### Description de l'évolution réalisée

Prestataire « sous surveillance » : un prestataire est sous surveillance lorsque la moyenne de ses 5 dernières évaluations est strictement inférieure à 3. L'affectation (ou la réaffectation) d'un incident à un tel prestataire exige une confirmation explicite.

Fichiers principaux :

- Backend, domaine
  - `domain/model/ProviderSurveillance.java` (nouveau) : toute la règle. Constantes `WINDOW_SIZE = 5` et `THRESHOLD = 3.0` (PS6), tri par date d'évaluation puis identifiant décroissants (PS1), moyenne absente sous 5 évaluations (PS2), seuil strict (PS3), `ensureAssignable(provider, confirmed)` (PS5).
  - `domain/exception/ProviderUnderSurveillanceException.java` (nouveau) : levée quand l'affectation n'est pas confirmée.
  - `domain/model/Incident.java` : nouveau champ `evaluatedAt`, renseigné par `evaluate(rating, evaluationTime)`.
  - `ProviderSummary`, `ProviderDetails` : exposent la surveillance.
- Backend, application
  - `application/service/ProviderSurveillanceCalculator.java` (nouveau) : lit les incidents évalués par le port de sortie et délègue le calcul à `ProviderSurveillance`. Il est partagé par les cas d'utilisation qui renvoient un détail d'incident.
  - `AssignProviderService` : reçoit un `AssignProviderCommand` (nouveau, dans `domain/port/in`) et vérifie la surveillance avant tout enregistrement.
  - `EvaluateIncidentService` : renseigne la date d'évaluation.
  - `ListProvidersService` : calcule la surveillance de chaque prestataire.
  - Les services qui construisent un `IncidentDetails` (détail, modification, affectation, résolution, évaluation, historique, tableau de bord, fiche prestataire) lui passent la surveillance du prestataire.
- Backend, domaine (suite) : `IncidentDetails` a un nouveau champ `providerUnderSurveillance`, calculé par `IncidentDetails.of(incident, now, surveillance)`.
- Backend, adaptateurs
  - `AssignProviderRequest` : champ `surveillanceConfirmed`.
  - `GlobalExceptionHandler` : 409 avec la propriété `code: "PROVIDER_UNDER_SURVEILLANCE"`.
  - `IncidentRestMapper.toCommand(...)` : construit l'`AssignProviderCommand`, comme `DeclareIncidentCommand`.
  - `ProviderSummaryResponse` et `ProviderDetailsResponse` : `recentAverageRating` et `underSurveillance`.
  - `IncidentDetailsResponse` : `providerUnderSurveillance`.
  - `IncidentEntity` : colonne `evaluated_at`.
  - `incident.sql` : dates d'évaluation, et 3 incidents notés supplémentaires pour que « Polytech Maintenance » soit sous surveillance (5 dernières notes : 3, 2, 3, 2, 3, soit une moyenne de 2,6).
- Front
  - `incident-detail` : mention dans la liste de choix, bloc de confirmation, badge sur le prestataire affecté (lu dans `providerUnderSurveillance` de la réponse).
  - `core/http/api-error.ts` : `hasApiErrorCode(...)` pour reconnaître le 409 de surveillance.
  - `provider.model.ts` : libellé `SURVEILLANCE_LABEL` et `providerOptionLabel`.
  - `provider-list` et `provider-detail` : badge et moyenne récente.

### Choix de conception

- **Forme de la confirmation : un aller-retour piloté par le serveur.** Le premier envoi part avec `surveillanceConfirmed: false`. Si le prestataire est surveillé, le service lève l'exception avant `save`, donc rien n'est enregistré (RG10), et l'API répond 409 avec un message contenant « surveillance » et un `code` dédié. Le front affiche ce message dans un encadré avec les boutons « Annuler » et « Confirmer l'affectation ». Ce dernier renvoie la requête avec `surveillanceConfirmed: true`. Changer de prestataire dans la liste efface l'avertissement. Un appel direct à l'API sans confirmation est donc refusé aussi : la règle ne dépend pas du front.
- **La règle vit dans le modèle** (`ProviderSurveillance`), et les services ne font que l'appeler. Les contrôleurs et les vues se contentent de transmettre et d'afficher (PS7). Le seuil et la fenêtre ne sont définis qu'à un seul endroit ; le front n'affiche jamais « 5 » ni « 3 ».
- **Statut recalculé à chaque lecture** à partir des évaluations, jamais stocké (PS4).
- **Date d'évaluation stockée** (`evaluated_at`) plutôt que d'utiliser `resolvedAt` : les évaluations peuvent être faites dans un autre ordre que les résolutions, et PS1 parle bien de la date d'évaluation.
- **Le statut du prestataire voyage avec l'incident** (`providerUnderSurveillance` dans `IncidentDetails`), comme `attentionLevel` : le front ne fait aucun calcul ni croisement de données.
- **Un service applicatif partagé** (`ProviderSurveillanceCalculator`) évite de répéter, dans chaque cas d'utilisation, la lecture des évaluations d'un prestataire. Pour les listes, `forAllProviders()` charge les évaluations une seule fois.
- **Raccourci pris :** aucun sur la règle elle-même. Le seul compromis concerne les performances : la surveillance est recalculée à chaque requête au lieu d'être mise en cache (voir les limites).

### Tests ajoutés

- `ProviderSurveillanceTest` (domaine, 12 tests) :
  - les 7 exemples de référence de l'énoncé (test paramétré) ;
  - le tri, quel que soit l'ordre des incidents fournis ;
  - le départage par identifiant décroissant à date d'évaluation égale ;
  - les incidents résolus sans note, qui sont ignorés ;
  - le refus sans confirmation pour un prestataire surveillé, avec un message contenant « surveillance » ;
  - l'acceptation après confirmation, et l'acceptation sans confirmation pour un prestataire non surveillé.
- `AssignProviderServiceTest` (service, Mockito, 3 tests) :
  - prestataire surveillé sans confirmation : exception et `save` jamais appelé ;
  - prestataire surveillé avec confirmation : affectation enregistrée ;
  - prestataire non surveillé sans confirmation : affectation enregistrée.

Lancer les tests : `mvn test -Dtest='ProviderSurveillanceTest,AssignProviderServiceTest'`. `ArgosBackendApplicationTests` demande une base MySQL.

### Limites connues

- Les évaluations antérieures à l'évolution n'ont pas de vraie date : le seed fixe `evaluated_at = resolved_at + 2 h`. Sur une base existante, la colonne doit être ajoutée et remplie (ici `ddl-auto: create` recrée le schéma).
- Pas de test d'intégration REST (`@WebMvcTest`) ni de test du front pour le 409 et la confirmation.
- Aucun verrou entre l'avertissement et la confirmation : si une note arrive entre les deux, la confirmation porte sur l'ancien statut. C'est sans conséquence, puisque confirmer ne fait qu'autoriser l'affectation.
- `GET /providers`, l'historique et le tableau de bord chargent tous les incidents résolus pour calculer la surveillance, en plus de leur propre requête. C'est acceptable au volume actuel.
- Le seed utilise l'horloge MySQL (`NOW()`) alors que l'application utilise celle de la JVM.
