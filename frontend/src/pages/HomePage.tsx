import React from 'react';
import { Link } from 'react-router-dom';
import { ProductGrid } from '../components/ProductGrid';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';

export const HomePage: React.FC = () => {
  const productTypes = [
    { type: 'book', name: 'Books', icon: '📚', description: 'Discover amazing stories' },
    { type: 'cd', name: 'CDs', icon: '💿', description: 'Listen to great music' },
    { type: 'dvd', name: 'DVDs', icon: '📀', description: 'Watch fantastic movies' },
  ];

  return (
    <div className="container mx-auto px-4 py-8 space-y-12">
      {/* Hero Section */}
      <section className="text-center py-16 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg">
        <h1 className="text-4xl md:text-6xl font-bold mb-6">
          Welcome to ITSS Store
        </h1>
        <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
          Discover our amazing collection of books, CDs, and DVDs. 
          Find your next favorite story, song, or movie.
        </p>
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Button size="lg" asChild>
            <Link to="/products">Shop Now</Link>
          </Button>
          <Button variant="outline" size="lg" asChild>
            <Link to="/categories">Browse Categories</Link>
          </Button>
        </div>
      </section>

      {/* Product Categories */}
      <section>
        <h2 className="text-3xl font-bold text-center mb-8">Shop by Category</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {productTypes.map((category) => (
            <Card key={category.type} className="hover:shadow-lg transition-shadow">
              <CardHeader className="text-center">
                <div className="text-6xl mb-4">{category.icon}</div>
                <CardTitle>{category.name}</CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-muted-foreground mb-4">{category.description}</p>
                <Button asChild>
                  <Link to={`/products?type=${category.type}`}>
                    Browse {category.name}
                  </Link>
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      {/* Featured Products */}
      <section>
        <h2 className="text-3xl font-bold text-center mb-8">Featured Products</h2>
        <ProductGrid />
      </section>

      {/* Features Section */}
      <section className="py-16">
        <h2 className="text-3xl font-bold text-center mb-12">Why Choose Us?</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="text-center">
            <div className="text-4xl mb-4">🚚</div>
            <h3 className="text-xl font-semibold mb-2">Fast Delivery</h3>
            <p className="text-muted-foreground">
              Rush order support available for urgent deliveries
            </p>
          </div>
          <div className="text-center">
            <div className="text-4xl mb-4">🛡️</div>
            <h3 className="text-xl font-semibold mb-2">Quality Guarantee</h3>
            <p className="text-muted-foreground">
              All products are carefully inspected before shipping
            </p>
          </div>
          <div className="text-center">
            <div className="text-4xl mb-4">💳</div>
            <h3 className="text-xl font-semibold mb-2">Secure Payment</h3>
            <p className="text-muted-foreground">
              Multiple payment options with secure processing
            </p>
          </div>
        </div>
      </section>
    </div>
  );
};