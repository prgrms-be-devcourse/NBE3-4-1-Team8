"use client";

import Image from "next/image";
import { useState } from "react";
import Link from "next/link";

interface Product {
  id: number;
  category: string;
  name: string;
  price: number;
  image: string;
}

export default function Home() {
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

  return (
    <div className="container mx-auto px-4 py-8 text-black">
      <h1 className="text-3xl font-bold text-center mb-8">Grids & Circle</h1>

      <div className="bg-white rounded-lg shadow-lg overflow-hidden p-6">
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
                <Link href={`/product/${product.id}`} className="font-semibold text-black">{product.name}</Link>
              </div>
              <div className="text-center text-black">
                <p>{product.price.toLocaleString()}원</p>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}