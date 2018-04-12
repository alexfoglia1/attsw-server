Feature: Grids listing and deleting

Scenario: List no grids
	Given The server is running
	When The user connects to the server
	Then A welcome page with link to view db and insert table must be shown
