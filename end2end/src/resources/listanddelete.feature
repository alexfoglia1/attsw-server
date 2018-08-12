Feature: Grids listing and deleting

Scenario: List no grids
	Given The database is empty
	When The user is on Home Page
	Then A message "No Grids" must be shown
	
Scenario: List some grids
	Given The database is not empty
	When The user is on Home Page
	Then A list of grids must be shown with a deletion link
	
Scenario: Delete a grid
	Given The database is not empty
	When The user deletes one grid
	Then A list of grids must be shown where there is not the deleted grid
