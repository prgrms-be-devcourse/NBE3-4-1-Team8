"use client";

import { useEffect, useState } from "react";
import { useProduct } from "@/app/hooks/useProduct";
import Image from 'next/image';
import { use } from 'react';

export default function Page({ params }: { params: Promise<{ id: string }> }) {
  const unwrappedParams = use(params);
  const productId = parseInt(unwrappedParams.id);
  const [quantity, setQuantity] = useState<number>(1);
  const [isAddedToCart, setIsAddedToCart] = useState<boolean>(false);

  const product = useProduct(productId);

  if (!product) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-6xl mx-auto bg-white rounded-lg shadow-lg p-6">
          <p className="text-center text-gray-600">상품을 불러오는 중...</p>
        </div>
      </div>
    );
  }

  console.log(product);
  const updateQuantity = (change: number) => {
    const newQuantity = quantity + change;
    if (newQuantity >= 1) {
      setQuantity(newQuantity);
    }
  };

  const handleAddToCart = () => {
    setIsAddedToCart(true);
    setTimeout(() => setIsAddedToCart(false), 2000);
  };

  return (
    <div className="container mx-auto px-4 py-8 text-black">
      <div className="max-w-6xl mx-auto bg-white rounded-lg shadow-lg overflow-hidden">
        <div className="md:flex">
          <div className="md:w-1/2 p-6">
            <div className="aspect-square relative rounded-lg overflow-hidden bg-gray-100">
              <Image
                src={product.imgUrl}
                alt={product.name}
                fill
                className="object-cover"
              />
            </div>
          </div>
          <div className="md:w-1/2 p-6 space-y-6">
            <div>
              <h1 className="text-3xl font-bold mt-1">{product.name}</h1>
              <p className="text-2xl font-semibold mt-2">{product.price}원</p>
            </div>
            <div className="space-y-4">
              <div>
                <h2 className="font-semibold">상품 설명</h2>
                <p className="mt-1 text-gray-600">{product.content}</p>
              </div>
              <div className="space-y-4">
                <div>
                  <h2 className="font-semibold mb-2">수량</h2>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => updateQuantity(-1)}
                      className="px-3 py-1 border border-gray-800 rounded hover:bg-gray-800 hover:text-white transition-colors"
                    >
                      -
                    </button>
                    <span className="w-12 text-center">{quantity}</span>
                    <button
                      onClick={() => updateQuantity(1)}
                      className="px-3 py-1 border border-gray-800 rounded hover:bg-gray-800 hover:text-white transition-colors"
                    >
                      +
                    </button>
                  </div>
                </div>
                <div className="pt-4">
                  <button
                    onClick={handleAddToCart}
                    className="w-full bg-gray-800 text-white py-3 rounded hover:bg-gray-700 transition-colors"
                  >
                    {isAddedToCart ? '장바구니에 담았습니다!' : '장바구니 담기'}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}