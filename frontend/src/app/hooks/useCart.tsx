import { useState } from 'react';
import { useRouter } from 'next/navigation';

export const useCart = () => {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const router = useRouter();

  const addToCart = async (productId: number, quantity: number) => {
    if (isLoading) return;

    try {
      setIsLoading(true);

      const response = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/carts`,
        {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ productId, quantity })
        }
      );

      const responseData: ApiResponse<number> = await response.json();

      if (!responseData.success) {
        throw new Error(responseData.message || '장바구니에 상품 등록중 예외가 발생하였습니다.');
      }

      alert('상품을 장바구니에 추가했습니다!');
    } catch (error) {
      alert(error instanceof Error ? error.message : '장바구니에 상품 등록중 예외가 발생하였습니다.');
      router.push('/');
    } finally {
      setIsLoading(false);
    }
  };

  return {
    isLoading,
    addToCart
  };
};