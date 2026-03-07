# Use Case: Mark as Favourite

## Main Flow

### Preconditions:
* The user is registered and authenticated.
* The post is created and uploaded to the system.
* The post is not currently marked as a Favourite.
* The user has less than 4 posts marked as Favourite.

### Main Event Flow:
1. The user navigates to a specific post from their own.
2. The user clicks the Heart icon located on the post UI.
3. The system checks if the user has an available "slot" for favourites (maximum 4).
4. The system adds the post to the "Favourite Posts" section of the user's profile.
5. The Heart icon changes its visual state (filled with color) to confirm the action.


## Alternative Event Flow:

### A1. Favourite Limit Reached (4/4 slots full)
* At step 3,if the user already has 4 favourites.
1. The system shows an error message: "Maximum limit of 4 favorites reached. Please un-favourite another post first"
2. The system does not add the new favorite.

### A2.Remove a Post from Favourites (Unmark)
* At step 2, if the post is already marked as Favourite.
1. The user clicks the Heart icon (which is currently color-filled).
2. The system removes the post from the "Top 4" section of the profile.
3. The system frees up one of the 4 slots.
4. The Heart icon returns to its default "empty" state.