# Use Case: Manage Profile

### Preconditions:
* The user is logegd into the application
* THe user has an existing account

## Main Flow

1. THe user accesses the profile section of the application
2. The system displays the user's current profile information
3. The suer selects the edit profile option
4. The user modifies the desired information (e.g., name, profile picture, bio, country)
5. The user click the save button
6. The system validates the information
7. The system updates the profile information
8. The system confirms that the profile has been successfully updated

## Alternative Flows

### A1. Invalid informaiton
* At step 6, if the informaiton entered is invalid
1. The system displays an error message (e.g., "Invalid information provided")
2. The user corrects the information
3. The flow returns to step 5

### A2. User cancels the operation 
* At step 4 or 5, if the user cancels the modification
1. The user clicks cancel
2. The system discads the changes
3. The system returns to the profile view page

### A3. Uploading profile picture fails
* At step 4, if the image upload fails
1. The system shows an error message "Profile picture upload failed"
2. The user can try uploading another image