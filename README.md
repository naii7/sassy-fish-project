# Sassy-me 

[![View Presentation](https://img.shields.io/badge/Final_Presentation-Google_Slides-4285F4?style=for-the-badge&logo=google-slides&logoColor=white)](https://docs.google.com/presentation/d/1ia48QtU-yVTR9XPIfAOYYiJ38kBl_Rd66Vx9PCwIufs/preview)

> A social platform where you can rate all type of aspects of your everyday life. From your favorite restaurant's meal down to the public bathroom at the train station. Everything deserves a star.

---

## What is Sassy-me?

**Sassy-me** is a JavaFX desktop social network inspired by Letterboxd, but for your entire life. Users can create posts rating any aspect of their day-to-day experience — food, music, cinema, books, games, concerts, and more. Every post can include images, tags, and a star rating, and can be marked as a personal favorite.

The app has two main spaces: a **feed** showing your best friends' latest posts (with option to like and comment on them), and a **personal profile** displaying your own posts and favorites. The feed can be filtered by tag so you only see what matters to you.

---

## Features

- **Authentication** — register and log in securely 
- **Post creation** — title, image upload, tag selection, star rating, and mark as favorite
- **Profile management** — personal profile with display name, bio, profile picture, and favorites display
- **Edit profile** — update your username, profile picture, and personal info
- **Feed view** — see posts from people you follow, like and comment on them, filter by tags
- **Comment system** — add comments on any post
- **Database integration** — full Hibernate ORM setup with persistent storage
- **JUnit test suite** — business logic and model tests verified with `mvn clean test`

---

## Sprint History

### Sprint 1 — Foundations
_(Feb 11 – Mar 10 )_

Project setup and planning. This sprint covered the initial requirements, UI mockups, use-case diagram, domain model, and main/alternative flows. The product backlog was also defined and the repo structure set up with forks.

---

### Sprint 2 — Core Use Cases
_(Mar 11 – Apr 07)_

First wave of real feature implementation. The profile view was built from scratch with a profile picture, favorites display, and post integration. Post creation got its full UI with star rating, tag support and like button. The comment system was implemented. Business logic and DB integration were managed. Hibernate ORM was introduced and the data layer was fully adapted to it. A JUnit test suite was set up and verified. A sequence diagram for Manage Profile was added to the docs.

---

### Sprint 3 — Polish & Completion
_(Apr 08 – May 04)_

Final sprint focused on finishing touches and consistency. Edit Profile was fully implemented, along with filtering the feed by tags and a home button from the profile view. The Singleton pattern was applied to the architecture. Visual consistency was improved across all views using Scene Builder, and window sizes were refactored. Profile photos, updated username display on posts, and comment fixes were also shipped. The presentation was prepared and the README updated.

---

## Team

| Name | GitHub |
|------|--------|
| Bruno Padilla *(leader)* | [bp-001](https://github.com/bp-001) |
| Asier Ferreiro | [asife77 / fork](https://github.com/asife77/sassy-fish-project.git) |
| Naiara Gutierrez | [naii7 / fork](https://github.com/naii7/sassy-fish-project.git) |
| Aitana Perez | [a-perz / fork](https://github.com/a-perz/sassy-fish-project.git) |

---

*Software Engineering · UPV/EHU · 2025–2026*