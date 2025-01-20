"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/router";
import { useProduct } from "@/app/hooks/useProduct";
import { use } from 'react';
import ProductForm from "../../../../components/ProductForm";

export default function ProductModify({ params }: { params: Promise<{ id: string }> }) {
    const unwrappedParams = use(params);
    const productId = parseInt(unwrappedParams.id);

    const product = useProduct(productId);
    const [initialData, setInitialData] = useState<{
        name: string;
        content: string;
        price: string;
        imgUrl: string;
        quantity: string;
    } | undefined>(undefined);

    useEffect(() => {
        if (product) {
            setInitialData({
                name: product.name,
                content: product.content,
                price: product.price.toString(),
                imgUrl: product.imgUrl,
                quantity: "",
            });
        }
    }, [product]);

    const handleSubmit = async (formData: { name: string; content: string; price: number; imgUrl: string; quantity: number; }) => {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/products/${productId}`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formData),
            credentials: "include",
        });

        if (!response.ok) {
            throw new Error('Failed to update product');
        }

        const data = await response.json();
        console.log('Product updated:', data);
    };

    return (
        <div className="container mx-auto px-4 py-8 text-black">
            <h1 className="text-2xl font-bold mb-6 text-center">상품 수정</h1>

            <ProductForm initialData={initialData} onSubmit={handleSubmit} />

        </div>
    );
}