"use client";

import Pagination from '../../../components/Pagination';
import { useProductsWithPagination } from '@/app/hooks/useProductsWithPagination';
import Image from 'next/image';

export default function ProductDeleteList() {
    const {
        products,
        currentPage,
        totalPages,
        isLoading,
        error,
        handlePageChange,
    } = useProductsWithPagination();

    const handleDelete = async (productId: number, productName: string) => {
        const confirmDelete = window.confirm(
            `${productName} 상품을 정말 삭제하시겠습니까?`
        );

        if (!confirmDelete) {
            return; // 사용자가 삭제를 취소한 경우
        }

        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/products/${productId}`, {
                method: 'DELETE',
                credentials: "include",
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || '상품 삭제에 실패했습니다.');
            }

            alert('상품이 성공적으로 삭제되었습니다.');
            // 삭제 성공 후 새로고침
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert(error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.');
        }
    };

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
            <h2 className="text-xl font-bold mb-4">삭제할 상품 선택</h2>
            {(!products || products.length === 0) ? (
                <p className="text-gray-500 text-center py-4">등록된 상품이 없습니다.</p>
            ) : (
                <>
                    <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {products.map((product) => (
                            <li key={product.id} className="flex flex-col p-4 border rounded-lg hover:shadow-md transition-shadow">
                                <div className="w-full relative h-48">
                                    <Image
                                        src={product.imgUrl}
                                        alt={product.name}
                                        fill
                                        className="object-cover rounded"
                                    />
                                </div>
                                <div className="flex flex-col space-y-2 mt-4">
                                    <button
                                        onClick={() => handleDelete(product.id, product.name)}
                                        className="font-semibold text-red-500 hover:text-red-700 transition-colors text-lg"
                                    >
                                        {product.name}
                                    </button>
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
