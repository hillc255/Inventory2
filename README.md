# Inventory2
Android Studio project: Create an Inventory app using an SQLite Database - Grow with Google - Udacity Nanodegree Scholarship 2018

## Synopsis

Name:  Inventory

This is an Inventory application using an SQLite Database to store the list of items.  

There are screens to Add Items and Edit Inventory.  Dummy data can be added to speed the process of adding items.

The database has the following fields:  _ID, Product Name, Price, Quantity, Supplier Name, Supplier Phone Numer


</br>
<p align="center">
 <kbd><img width="300" height="533" src="readme_assets/inventoryvideo.gif"></kbd>
</p>

## Code Description

After the initial splash screen, which has an image of the db table columns, user can either add a new item with the floating button or insert dummy data.  There are two Activites:  CatalogActivity and EditoryActivity along with an InventoryCursorAdapter.

The CatalogActivity is for the main splash screen.  The EditorActivity is for editing the new and existing products in the inventory.  The InventoryCursorAdapter is used to pull the list together from the database.  

A delete quantity by 1 button on the list screen and add/delete buttons on the edit screen permit the user to adjust the quantity of items in the inventory. An item can be deleted from the inventory in the edit screen.

Below are 4 screen shots with the splash screen, insertion of dummy data, editing a product and adding a product.

![Splash screen with db columns](https://github.com/hillc255/Inventory2/blob/master/readme_assets/dbscreen.png) |
![Dummy data inserted.](https://github.com/hillc255/Inventory2/blob/master/readme_assets/dummydata.png) |
![Edit a product.](https://github.com/hillc255/Inventory2/blob/master/readme_assets/editproduct.png) |
![Add a product.](https://github.com/hillc255/Inventory2/blob/master/readme_assets/addproduct.png)

## Motivation

Application was assigned as the final project for the "Grow with Google - Udacity Nanodegree Android Basics" program.

## Installation

Project can be downloaded from GitHub.  
https://github.com/hillc255/Inventory2

## API Reference

## Tests (Future consideration!)

Describe and show how to run the tests with code examples.

## Contributors

Claudia Hill designed and developed this project.

## License

Only to be used for educational purposes
