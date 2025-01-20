"use client";

import Pagination from '../../../components/Pagination';
import { useProductsWithPagination } from '@/app/hooks/useProductsWithPagination';
import Link from 'next/link';
import Image from 'next/image';

export default function ProductModifyList() {
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
      <h2 className="text-xl font-bold mb-4">수정할 상품 선택</h2>
      {(!products || products.length === 0) ? (
        <p className="text-gray-500 text-center py-4">등록된 상품이 없습니다.</p>
      ) : (
        <>
          <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {products.map(product => (
                <li className="flex flex-col p-4 border rounded-lg hover:shadow-md transition-shadow">
                <div className="w-full relative h-48">
                    <Image
                        src={product.imgUrl}
                        alt={product.name}
                        fill
                        className="object-cover rounded"
                    />
                </div>
                <div className="flex flex-col space-y-2 mt-4">
                    <Link
                        href={`/admin/product/modify/${product.id}`}
                        className="font-semibold text-black hover:text-gray-700 transition-colors text-lg"
                    >
                    {product.name}
                    </Link>
                    <p className="text-black font-medium">
                        {product.price.toLocaleString()}원
                    </p>
                </div>
                </li>
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