# WebAnalyzer

## Qu'est-ce que WebAnalyzer ?

WebAnalyzer est un projet qui vise à analyser les pages HTML grâce à leur URL. Il s'agit d'une application web qui tourne sur le port 8080 et développée avec Spring Boot version 3.3.1. Elle s'appuie sur les technologies suivantes :
- Spring Web
- Lombok
- Jsoup (une bibliothèque permettant de faire du web scraping)
- Thymeleaf (moteur de template HTML)
- DevTools

## Comment lancer l'application ?

### Prérequis :

- JDK (version 17)
- Maven ou Spring Boot

### Étapes pour lancer l'application :

1. Placez-vous dans le répertoire racine du projet.
2. Téléchargez et installez les dépendances avec la commande suivante :
    - Si vous possédez Maven :
      ```bash
      mvn clean install
      ```
    - Si vous avez Spring Boot via STS ou une extension sur votre IDE :
      ```bash
      ./mvnw clean install
      ```
3. Une fois toutes les dépendances installées, vous pouvez lancer l'application de la manière suivante :
    - Si vous possédez Maven :
      ```bash
      mvn spring-boot:run
      ```
    - Si vous avez Spring Boot via STS ou une extension sur votre IDE :
      ```bash
      ./mvnw spring-boot:run
      ```
4. Une version déjà construite du projet existe dans le dossier `target`. Vous pouvez la lancer avec la commande suivante :
   ```bash
   java -jar target/webAnalyzer-0.0.1-SNAPSHOT.jar

5. Une fois l'application lancée, rendez-vous sur votre navigateur et tapez "http://localhost:8080". Vous serez redirigé vers l'application.

## Comment est conçu WebAnalyzer ?

WebAnalyzer repose essentiellement sur la librairie Jsoup. Une fois l'URL de la page à analyser récupérée, Jsoup est utilisé pour se connecter à cette page et récupérer le fichier HTML de celle-ci.
Une fois le fichier récupéré, Jsoup fournit un ensemble de classes que nous utilisons pour extraire les balises HTML qui nous intéressent, par exemple la balise `<title>` pour le titre de la page ou `<a>` pour les liens.
Pour la détection du formulaire d'authentification, l'application se contente de rechercher dans le document HTML un formulaire avec un champ input de type password.
Pour ce qui est de la vérification des liens et de l'accessibilité des ressources derrière les liens collectés depuis la page,
ce traitement est réalisé de manière asynchrone pour permettre le chargement de la page une fois l'extraction des informations terminée, tandis que la vérification des liens, qui est un processus plus long, se poursuit.
Pour rendre ce traitement plus optimal, au lieu d'effectuer la vérification des liens en série (c'est-à-dire l'un après l'autre), WebAnalyzer utilise le parallélisme pour gagner du temps.
La liste de liens à vérifier est découpée en plusieurs lots et pour chaque lot, un thread est instancié pour effectuer la vérification de ce lot de liens.

## Limites connues de WebAnalyzer

- La détection de formulaire d'authentification n'est pas des plus optimales et va souvent manquer les formulaires obtenus en cliquant sur un bouton de login qui redirige vers une page de connexion.
- L'affichage à l'écran des erreurs survenues est très limité. Il s'agit juste des messages d'exceptions qui sont remontés.
- Il arrive parfois que parmi les liens à vérifier, il y ait des liens conduisant vers certains types de ressources que Jsoup ne prend pas en charge, comme les PDF, ou simplement les liens vers des boîtes mails. L'application les considérera comme inaccessibles.
- L'application n'a pas été faite de manière responsive.
- Si sur une page analysée un même lien revient plusieurs fois (comme les liens vers la page d'accueil qui se retrouvent souvent à plusieurs endroits sur la page), l'application comptera ce lien plusieurs fois et effectuera sa vérification plusieurs fois.
- Le contrôle de formulaire n'a pas été fait, il n'y a aucun filtre de formulaire.
- Si vous ouvrez l'application sur plusieurs fenêtres différentes, toutes les fenêtres travailleront sur les mêmes données et pourront corrompre le traitement des autres.

---
`Ce README` fournit une description claire du projet, les technologies utilisées, des instructions précises pour lancer l'application, des détails sur la conception et le fonctionnement de WebAnalyzer, ainsi que les limites connues de l'application.