# Use Case: Filter Feed by Tags

## Main Flow

### Preconditions
* The user is be registered.
* The user is be authenticated.

### Main Event Flow
1. The system retrieves the posts from the user's feed.
2. The system groups the posts according to their tags.
3. The system displays the feed organized by tags, where each tag appears as a folder conatining the corresponding posts.


## Alternative Event Flow
### A1. Empty Feed
* At step 1, if the user doesn't have any posts on their feed.
1. The system displays the message: "No posts available to filter".
