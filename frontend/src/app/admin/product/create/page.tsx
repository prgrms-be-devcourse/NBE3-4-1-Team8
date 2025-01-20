"use client";

import ProductForm from "../../../components/ProductForm";

export default function ProductCreate() {
  const handleSubmit = async (formData: { name: string; content: string; price: number; imgUrl: string; quantity: number; }) => {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/products`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
      credentials: "include",
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`오류 발생: ${errorData.message || "알 수 없는 오류"}`);
    }
  };

  return (
      <div className="container mx-auto px-4 py-8 text-black">
        <h1 className="text-2xl font-bold mb-6 text-center">상품 등록</h1>
        <ProductForm onSubmit={handleSubmit} />
      </div>
  );
}