import { ProductCard } from '../components/ProductCard';
import Pagination from '../components/Pagination';
import { useProductsWithPagination } from '../hooks/useProductsWithPagination';

export default function ProductList() {
  const {
    products,
    currentPage,
    totalPages,
    isLoading,
    error,
    handlePageChange,
  } = useProductsWithPagination();

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[200px]">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 text-red-500 p-4 rounded-lg">
        {error}
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden p-6">
      <h2 className="text-xl font-bold mb-4">상품 목록</h2>
      {(!products || products.length === 0) ? (
        <p className="text-gray-500 text-center py-4">등록된 상품이 없습니다.</p>
      ) : (
        <>
          <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {products.map(product => (
              <ProductCard key={product.Id} product={product} />
            ))}
          </ul>
          {totalPages > 1 && (
            <Pagination
              page={currentPage}
              totalPages={totalPages}
              handlePageChange={handlePageChange}
            />
          )}
        </>
      )}
    </div>
  );
}