# Use Case: Create Post

## Main Flow
### Preconditions:
* The user is registered
* The user is logged in

### Main Event Flow
1. The user enters the information for the post:
	* title
	* tags
	* image
	* description
	* star rating
	* optionally add post to favorites
2. The user saves post.
3. The system checks whether all requiered fields are filled.
4. The system creates the post.
5. The system shows post on the user’s profile.


## Alternative Event Flow
### AA. Cancel Post
* At step 1, if the user cancels the creation of the post.
1. The system cancels and deletes all information about the post.
2. The system returns to the user's profile window.

### A2. Empty Field
* At step 3, if the user tries to save post when requiered fields are missing.
1. The system shows error message “ required fields missing”.
2. The system asks the user to complete the requiered fields.
3. The system does not save the post.
4. The user stays on the create post window.
