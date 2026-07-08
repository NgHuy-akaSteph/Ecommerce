package com.myapp.ecommerce.bootstrap;

import java.util.List;

public record EndpointDef(String name, String apiPath, String method, String module) {
    public static List<EndpointDef> all() {
        return List.of(
                // PRODUCTS
                new EndpointDef("Create a product", "/products", "POST", "PRODUCTS"),
                new EndpointDef("Import product from excel", "/products/excel/import", "POST", "PRODUCTS"),
                new EndpointDef("Update a product", "/products/{id}", "PUT", "PRODUCTS"),
                new EndpointDef("Delete a product", "/products/{id}", "DELETE", "PRODUCTS"),
                new EndpointDef("Get a product by id", "/products/{id}", "GET", "PRODUCTS"),
                new EndpointDef("Get products with pagination", "/products", "GET", "PRODUCTS"),
                new EndpointDef("Delete all product in list", "/products/deleteAll", "DELETE", "PRODUCTS"),
                new EndpointDef("Fetch products by category", "/products/category/{id}", "GET", "PRODUCTS"),

                // CATEGORIES
                new EndpointDef("Create a category", "/categories", "POST", "CATEGORIES"),
                new EndpointDef("Update a category", "/categories/{id}", "PUT", "CATEGORIES"),
                new EndpointDef("Delete a category", "/categories/{id}", "DELETE", "CATEGORIES"),
                new EndpointDef("Get a category by id", "/categories/{id}", "GET", "CATEGORIES"),
                new EndpointDef("Get categories with pagination", "/categories", "GET", "CATEGORIES"),

                // PERMISSIONS
                new EndpointDef("Create a permission", "/permissions", "POST", "PERMISSIONS"),
                new EndpointDef("Update a permission", "/permissions/{id}", "PUT", "PERMISSIONS"),
                new EndpointDef("Delete a permission", "/permissions/{id}", "DELETE", "PERMISSIONS"),
                new EndpointDef("Get a permission by id", "/permissions/{id}", "GET", "PERMISSIONS"),
                new EndpointDef("Get permissions with pagination", "/permissions", "GET", "PERMISSIONS"),

                // TAGS
                new EndpointDef("Create a tag", "/tags", "POST", "TAGS"),
                new EndpointDef("Update a tag", "/tags/{id}", "PUT", "TAGS"),
                new EndpointDef("Delete a tag", "/tags/{id}", "DELETE", "TAGS"),
                new EndpointDef("Get a tag by id", "/tags/{id}", "GET", "TAGS"),
                new EndpointDef("Get tags with pagination", "/tags", "GET", "TAGS"),

                // ROLES
                new EndpointDef("Create a role", "/roles", "POST", "ROLES"),
                new EndpointDef("Update a role", "/roles/{id}", "PUT", "ROLES"),
                new EndpointDef("Delete a role", "/roles/{id}", "DELETE", "ROLES"),
                new EndpointDef("Get a role by id", "/roles/{id}", "GET", "ROLES"),
                new EndpointDef("Get roles with pagination", "/roles", "GET", "ROLES"),

                // USERS
                new EndpointDef("Create a user", "/users", "POST", "USERS"),
                new EndpointDef("Update a user", "/users/{id}", "PUT", "USERS"),
                new EndpointDef("Delete a user", "/users/{id}", "DELETE", "USERS"),
                new EndpointDef("Get a user by id", "/users/{id}", "GET", "USERS"),
                new EndpointDef("Get users with pagination", "/users", "GET", "USERS"),
                new EndpointDef("Get my information", "/users/my-info", "GET", "USERS"),

                // ORDERS
                new EndpointDef("Create a order", "/orders", "POST", "ORDERS"),
                new EndpointDef("Update a order", "/orders/{id}", "PUT", "ORDERS"),
                new EndpointDef("Delete a order", "/orders/{id}", "DELETE", "ORDERS"),
                new EndpointDef("Get a order by id", "/orders/{id}", "GET", "ORDERS"),
                new EndpointDef("Get orders with pagination", "/orders", "GET", "ORDERS"),
                new EndpointDef("Get order history", "/orders/history", "GET", "ORDERS"),
                new EndpointDef("Delete all orders", "/orders/deleteAll", "DELETE", "ORDERS"),

                // ORDER DETAILS
                new EndpointDef("Create a order details", "/orderdetails", "POST", "ORDERDETAILS"),
                new EndpointDef("Get order details with pagination", "/orderdetails", "GET", "ORDERDETAILS"),
                new EndpointDef("Get order details by id", "/orderdetails/{id}", "GET", "ORDERDETAILS"),
                new EndpointDef("Update a order details", "/orderdetails/{id}", "PUT", "ORDERDETAILS"),
                new EndpointDef("Delete a order details", "/orderdetails/{id}", "DELETE", "ORDERDETAILS"),

                // CARTS
                new EndpointDef("Get cart by user", "/carts", "GET", "CARTS"),
                new EndpointDef("Add product to cart", "/carts/add", "POST", "CARTS"),
                new EndpointDef("Change product quantity", "/carts/change", "POST", "CARTS"),
                new EndpointDef("Delete cart detail", "/carts/delete/{id}", "DELETE", "CARTS"),

                // FILES
                new EndpointDef("Upload a file", "/file/upload", "POST", "FILES"),

                // ADDRESSES
                new EndpointDef("Get my addresses", "/addresses", "GET", "ADDRESSES"),
                new EndpointDef("Create an address", "/addresses", "POST", "ADDRESSES"),
                new EndpointDef("Get an address by id", "/addresses/{id}", "GET", "ADDRESSES"),
                new EndpointDef("Update an address", "/addresses/{id}", "PUT", "ADDRESSES"),
                new EndpointDef("Delete an address", "/addresses/{id}", "DELETE", "ADDRESSES"),
                new EndpointDef("Set default address", "/addresses/{id}/default", "PUT", "ADDRESSES"),

                // PRODUCT VARIANTS
                new EndpointDef("Create a product variant", "/product-variants", "POST", "PRODUCT_VARIANTS"),
                new EndpointDef("Get all product variants", "/product-variants", "GET", "PRODUCT_VARIANTS"),
                new EndpointDef("Get variants by product", "/product-variants/product/{productId}", "GET", "PRODUCT_VARIANTS"),
                new EndpointDef("Get a product variant by id", "/product-variants/{id}", "GET", "PRODUCT_VARIANTS"),
                new EndpointDef("Update a product variant", "/product-variants/{id}", "PUT", "PRODUCT_VARIANTS"),
                new EndpointDef("Delete a product variant", "/product-variants/{id}", "DELETE", "PRODUCT_VARIANTS"),

                // VARIANT OPTIONS
                new EndpointDef("Create a variant option", "/variant-options", "POST", "VARIANT_OPTIONS"),
                new EndpointDef("Get all variant options", "/variant-options", "GET", "VARIANT_OPTIONS"),
                new EndpointDef("Get a variant option by id", "/variant-options/{id}", "GET", "VARIANT_OPTIONS"),
                new EndpointDef("Update a variant option", "/variant-options/{id}", "PUT", "VARIANT_OPTIONS"),
                new EndpointDef("Delete a variant option", "/variant-options/{id}", "DELETE", "VARIANT_OPTIONS"),

                // VARIANT VALUES
                new EndpointDef("Create a variant value", "/variant-values", "POST", "VARIANT_VALUES"),
                new EndpointDef("Get all variant values", "/variant-values", "GET", "VARIANT_VALUES"),
                new EndpointDef("Get variant values by option", "/variant-values/option/{optionId}", "GET", "VARIANT_VALUES"),
                new EndpointDef("Get a variant value by id", "/variant-values/{id}", "GET", "VARIANT_VALUES"),
                new EndpointDef("Update a variant value", "/variant-values/{id}", "PUT", "VARIANT_VALUES"),
                new EndpointDef("Delete a variant value", "/variant-values/{id}", "DELETE", "VARIANT_VALUES"),

                // REPORTS
                new EndpointDef("Generate secure download URL", "/reports/download-url", "GET", "REPORTS")
        );
    }
}
