Feature: Grid Insertion

Scenario: Insert one grid
	Given The database is empty
	When The user inserts one grid
	Then A list with that grid must be shown
	