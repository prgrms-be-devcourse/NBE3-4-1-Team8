"use client";

import Link from "next/link";

const AdminPage = () => {
  return (
    <div className="container mx-auto px-4 py-8 text-black">
      <h1 className="text-3xl font-bold text-center mb-8">Admin Page</h1>

      <div className="bg-white rounded-lg shadow-lg overflow-hidden p-6">
        <h2 className="text-xl font-bold mb-4">관리자 작업</h2>
        <div className="space-y-4">
          {/* 상품 등록 버튼 */}
          <div className="flex justify-center">
            <Link href="/admin/product/create">
              <button className="bg-blue-500 text-white px-8 py-4 rounded-lg shadow hover:bg-blue-600 transition">
                상품 등록
              </button>
            </Link>
          </div>

          {/* 상품 수정 버튼 */}
          <div className="flex justify-center">
            <Link href="/admin/product/modify">
              <button className="bg-yellow-500 text-white px-8 py-4 rounded-lg shadow hover:bg-yellow-600 transition">
                상품 수정
              </button>
            </Link>
          </div>

          {/* 상품 삭제 버튼 */}
          <div className="flex justify-center">
            <Link href="/admin/product/delete">
              <button className="bg-red-500 text-white px-8 py-4 rounded-lg shadow hover:bg-red-600 transition">
                상품 삭제
              </button>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPage;