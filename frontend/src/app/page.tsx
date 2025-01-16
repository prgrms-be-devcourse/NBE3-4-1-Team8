"use client";

import Image from "next/image";
import { useState } from "react";

interface Product {
  id: number;
  category: string;
  name: string;
  price: number;
  image: string;
}

interface CartItem {
  product: Product;
  quantity: number;
}

export default function Home() {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  const products: Product[] = [
    {
      id: 1,
      category: '커피콩',
      name: 'Columbia Nariñó',
      price: 5000,
      image: 'https://i.imgur.com/HKOFQYa.jpeg'
    },
    // 추가 제품들...
  ];

  const addToCart = (product: Product) => {
    setCartItems(prev => {
      const existingItem = prev.find(item => item.product.id === product.id);
      if (existingItem) {
        return prev.map(item =>
          item.product.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }
      return [...prev, { product, quantity: 1 }];
    });
  };

  const updateQuantity = (productId: number, change: number) => {
    setCartItems(prev => {
      const updated = prev.map(item => {
        if (item.product.id === productId) {
          const newQuantity = item.quantity + change;
          return newQuantity > 0 ? { ...item, quantity: newQuantity } : null;
        }
        return item;
      });
      return updated.filter((item): item is CartItem => item !== null);
    });
  };

  const total = cartItems.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);

  return (
    <div className="container mx-auto px-4 py-8 text-black">
      <h1 className="text-3xl font-bold text-center mb-8">Grids & Circle</h1>

      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        <div className="md:flex">
          {/* 제품 목록 섹션 */}
          <div className="md:w-2/3 p-6">
            <h2 className="text-xl font-bold mb-4">상품 목록</h2>
            <ul className="space-y-4">
              {products.map(product => (
                <li key={product.id} className="flex items-center space-x-4 p-4 border rounded-lg">
                  <div className="w-20 relative h-20">
                    {/*<Image
                      src={product.image}
                      alt={product.name}
                      fill
                      className="object-cover rounded"
                    />*/}
                  </div>
                  <div className="flex-1">
                    <p className="text-black">{product.category}</p>
                    <p className="font-semibold text-black">{product.name}</p>
                  </div>
                  <div className="text-center text-black">
                    <p>{product.price.toLocaleString()}원</p>
                  </div>
                  <button
                    onClick={() => addToCart(product)}
                    className="px-4 py-2 border border-gray-800 rounded hover:bg-gray-800 hover:text-white transition-colors"
                  >
                    추가
                  </button>
                </li>
              ))}
            </ul>
          </div>

          {/* 주문 요약 섹션 */}
          <div className="md:w-1/3 bg-gray-100 p-6">
            <h2 className="text-xl font-bold mb-4">Summary</h2>
            <hr className="my-4" />

            {cartItems.map(item => (
              <div key={item.product.id} className="mb-2">
                <div className="flex justify-between items-center text-black">
                  <span>{item.product.name}</span>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => updateQuantity(item.product.id, -1)}
                      className="px-2 py-1 border border-gray-800 rounded hover:bg-gray-800 hover:text-white transition-colors"
                    >
                      -
                    </button>
                    <span className="px-2">
                      {item.quantity}개
                    </span>
                    <button
                      onClick={() => updateQuantity(item.product.id, 1)}
                      className="px-2 py-1 border border-gray-800 rounded hover:bg-gray-800 hover:text-white transition-colors"
                    >
                      +
                    </button>
                  </div>
                </div>
              </div>
            ))}

            <form className="space-y-4 mt-6">
              <div>
                <label htmlFor="email" className="block text-sm font-medium mb-1 text-black">
                  이메일
                </label>
                <input
                  type="email"
                  id="email"
                  className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-gray-800 text-black"
                />
              </div>
              <div>
                <label htmlFor="address" className="block text-sm font-medium mb-1 text-black">
                  주소
                </label>
                <input
                  type="text"
                  id="address"
                  className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-gray-800 text-black"
                />
              </div>
              <div>
                <label htmlFor="postcode" className="block text-sm font-medium mb-1 text-black">
                  우편번호
                </label>
                <input
                  type="text"
                  id="postcode"
                  className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-gray-800 text-black"
                />
              </div>
              <p className="text-black">
                당일 오후 2시 이후의 주문은 다음날 배송을 시작합니다.
              </p>
            </form>

            <div className="mt-6 pt-4 border-t flex justify-between items-center">
              <h3 className="text-lg font-bold text-black">총금액</h3>
              <h3 className="text-lg font-bold text-black">{total.toLocaleString()}원</h3>
            </div>

            <button className="w-full mt-4 bg-gray-800 text-white py-3 rounded hover:bg-gray-700 transition-colors">
              결제하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}