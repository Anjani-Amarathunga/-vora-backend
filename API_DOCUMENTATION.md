# Évora Backend - Complete API Documentation

This document provides comprehensive API documentation for frontend integration.

## Table of Contents
1. [Setup](#setup)
2. [Authentication](#authentication)
3. [User Profile Management](#user-profile-management)
4. [Product APIs](#product-apis)
5. [Cart Management](#cart-management)
6. [Order Management](#order-management)
7. [Reviews & Ratings](#reviews--ratings)
8. [Error Handling](#error-handling)
9. [Frontend Integration Examples](#frontend-integration-examples)

---

## Setup

### Backend URL
- **Development:** `http://localhost:8080`
- **Production:** Update based on your deployment

### Environment Variables (Frontend .env)
```
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_JWT_TOKEN_KEY=evora_token
```

### CORS Allowed Origins
- `http://localhost:3000`
- `http://localhost:3001`

To add production domain, update `SecurityConfig.java` → `corsConfigurationSource()` method.

---

## Authentication

### Login
**Endpoint:** `POST /api/auth/login`  
**Auth Required:** No  
**Rate Limit:** None (implement on frontend if needed)

**Request:**
```json
{
  "email": "user@vora.com",
  "password": "user123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@vora.com",
  "roles": ["CUSTOMER"]
}
```

**Error Responses:**
```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password"
}
```

**Implementation Notes:**
- Store token in localStorage/sessionStorage
- Token expires after 24 hours (86400000 ms)
- Include token in all protected requests: `Authorization: Bearer {token}`

---

## User Profile Management

### Get Current User
**Endpoint:** `GET /api/user/me`  
**Auth Required:** Yes (Bearer Token)

**Response (200 OK):**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "user@vora.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "postalCode": "62701",
  "country": "USA",
  "roles": ["CUSTOMER"]
}
```

### Update User Profile
**Endpoint:** `PUT /api/user/me`  
**Auth Required:** Yes (Bearer Token)  
**Method:** HTTP PUT

**Request:** (All fields optional)
```json
{
  "fullName": "Jane Doe",
  "phoneNumber": "9876543210",
  "address": "456 Oak Ave",
  "city": "Springfield",
  "state": "IL",
  "postalCode": "62702",
  "country": "USA"
}
```

**Response (200 OK):**
```json
{
  "message": "Profile updated"
}
```

---

## Product APIs

### List All Products
**Endpoint:** `GET /api/products`  
**Auth Required:** No

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop for professionals",
    "price": 1299.99,
    "stock": 50
  },
  {
    "id": 2,
    "name": "Mouse",
    "description": "Wireless mouse",
    "price": 29.99,
    "stock": 200
  }
]
```

### Search Products by Name
**Endpoint:** `GET /api/products?q=laptop`  
**Auth Required:** No  
**Query Params:**
- `q` (optional): Search term (case-insensitive substring match)

**Response:** Same as List All Products

**Example:**
```bash
GET /api/products?q=laptop
GET /api/products?q=mouse
```

### Filter Products by Price
**Endpoint:** `GET /api/products?minPrice=100&maxPrice=500`  
**Auth Required:** No  
**Query Params:**
- `minPrice` (optional): Minimum price
- `maxPrice` (optional): Maximum price

**Response:** Filtered product list

### Get Product Details
**Endpoint:** `GET /api/products/{id}`  
**Auth Required:** No  
**Path Params:**
- `id`: Product ID (required)

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop for professionals",
  "price": 1299.99,
  "stock": 50
}
```

**Error (404 Not Found):**
```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

### Create Product (Admin Only)
**Endpoint:** `POST /api/products`  
**Auth Required:** Yes (Admin role)

**Request:**
```json
{
  "name": "New Product",
  "description": "Product description",
  "price": 99.99,
  "stock": 100
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "name": "New Product",
  "description": "Product description",
  "price": 99.99,
  "stock": 100
}
```

---

## Cart Management

### Get User's Cart
**Endpoint:** `GET /api/cart`  
**Auth Required:** Yes (Bearer Token)

**Response (200 OK):**
```json
{
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop",
      "productPrice": 1299.99,
      "quantity": 1,
      "priceAtAddTime": 1299.99,
      "subtotal": 1299.99,
      "addedAt": "2026-03-05T10:00:00",
      "updatedAt": "2026-03-05T10:00:00"
    },
    {
      "id": 2,
      "productId": 2,
      "productName": "Mouse",
      "productPrice": 29.99,
      "quantity": 2,
      "priceAtAddTime": 29.99,
      "subtotal": 59.98,
      "addedAt": "2026-03-05T10:05:00",
      "updatedAt": "2026-03-05T10:05:00"
    }
  ],
  "totalItems": 3,
  "totalPrice": 1359.97
}
```

**Empty Cart Response:**
```json
{
  "items": [],
  "totalItems": 0,
  "totalPrice": 0
}
```

### Add Item to Cart
**Endpoint:** `POST /api/cart/add`  
**Auth Required:** Yes (Bearer Token)

**Request:**
```json
{
  "productId": 1,
  "quantity": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": 1,
  "productName": "Laptop",
  "productPrice": 1299.99,
  "quantity": 1,
  "priceAtAddTime": 1299.99,
  "subtotal": 1299.99,
  "addedAt": "2026-03-05T10:00:00",
  "updatedAt": "2026-03-05T10:00:00"
}
```

**Validation Errors (400 Bad Request):**
```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Quantity must be at least 1"
}
```

**Stock Insufficient (400 Bad Request):**
```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock for product: Laptop"
}
```

### Update Cart Item Quantity
**Endpoint:** `PUT /api/cart/{cartItemId}`  
**Auth Required:** Yes (Bearer Token)

**Request:**
```json
{
  "quantity": 5
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": 1,
  "productName": "Laptop",
  "productPrice": 1299.99,
  "quantity": 5,
  "priceAtAddTime": 1299.99,
  "subtotal": 6499.95,
  "addedAt": "2026-03-05T10:00:00",
  "updatedAt": "2026-03-05T10:30:00"
}
```

### Remove Item from Cart
**Endpoint:** `DELETE /api/cart/{cartItemId}`  
**Auth Required:** Yes (Bearer Token)

**Response (204 No Content)**

### Clear Entire Cart
**Endpoint:** `DELETE /api/cart/clear/all`  
**Auth Required:** Yes (Bearer Token)

**Response (204 No Content)**

---

## Order Management

### Place Order
**Endpoint:** `POST /api/orders/place`  
**Auth Required:** Yes (Bearer Token)

**Request:**
```json
{
  "shippingAddress": "123 Main St, Springfield, IL 62701",
  "phoneNumber": "1234567890"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "orderNumber": "ORD-ABC12345",
  "status": "PENDING",
  "totalAmount": 1359.97,
  "shippingAddress": "123 Main St, Springfield, IL 62701",
  "phoneNumber": "1234567890",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop",
      "quantity": 1,
      "pricePerUnit": 1299.99,
      "subtotal": 1299.99
    },
    {
      "id": 2,
      "productId": 2,
      "productName": "Mouse",
      "quantity": 2,
      "pricePerUnit": 29.99,
      "subtotal": 59.98
    }
  ],
  "createdAt": "2026-03-05T10:30:00",
  "updatedAt": "2026-03-05T10:30:00",
  "deliveredAt": null
}
```

**Cart Validation Errors (400 Bad Request):**
```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cart is empty"
}
```

### Get Order History
**Endpoint:** `GET /api/orders`  
**Auth Required:** Yes (Bearer Token)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderNumber": "ORD-ABC12345",
    "status": "SHIPPED",
    "totalAmount": 1359.97,
    "shippingAddress": "123 Main St, Springfield, IL 62701",
    "phoneNumber": "1234567890",
    "items": [...],
    "createdAt": "2026-03-05T10:30:00",
    "updatedAt": "2026-03-05T11:00:00",
    "deliveredAt": null
  }
]
```

**Empty History (200 OK):**
```json
[]
```

### Get Order Details
**Endpoint:** `GET /api/orders/{orderId}`  
**Auth Required:** Yes (Bearer Token)

**Response (200 OK):**
Same format as Order object above

**Not Found (404):**
```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

**Order Status Values:**
- `PENDING` - Order placed, awaiting confirmation
- `CONFIRMED` - Order confirmed by seller
- `PROCESSING` - Order being prepared
- `SHIPPED` - Order shipped to customer
- `DELIVERED` - Order delivered to customer
- `CANCELLED` - Order cancelled

---

## Reviews & Ratings

### Add/Update Review
**Endpoint:** `POST /api/reviews`  
**Auth Required:** Yes (Bearer Token)

**Request:**
```json
{
  "productId": 1,
  "rating": 5,
  "comment": "Excellent laptop! Very satisfied with the purchase."
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "productId": 1,
  "userId": 1,
  "userName": "John Doe",
  "rating": 5,
  "comment": "Excellent laptop! Very satisfied with the purchase.",
  "createdAt": "2026-03-05T10:30:00",
  "updatedAt": "2026-03-05T10:30:00"
}
```

**Validation Errors (400 Bad Request):**
```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Rating must be between 1 and 5"
}
```

### Get Product Reviews
**Endpoint:** `GET /api/reviews/product/{productId}`  
**Auth Required:** No

**Response (200 OK):**
```json
{
  "productId": 1,
  "averageRating": 4.5,
  "totalReviews": 10,
  "reviews": [
    {
      "id": 1,
      "productId": 1,
      "userId": 1,
      "userName": "John Doe",
      "rating": 5,
      "comment": "Excellent laptop!",
      "createdAt": "2026-03-05T10:30:00",
      "updatedAt": "2026-03-05T10:30:00"
    }
  ]
}
```

**No Reviews (200 OK):**
```json
{
  "productId": 1,
  "averageRating": 0.0,
  "totalReviews": 0,
  "reviews": []
}
```

### Get User's Reviews
**Endpoint:** `GET /api/reviews/user`  
**Auth Required:** Yes (Bearer Token)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "productId": 1,
    "userId": 1,
    "userName": "John Doe",
    "rating": 5,
    "comment": "Excellent laptop!",
    "createdAt": "2026-03-05T10:30:00",
    "updatedAt": "2026-03-05T10:30:00"
  }
]
```

### Delete Review
**Endpoint:** `DELETE /api/reviews/{reviewId}`  
**Auth Required:** Yes (Bearer Token)

**Response (204 No Content)**

---

## Error Handling

All errors follow a consistent format:

```json
{
  "timestamp": "2026-03-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message explaining what went wrong"
}
```

### HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | Success | GET request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | DELETE successful |
| 400 | Bad Request | Validation error, missing fields |
| 401 | Unauthorized | Missing/invalid JWT token |
| 403 | Forbidden | User lacks required permissions |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Unexpected server error |

### Common Validation Error Messages

- `"Product ID is required"`
- `"Quantity must be at least 1"`
- `"Shipping address is required"`
- `"Phone number must be at least 10 digits"`
- `"Rating must be between 1 and 5"`
- `"Comment cannot be empty"`

---

## Frontend Integration Examples

### Using Fetch API

```javascript
const API_BASE = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

// Get token from storage
const getToken = () => localStorage.getItem('evora_token');

// Make authenticated request
const apiCall = async (endpoint, options = {}) => {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'API Error');
  }

  return response.json();
};

// Login
const login = async (email, password) => {
  const data = await apiCall('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
  localStorage.setItem('evora_token', data.token);
  return data;
};

// Get Cart
const getCart = () => apiCall('/api/cart');

// Add to Cart
const addToCart = (productId, quantity) =>
  apiCall('/api/cart/add', {
    method: 'POST',
    body: JSON.stringify({ productId, quantity }),
  });

// Place Order
const placeOrder = (shippingAddress, phoneNumber) =>
  apiCall('/api/orders/place', {
    method: 'POST',
    body: JSON.stringify({ shippingAddress, phoneNumber }),
  });

// Add Review
const addReview = (productId, rating, comment) =>
  apiCall('/api/reviews', {
    method: 'POST',
    body: JSON.stringify({ productId, rating, comment }),
  });
```

### Using Axios

```javascript
import axios from 'axios';

const API = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
});

