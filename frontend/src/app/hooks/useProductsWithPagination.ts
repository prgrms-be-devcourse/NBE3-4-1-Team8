import { useState, useEffect } from 'react';

export const useProductsWithPagination = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setIsLoading(true);
        const response = await fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/products?page=${currentPage}`,
          {
            method: 'GET',
            credentials: 'include',
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );

        if (!response.ok) {
          throw new Error('Failed to fetch products');
        }

        const responseData: ApiResponse<Product> = await response.json();
        
        if (!responseData.success) {
          throw new Error(responseData.message || '상품을 불러오는데 실패했습니다.');
        }

        setProducts(responseData.data.content);
        setTotalPages(responseData.data.totalPages);
      } catch (err) {
        setError(err instanceof Error ? err.message : '상품을 불러오는데 실패했습니다.');
        setProducts([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchProducts();
  }, [currentPage]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return {
    products,
    currentPage,
    totalPages,
    isLoading,
    error,
    handlePageChange,
  };
};
