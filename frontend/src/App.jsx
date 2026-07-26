import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8080';

export default function App() {
  // Auth State
  const [token, setToken] = useState(localStorage.getItem('token') || '');
  const [user, setUser] = useState(JSON.parse(localStorage.getItem('user')) || null);
  const [isLoginView, setIsLoginView] = useState(true);
  const [usernameInput, setUsernameInput] = useState('');
  const [passwordInput, setPasswordInput] = useState('');
  const [roleInput, setRoleInput] = useState('ROLE_USER');
  const [authError, setAuthError] = useState('');
  const [authSuccess, setAuthSuccess] = useState('');

  // Catalog State
  const [products, setProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [loadingProducts, setLoadingProducts] = useState(false);
  const [productError, setProductError] = useState('');

  // Cart State
  const [cart, setCart] = useState([]);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isCheckingOut, setIsCheckingOut] = useState(false);
  const [checkoutError, setCheckoutError] = useState('');
  const [lastOrder, setLastOrder] = useState(null); // For order success modal
  const [shippingAddress, setShippingAddress] = useState('');

  // Admin State
  const [showAdminPanel, setShowAdminPanel] = useState(false);
  const [newProductName, setNewProductName] = useState('');
  const [newProductDesc, setNewProductDesc] = useState('');
  const [newProductPrice, setNewProductPrice] = useState('');
  const [newProductStock, setNewProductStock] = useState('');
  const [newProductCategory, setNewProductCategory] = useState('Electronics');
  const [newProductImg, setNewProductImg] = useState('');
  const [adminError, setAdminError] = useState('');
  const [adminSuccess, setAdminSuccess] = useState('');

  // Fetch products
  const fetchProducts = async (category = '') => {
    setLoadingProducts(true);
    setProductError('');
    try {
      let url = `${API_BASE}/api/v1/products`;
      if (category && category !== 'All') {
        url += `?category=${encodeURIComponent(category)}`;
      }
      const res = await fetch(url);
      if (!res.ok) throw new Error(`Error loading products: ${res.statusText}`);
      const data = await res.json();
      setProducts(data);
    } catch (err) {
      console.error(err);
      setProductError('Could not load products. Please check if gateway and product services are running.');
    } finally {
      setLoadingProducts(false);
    }
  };

  useEffect(() => {
    fetchProducts(selectedCategory);
  }, [selectedCategory]);

  // Auth: Register/Login
  const handleAuthSubmit = async (e) => {
    e.preventDefault();
    setAuthError('');
    setAuthSuccess('');

    if (!usernameInput || !passwordInput) {
      setAuthError('Please fill in all fields');
      return;
    }

    try {
      if (isLoginView) {
        // Login
        const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: usernameInput, password: passwordInput })
        });
        
        if (!res.ok) {
          const errMsg = await res.text();
          throw new Error(errMsg || 'Login failed. Please check credentials.');
        }

        const data = await res.json();
        setToken(data.token);
        setUser({ id: data.userId, username: data.username, role: data.role });
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify({ id: data.userId, username: data.username, role: data.role }));
        
        // Reset fields
        setUsernameInput('');
        setPasswordInput('');
      } else {
        // Register
        const res = await fetch(`${API_BASE}/api/v1/auth/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: usernameInput,
            password: passwordInput,
            role: roleInput
          })
        });

        if (!res.ok) {
          const errMsg = await res.text();
          throw new Error(errMsg || 'Registration failed.');
        }

        setAuthSuccess('Registration successful! Please login.');
        setIsLoginView(true);
        setPasswordInput('');
      }
    } catch (err) {
      setAuthError(err.message);
    }
  };

  const handleLogout = () => {
    setToken('');
    setUser(null);
    setCart([]);
    setShowAdminPanel(false);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  // Cart operations
  const addToCart = (product) => {
    setCart(prevCart => {
      const existing = prevCart.find(item => item.id === product.id);
      if (existing) {
        if (existing.quantity >= (product.stockQuantity || 0)) {
          alert(`Only ${product.stockQuantity} items in stock.`);
          return prevCart;
        }
        return prevCart.map(item =>
          item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      return [...prevCart, { ...product, quantity: 1 }];
    });
    setIsCartOpen(true); // Automatically slide open cart sidebar for feedback
  };

  const updateQuantity = (itemId, amount) => {
    setCart(prevCart => {
      return prevCart.map(item => {
        if (item.id === itemId) {
          const newQty = item.quantity + amount;
          // Verify stock limit
          if (amount > 0 && newQty > item.stockQuantity) {
            alert(`Only ${item.stockQuantity} items in stock.`);
            return item;
          }
          if (newQty <= 0) return null;
          return { ...item, quantity: newQty };
        }
        return item;
      }).filter(Boolean);
    });
  };

  // Checkout (Calls Order Service through Gateway)
  const handleCheckout = async () => {
    if (cart.length === 0) return;
    if (!shippingAddress.trim()) {
      setCheckoutError('Please enter a shipping address.');
      return;
    }
    setIsCheckingOut(true);
    setCheckoutError('');

    const orderPayload = {
      items: cart.map(item => ({
        productId: item.id,
        quantity: item.quantity
      })),
      shippingAddress: shippingAddress
    };

    try {
      const res = await fetch(`${API_BASE}/api/v1/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(orderPayload)
      });

      if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText || 'Checkout failed. Please verify stock.');
      }

      const orderResult = await res.json();
      setLastOrder(orderResult); // Save order for receipt view
      setCart([]); // Clear cart
      setShippingAddress(''); // Clear address field
      setIsCartOpen(false);
      
      // Refresh catalog to see updated stock
      fetchProducts(selectedCategory);
    } catch (err) {
      console.error(err);
      setCheckoutError(err.message || 'Service temporarily unavailable.');
    } finally {
      setIsCheckingOut(false);
    }
  };

  // Admin: Create product
  const handleCreateProduct = async (e) => {
    e.preventDefault();
    setAdminError('');
    setAdminSuccess('');

    if (!newProductName || !newProductPrice || !newProductStock) {
      setAdminError('Product Name, Price, and Stock are required.');
      return;
    }

    const payload = {
      name: newProductName,
      description: newProductDesc,
      price: parseFloat(newProductPrice),
      stockQuantity: parseInt(newProductStock),
      category: newProductCategory,
      imageUrl: newProductImg || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=600&auto=format&fit=crop'
    };

    try {
      const res = await fetch(`${API_BASE}/api/v1/products`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        const errMsg = await res.text();
        throw new Error(errMsg || 'Failed to create product.');
      }

      setAdminSuccess('Product created successfully!');
      // Reset form
      setNewProductName('');
      setNewProductDesc('');
      setNewProductPrice('');
      setNewProductStock('');
      setNewProductImg('');

      // Refresh catalog
      fetchProducts(selectedCategory);
    } catch (err) {
      setAdminError(err.message);
    }
  };

  const getCartTotal = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0).toFixed(2);
  };

  const getCartCount = () => {
    return cart.reduce((total, item) => total + item.quantity, 0);
  };

  // Auth Screen Render
  if (!token) {
    return (
      <div className="auth-container">
        <div className="auth-box glass-panel">
          <div className="auth-header">
            <div className="logo-text">Cartify</div>
            <p style={{ color: '#6b7280', fontSize: '0.9rem' }}>
              {isLoginView ? 'Sign in to access your dashboard' : 'Create an account to start shopping'}
            </p>
          </div>

          {authError && <div className="error-alert">{authError}</div>}
          {authSuccess && (
            <div className="error-alert" style={{ background: 'rgba(16,185,129,0.15)', borderColor: 'rgba(16,185,129,0.3)', color: '#a7f3d0' }}>
              {authSuccess}
            </div>
          )}

          <form onSubmit={handleAuthSubmit}>
            <div className="form-group">
              <label className="form-label">Username</label>
              <input
                type="text"
                className="form-input"
                placeholder="e.g. customer, admin"
                value={usernameInput}
                onChange={(e) => setUsernameInput(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-input"
                placeholder="••••••••"
                value={passwordInput}
                onChange={(e) => setPasswordInput(e.target.value)}
              />
            </div>

            {!isLoginView && (
              <div className="form-group">
                <label className="form-label">Account Type (Role)</label>
                <select
                  className="form-select"
                  value={roleInput}
                  onChange={(e) => setRoleInput(e.target.value)}
                >
                  <option value="ROLE_USER">Customer (User)</option>
                  <option value="ROLE_ADMIN">Store Administrator (Admin)</option>
                </select>
              </div>
            )}

            <button type="submit" className="btn-primary" style={{ marginTop: '12px' }}>
              {isLoginView ? 'Sign In' : 'Create Account'}
            </button>
          </form>

          <div className="auth-toggle">
            {isLoginView ? (
              <>
                New to Nexus?{' '}
                <span className="auth-toggle-link" onClick={() => { setIsLoginView(false); setAuthError(''); }}>
                  Register here
                </span>
              </>
            ) : (
              <>
                Already have an account?{' '}
                <span className="auth-toggle-link" onClick={() => { setIsLoginView(true); setAuthError(''); }}>
                  Login here
                </span>
              </>
            )}
          </div>
        </div>
      </div>
    );
  }

  // Dashboard Screen Render
  return (
    <div className="dashboard-layout">
      {/* Navigation */}
      <nav className="navbar">
        <div className="nav-logo">Cartify</div>
        <div className="nav-actions">
          {user && (
            <div className="user-tag">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{ color: 'var(--primary)' }}>
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <span>{user.username}</span>
              <span className={`role-badge ${user.role === 'ROLE_ADMIN' ? 'role-admin' : 'role-user'}`}>
                {user.role === 'ROLE_ADMIN' ? 'Admin' : 'Customer'}
              </span>
            </div>
          )}

          {user && user.role === 'ROLE_ADMIN' && (
            <button
              className="btn-nav"
              style={{
                borderColor: showAdminPanel ? 'var(--primary)' : 'var(--glass-border)',
                background: showAdminPanel ? 'rgba(201, 24, 74, 0.08)' : 'transparent',
                color: showAdminPanel ? 'var(--primary)' : '#4b5563'
              }}
              onClick={() => setShowAdminPanel(!showAdminPanel)}
            >
              🛠️ Control Panel
            </button>
          )}

          <button className="btn-cart" onClick={() => setIsCartOpen(true)}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="8" cy="21" r="1" />
              <circle cx="19" cy="21" r="1" />
              <path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12" />
            </svg>
            Cart
            <span className="cart-count">{getCartCount()}</span>
          </button>

          <button className="btn-nav" onClick={handleLogout} style={{ color: 'var(--primary)', borderColor: 'rgba(201, 24, 74, 0.25)' }}>
            Logout
          </button>
        </div>
      </nav>

      {/* Main Container */}
      <div className="main-content">
        
        {/* Admin Panel Panel */}
        {showAdminPanel && user && user.role === 'ROLE_ADMIN' && (
          <div className="admin-section glass-panel">
            <div className="admin-header">
              <h2 className="admin-title">Store Administration Dashboard</h2>
              <button className="btn-close" onClick={() => setShowAdminPanel(false)}>×</button>
            </div>
            
            {adminError && <div className="error-alert">{adminError}</div>}
            {adminSuccess && (
              <div className="error-alert" style={{ background: 'rgba(16,185,129,0.15)', borderColor: 'rgba(16,185,129,0.3)', color: '#a7f3d0' }}>
                {adminSuccess}
              </div>
            )}

            <div className="admin-grid">
              {/* Product creation form */}
              <div>
                <h3 style={{ fontSize: '1.1rem', marginBottom: '16px', color: 'var(--primary)' }}>Add New Catalog Item</h3>
                <form onSubmit={handleCreateProduct}>
                  <div className="admin-form-group">
                    <label className="form-label" style={{ fontSize: '0.75rem' }}>Product Name</label>
                    <input
                      type="text"
                      className="form-input"
                      style={{ padding: '10px' }}
                      placeholder="e.g. Mechanical Keyboard"
                      value={newProductName}
                      onChange={(e) => setNewProductName(e.target.value)}
                    />
                  </div>
                  <div className="admin-form-group">
                    <label className="form-label" style={{ fontSize: '0.75rem' }}>Description</label>
                    <textarea
                      className="form-input"
                      style={{ padding: '10px', minHeight: '60px', fontFamily: 'inherit' }}
                      placeholder="Product details..."
                      value={newProductDesc}
                      onChange={(e) => setNewProductDesc(e.target.value)}
                    />
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                    <div className="admin-form-group">
                      <label className="form-label" style={{ fontSize: '0.75rem' }}>Price (USD)</label>
                      <input
                        type="number"
                        step="0.01"
                        className="form-input"
                        style={{ padding: '10px' }}
                        placeholder="19.99"
                        value={newProductPrice}
                        onChange={(e) => setNewProductPrice(e.target.value)}
                      />
                    </div>
                    <div className="admin-form-group">
                      <label className="form-label" style={{ fontSize: '0.75rem' }}>Initial Stock</label>
                      <input
                        type="number"
                        className="form-input"
                        style={{ padding: '10px' }}
                        placeholder="50"
                        value={newProductStock}
                        onChange={(e) => setNewProductStock(e.target.value)}
                      />
                    </div>
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                    <div className="admin-form-group">
                      <label className="form-label" style={{ fontSize: '0.75rem' }}>Category</label>
                      <select
                        className="form-select"
                        style={{ padding: '10px' }}
                        value={newProductCategory}
                        onChange={(e) => setNewProductCategory(e.target.value)}
                      >
                        <option value="Electronics">Electronics</option>
                        <option value="Furniture">Furniture</option>
                        <option value="Accessories">Accessories</option>
                      </select>
                    </div>
                    <div className="admin-form-group">
                      <label className="form-label" style={{ fontSize: '0.75rem' }}>Image URL</label>
                      <input
                        type="text"
                        className="form-input"
                        style={{ padding: '10px' }}
                        placeholder="https://images.unsplash..."
                        value={newProductImg}
                        onChange={(e) => setNewProductImg(e.target.value)}
                      />
                    </div>
                  </div>
                  <button type="submit" className="btn-primary" style={{ padding: '10px', fontSize: '0.9rem' }}>
                    Publish to Catalog
                  </button>
                </form>
              </div>

              {/* Quick stock inventory lists */}
              <div>
                <h3 style={{ fontSize: '1.1rem', marginBottom: '16px', color: 'var(--primary)' }}>Live Stock Levels</h3>
                <div style={{ maxHeight: '280px', overflowY: 'auto', border: '1px solid var(--glass-border)', borderRadius: '8px', padding: '15px', background: '#f9fafb' }}>
                  {products.length === 0 ? (
                    <p style={{ color: '#6b7280', fontSize: '0.9rem' }}>No products available.</p>
                  ) : (
                    products.map(p => (
                      <div key={p.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid rgba(0,0,0,0.05)', fontSize: '0.9rem' }}>
                        <span>{p.name}</span>
                        <span style={{ fontWeight: '600', color: p.stockQuantity < 5 ? 'var(--accent)' : 'var(--success)' }}>
                          {p.stockQuantity} units
                        </span>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Filter Bar */}
        <div className="filter-bar">
          <div className="filter-pills">
            {['All', 'Electronics', 'Furniture', 'Accessories'].map(cat => (
              <button
                key={cat}
                className={`filter-pill ${selectedCategory === cat ? 'active' : ''}`}
                onClick={() => setSelectedCategory(cat)}
              >
                {cat}
              </button>
            ))}
          </div>
          <button className="btn-nav" onClick={() => fetchProducts(selectedCategory)}>
            🔄 Sync Catalog
          </button>
        </div>

        {/* Error States */}
        {productError && (
          <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', marginBottom: '40px', border: '1px solid rgba(244,63,94,0.3)' }}>
            <div style={{ fontSize: '2.5rem', marginBottom: '16px' }}>🔌</div>
            <h3 style={{ fontSize: '1.25rem', marginBottom: '8px' }}>Services Outage Detected</h3>
            <p style={{ color: '#9ca3af', maxWidth: '600px', margin: '0 auto 20px auto', fontSize: '0.95rem' }}>
              {productError}
            </p>
            <div style={{ background: '#ffffff', border: '1px solid var(--glass-border)', padding: '15px', borderRadius: '8px', maxWidth: '500px', margin: '0 auto', textAlign: 'left', fontSize: '0.85rem' }}>
              <strong style={{ color: 'var(--primary)' }}>Quick Interview Talking Point:</strong> Downstream services run independently. If the product-service goes down, catalog retrieval fails but the gateway and identity systems remain active.
            </div>
          </div>
        )}

        {/* Loading State */}
        {loadingProducts && <div style={{ textAlign: 'center', padding: '60px', fontSize: '1.2rem', color: '#9ca3af' }}>Syncing with microservices catalog...</div>}

        {/* Catalog Grid */}
        {!loadingProducts && !productError && (
          <div className="products-grid">
            {products.length === 0 ? (
              <div style={{ gridColumn: '1/-1', textAlign: 'center', padding: '60px', color: '#6b7280' }}>
                No products found in this category.
              </div>
            ) : (
              products.map(product => (
                <div key={product.id} className="product-card glass-card">
                  <div className="product-image-container">
                    <img src={product.imageUrl} alt={product.name} className="product-image" />
                    <span className="product-category-tag">{product.category}</span>
                  </div>
                  <div className="product-info">
                    <h3 className="product-title">{product.name}</h3>
                    <p className="product-desc">{product.description}</p>
                    <div className="product-footer">
                      <div className="product-price">₹{product.price.toFixed(2)}</div>
                      <div>
                        <span className={`product-stock ${product.stockQuantity < 5 ? 'low' : ''}`}>
                          {product.stockQuantity === 0 ? 'Out of stock' : `${product.stockQuantity} in stock`}
                        </span>
                      </div>
                    </div>
                    <button
                      className="btn-add-cart"
                      style={{ marginTop: '16px' }}
                      disabled={product.stockQuantity === 0}
                      onClick={() => addToCart(product)}
                    >
                      {product.stockQuantity === 0 ? 'Sold Out' : 'Add to Cart'}
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      {/* Cart Sidebar Modal */}
      <div className={`cart-overlay ${isCartOpen ? 'active' : ''}`} onClick={() => setIsCartOpen(false)}>
        <div className="cart-sidebar" onClick={(e) => e.stopPropagation()}>
          <div className="cart-header">
            <h2 className="cart-title">Your Cart</h2>
            <button className="btn-close" onClick={() => setIsCartOpen(false)}>×</button>
          </div>

          <div className="cart-items">
            {checkoutError && <div className="error-alert">{checkoutError}</div>}
            
            {cart.length === 0 ? (
              <div className="cart-empty">
                <div className="cart-empty-icon">🛒</div>
                <p>Your shopping cart is empty.</p>
                <p style={{ fontSize: '0.8rem', color: '#4b5563', marginTop: '6px' }}>Select some high-grade gear to start.</p>
              </div>
            ) : (
              cart.map(item => (
                <div key={item.id} className="cart-item">
                  <img src={item.imageUrl} alt={item.name} className="cart-item-image" />
                  <div className="cart-item-info">
                    <h4 className="cart-item-name">{item.name}</h4>
                    <div className="cart-item-price">₹{(item.price * item.quantity).toFixed(2)}</div>
                    <div className="cart-item-qty">
                      <button className="btn-qty" onClick={() => updateQuantity(item.id, -1)}>-</button>
                      <span className="qty-val">{item.quantity}</span>
                      <button className="btn-qty" onClick={() => updateQuantity(item.id, 1)}>+</button>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>

          {cart.length > 0 && (
            <div className="cart-footer">
              <div className="form-group" style={{ marginBottom: '16px' }}>
                <label className="form-label" style={{ fontSize: '0.75rem' }}>Shipping Address</label>
                <textarea
                  className="form-input"
                  style={{ minHeight: '60px', fontFamily: 'inherit', padding: '10px', fontSize: '0.85rem' }}
                  placeholder="e.g. Flat 402, Ruby Apts, Mumbai"
                  value={shippingAddress}
                  onChange={(e) => setShippingAddress(e.target.value)}
                />
              </div>
              <div className="cart-total-row">
                <span>Total Amount:</span>
                <span className="cart-total-price">₹{getCartTotal()}</span>
              </div>
              <button
                className="btn-primary"
                onClick={handleCheckout}
                disabled={isCheckingOut}
              >
                {isCheckingOut ? 'Securing Stock & Placing Order...' : 'Confirm Order (Place checkout)'}
              </button>
              
              <div style={{ marginTop: '16px', background: 'rgba(0,0,0,0.02)', border: '1px dashed var(--glass-border)', padding: '10px', borderRadius: '6px', fontSize: '0.75rem', color: '#4b5563', lineHeight: '1.3' }}>
                <strong style={{ color: 'var(--secondary)' }}>Behind the Scenes:</strong> This request goes to Gateway (8080) which checks JWT, forwards it to Order-Service, which verifies stock with Product-Service via OpenFeign.
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Order Success Modal Receipt */}
      {lastOrder && (
        <div className="modal-overlay">
          <div className="success-box glass-panel">
            <div className="success-icon-wrapper">✓</div>
            <h2 className="success-title">Order Confirmed!</h2>
            <p className="success-desc">
              Your transaction completed successfully. Stock levels have been reduced across the microservices mesh.
            </p>
            
            <div className="invoice-details">
              <div className="invoice-row">
                <span>Invoice ID:</span>
                <strong style={{ color: 'var(--primary)' }}>CRT-000{lastOrder.id}</strong>
              </div>
              <div className="invoice-row">
                <span>Buyer Account ID:</span>
                <span>User #{lastOrder.customerId}</span>
              </div>
              <div className="invoice-row">
                <span>Order Status:</span>
                <span style={{ color: 'var(--success)', fontWeight: '600' }}>{lastOrder.status}</span>
              </div>
              <div className="invoice-row">
                <span>Date &amp; Time:</span>
                <span>{new Date(lastOrder.orderDate).toLocaleString()}</span>
              </div>
              <div className="invoice-row">
                <span>Total Paid:</span>
                <span style={{ color: 'var(--secondary)', fontWeight: '700', fontSize: '1.05rem' }}>
                  ₹{lastOrder.totalPrice.toFixed(2)}
                </span>
              </div>
              <div className="invoice-row" style={{ flexDirection: 'column', gap: '4px', alignItems: 'flex-start', marginTop: '6px', paddingTop: '6px', borderTop: '1px dashed var(--glass-border)' }}>
                <span style={{ fontSize: '0.8rem', color: '#6b7280' }}>Shipping Address:</span>
                <span style={{ fontSize: '0.85rem', color: '#111827', wordBreak: 'break-word', fontWeight: '500' }}>{lastOrder.shippingAddress || 'N/A'}</span>
              </div>
            </div>

            <button className="btn-primary" onClick={() => setLastOrder(null)}>
              Return to Marketplace
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