// Add token to all requests
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('evora_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Error handling
API.interceptors.response.use(
  (res) => res.data,
  (error) => {
    console.error('API Error:', error.response?.data?.message);
    throw error;
  }
);

// Usage
await API.post('/api/auth/login', { email, password });
await API.get('/api/cart');
await API.post('/api/orders/place', { shippingAddress, phoneNumber });
```

---

## Testing API Endpoints

### Using cURL

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@vora.com","password":"user123"}'

# Get Cart (with token)
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Add to Cart
curl -X POST http://localhost:8080/api/cart/add \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'

# Get Products
curl -X GET http://localhost:8080/api/products

# Search Products
curl -X GET "http://localhost:8080/api/products?q=laptop"

# Get Product Reviews
curl -X GET http://localhost:8080/api/reviews/product/1
```

---

## Important Notes

1. **Token Expiration:** Tokens expire after 24 hours. Implement logout and re-login.
2. **Stock Validation:** Cart and order operations validate stock availability.
3. **User Authorization:** Users can only access their own cart, orders, and reviews.
4. **Price Snapshot:** Cart and orders store prices at the time of action (protects against price changes).
5. **Phone Validation:** Phone numbers must be at least 10 digits.
6. **Rating Range:** Reviews require ratings between 1-5.

---

## Support

For API issues, check the error messages returned and verify:
- Correct endpoint URL
- Valid JWT token (not expired)
- Properly formatted request body
- Required fields are provided
- User has sufficient permissions (for admin endpoints)
