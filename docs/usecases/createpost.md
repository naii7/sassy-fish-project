# Use Case: Create Post

## Actor
Registered User

## Description
This use case describes how a user creates a new post in the application.

---

## Main Event Flow

1. The user logs into the application.
2. The system redirects the user to their profile page.
3. The user clicks the "+" button to create a new post.
4. The system displays the "Create Post" form.
5. The user uploads an image.
6. The user enters a title.
7. The user selects one tag from the available list.
8. The user selects a date (dafault: today's date)
9. The user selects a star rating.
10. The user optionally marks the post as "Favourite".
11. The user optionally writes a personal comment/description
12. The user clicks the "Submit" button.
13. The system saves the post.
14. The system redirects the user back to their profile.
15. The new post is visible on the profile page.

---

## Alternative Event Flows

### A1: Missing Required Information
* At step 4, if the user tries to submit the form without required fields (image, title, tag, rating):
1. The system shows an error message on the required feild
2. The post is not saved.
3. The user remains on the Create Post form.

### 4b: User Cancels Creation
* At step 4, if the user cancels the creation of the post.
1. The user clicks a "Cancel" button.
2. The system discards the entered information.
3. The user is redirected back to their profile.
