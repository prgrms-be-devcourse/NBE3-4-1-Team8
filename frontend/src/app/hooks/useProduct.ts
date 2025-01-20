import { useEffect, useState } from "react";
import {id} from "postcss-selector-parser";

export const useProduct = (productId: number) => {
  const [product, setProduct] = useState<Product | undefined>(undefined);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  if (productId === null) {
    throw new Error('상품 ID 값이 존재하지 않습니다.');
  }

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setIsLoading(true);
        const response = await fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/products/${productId}`,
          {
            method: 'GET',
            credentials: 'include',
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );

        if (!response.ok) {
          throw new Error('Failed to fetch product');
        }

        const responseData: ApiResponse<Product> = await response.json();

        if (!responseData.success) {
          throw new Error(responseData.message || '상품을 불러오는데 실패했습니다.');
        }

        setProduct(Array.isArray(responseData.data) ? responseData.data[0] : responseData.data);
      } catch (err) {
        setError(err instanceof Error ? err.message : '상품을 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);

  return product;
};