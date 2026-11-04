# Journal — Aurora (ALTN72, 2026-27)

**Groupe :** Arthur NOM, Ethan NOM

---

## README

- **Prérequis :** Java 17+, Maven 3.8+, PostgreSQL 14+ `[À COMPLÉTER : vérifier les versions]`
- **Initialiser la base :** créer une base locale `aurora_db`, puis exécuter `db/create.sql` `[À COMPLÉTER : nom de la base, données de test incluses ?]`
- **Lancer :**
  ```bash
  mvn spring-boot:run
  ```

  L'application est accessible sur http://localhost:8080.
- **Application déployée :** https://github.com/Avixa/Aurora
- **Comptes de test :**
  - Responsable d'exploitation : `responsable` / `Responsabel#2026`
  - Agent : `agent` / `Agent#2026`
- **Limites connues :**
  - Authentification développée nous même avec http session (sans Spring Security), donc moins complète et moins éprouvée qu'une solution standard (pas de protection CSRF native, par exemple).

---

## ARCHITECTURE

- **Dépôt :** https://github.com/Avixa/Aurora
- **Schéma :**

Aurora est une application web Spring Boot en architecture en couches, qui répertorie les incidents survenant sur les équipements des gares et les attribue à des prestataires. Les entités principales sont les **stations**, les **équipements**, les **incidents** et les **prestataires**.

- **Controllers (Spring MVC)** : reçoivent les requêtes du navigateur, remplissent le `Model` et délèguent au Server Side Rendering (Thymeleaf) pour produire les pages HTML.
- **API (`@RestController`)** : expose des endpoints JSON consommés côté navigateur, par exemple pour alimenter la carte Leaflet.
- **Services** : portent les règles et la logique métier. C'est la seule couche que les contrôleurs et l'API appellent pour accéder aux données.
- **Persistence (JPA / JPQL)** : repositories Spring Data JPA, qui communiquent avec **PostgreSQL** via JDBC.

---

## TESTS

- **Stratégie :** tests unitaires (JUnit 5 + Mockito) uniquement sur la **couche Services**. C'est là que se concentre la logique métier d'Aurora (règles d'affectation et de réaffectation d'un prestataire, déclaration et résolution d'un incident, évaluation d'un prestataire, etc.). Les repositories sont mockés pour tester chaque règle de façon isolée, rapide et déterministe.
- **Commande :**
  ```bash
  mvn verify
  ```
- **Couvert :** les services métier (règles de gestion, cas nominaux et cas d'erreur).
- **Non couvert :**
  - **Repositories** : ils reposent sur Spring Data JPA, déjà testé par le framework, et ne contiennent quasiment pas de logique propre.
  - **Controllers et vues Thymeleaf** : ils se limitent au routage et au binding du modèle, la logique étant déléguée aux services. Ils ont été vérifiés manuellement.
  - **Authentification maison** : elle n'est pas couverte automatiquement et a été validée manuellement. C'est la principale zone de risque, car nous n'avons pas pu nous appuyer sur un composant déjà testé comme Spring Security.

---

## CHOIX TECHNIQUES

### Droits par profil


| N°    | Fonctionnalité                        | Responsable d'exploitation | Agent          |
| ------ | -------------------------------------- | -------------------------- | -------------- |
| F2–F3 | Consulter équipements et fiches       | Lecture seule              | Lecture seule  |
| F4     | Déclarer un incident                  | Interdit                   | Accès complet |
| F5     | Consulter un incident                  | Accès complet             | Accès complet |
| F6     | Modifier un incident                   | Interdit                   | Accès complet |
| F7     | Affecter ou réaffecter un prestataire | Accès complet             | Accès complet |
| F8     | Déclarer un incident résolu          | Interdit                   | Accès complet |
| F9     | Consulter l'historique                 | Lecture seule              | Lecture seule  |
| F10    | Évaluer un prestataire                | Lecture seule              | Accès complet |
| F11    | Tableau de bord                        | Lecture seule              | Lecture seule  |

### Exceptions : `@ControllerAdvice` ou `ResponseStatusException` ?

**Choix retenu :** `ResponseStatusException` combinée à des **exceptions métier personnalisées** (par exemple `IncidentNotFoundException`, `PrestataireIndisponibleException`, `ActionInterditeException`). `[À VÉRIFIER : noms réels, et si elles étendent ResponseStatusException, sont annotées @ResponseStatus, ou sont converties dans les controllers]`

**Fonctionnement :**
- Les **services** lèvent des exceptions custom qui expriment une règle métier violée (ressource introuvable, action interdite pour le profil, affectation impossible...). Cela garde les services indépendants de HTTP.
- Les exceptions sont traduites en réponse HTTP avec le bon code (404, 403, 400...) via `ResponseStatusException`.

**Pourquoi :**
- C'est simple à mettre en place : pas de classe de gestion globale à maintenir, et Spring produit automatiquement la réponse HTTP avec le bon statut.
- Les exceptions custom donnent des messages d'erreur explicites et distinguent clairement les cas métier des erreurs techniques.
- Petit projet donc c'est ok

**Limites (analyse critique) :**
- Le code HTTP est lié à l'endroit où l'exception est levée ou convertie, donc la gestion des erreurs est moins centralisée qu'avec `@ControllerAdvice`.
- Les pages d'erreur restent celles par défaut de Spring : pas de vue Thymeleaf personnalisée selon le type d'erreur.

### SOLID

**Principes appliqués :**
- **SRP (Single Responsibility)** : chaque couche a une seule responsabilité. Les controllers gèrent le routage et le binding du modèle, les services (`@Service`) portent la logique métier et les transactions, les repositories gèrent l'accès aux données. C'est ce qui nous a permis de concentrer les tests sur la couche Services (cf. TESTS).
- **Injection de dépendances par constructeur** (inversion de contrôle) : les controllers déclarent leurs dépendances en champs `private final` et les reçoivent par constructeur (ex : `StationController(StationService stationService)`). Spring fournit l'instance, ce qui évite les `new` dans le code, rend les dépendances explicites et immuables, et permet de les remplacer par des mocks dans les tests (Mockito).

**Principes écartés ou adaptés :**
- **DIP (Dependency Inversion), appliqué partiellement** : les controllers dépendent de **classes concrètes** (`StationService`) et non d'interfaces. Nous avons préféré ne pas créer une interface par service, car il n'existe qu'une seule implémentation de chacun et cela aurait doublé le nombre de fichiers sans bénéfice réel à cette échelle. En contrepartie, le couplage est plus fort : changer d'implémentation obligerait à modifier les controllers.
- **ISP (Interface Segregation)** : nous utilisons directement `JpaRepository`, qui expose beaucoup de méthodes.
- **OCP (Open/Closed)** : appliqué seulement ponctuellement. Les règles métier (par exemple les permissions par rôle) sont écrites de façon simple plutôt qu'avec un système de stratégies extensibles, jugé disproportionné pour deux profils.

### Requête SQL native

- **Où :** dans le repository des événements d'incident (`findJournal`), seule requête native du projet :
  ```java
  @Query(value = """
      SELECT id, incident_id, auteur_id, date_evenement, type_evenement, detail
      FROM evenement_incident
      WHERE incident_id = :incidentId
      ORDER BY date_evenement, id
      """, nativeQuery = true)
  List<IncidentEvent> findJournal(@Param("incidentId") Long incidentId);
  ```
- **À quoi elle sert :** elle retourne le journal complet d'un incident (création, affectation ou réaffectation d'un prestataire, modification, résolution...) dans l'ordre chronologique, pour la consultation de l'historique (F9).
- **Analyse critique :**
  - **Points positifs :** le SQL est lisible et on maîtrise exactement les colonnes lues et le tri. Le tri secondaire sur `id` rend l'ordre déterministe quand deux événements ont la même `date_evenement`. Le paramètre nommé `:incidentId` est lié par JDBC, ce qui évite toute injection SQL.
  - **Limites :** cette requête ne justifie pas vraiment le SQL natif. C'est un simple `SELECT ... WHERE ... ORDER BY` sur une seule table, qu'une méthode dérivée Spring Data (`findByIncidentIdOrderByDateEvenementAscIdAsc`) ou une requête JPQL exprimerait aussi bien, avec une vérification à la compilation. Le natif apporte surtout la maîtrise explicite du SQL, et nous l'avons choisi pour illustrer son usage.
  - **Fragilité :** la requête n'est pas validée par rapport au modèle d'entités. Un renommage de colonne ou d'attribut ne provoque une erreur qu'à l'exécution, et les colonnes sélectionnées doivent correspondre exactement au mapping de `IncidentEvent`.

### `@Transactional`

- **Où :** sur la couche **services**, avec une stratégie en deux niveaux. La classe est annotée `@Transactional(readOnly = true)` par défaut, et chaque méthode qui écrit en base (création, mise à jour, suppression) redéclare `@Transactional` pour passer en lecture-écriture. Exemple dans `EvaluationService` :
  ```java
  @Service
  @Transactional(readOnly = true)
  public class EvaluationService {
      // lecture : getEvaluation, isEvaluable, getEvaluableIncident

      @Transactional
      public Evaluation evaluate(Long incidentId, Evaluation evaluation) { ... }
  }
  ```
- **Pourquoi :**
  - **Sûr par défaut :** une nouvelle méthode de lecture est automatiquement en lecture seule, et seules les méthodes d'écriture, explicitement annotées, peuvent modifier la base. Hibernate évite alors le dirty checking inutile sur les lectures.
  - **Atomicité de la vérification et de l'écriture :** `evaluate` lit l'incident, vérifie qu'il est évaluable (résolu, affecté à un prestataire, pas encore évalué) puis enregistre l'évaluation. Ces étapes s'exécutent dans une seule transaction : si l'une échoue, rien n'est persisté.
  - **Rollback automatique :** `EvaluationNotAllowedException` étend `RuntimeException`, donc Spring annule la transaction sans code supplémentaire.
  - **Transaction partagée entre services :** `evaluate` appelle `IncidentService`, dont les méthodes rejoignent la transaction en cours (propagation `REQUIRED` par défaut), ce qui donne un seul commit pour toute l'opération.


## BILAN

- **Contribution de chacun :**
  - **Arthur :** tableau de bord (F11) et gestion des permissions de chaque rôle et autres.
  - **Ethan :** authentification (sans Spring Security), entités, services et controllers associés, pages Thymeleaf de base (hors authentification), et carte d'affichage des stations avec Leaflet (+ CSS).

- **Trois points à retenir du cours et du projet :**
  1. **Ne pas réinventer ce que le framework fournit** : l'authentification maison nous a coûté du temps pour un résultat moins fiable que Spring Security.
  2. **Une architecture en couches stricte** (Controller → Service → Repository) rend l'application lisible et permet de concentrer les tests sur la logique métier.
  3. **`@Transactional` et les tests de services** garantissent la cohérence des données : c'est là que se jouent les règles métier d'Aurora.
