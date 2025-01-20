import React from 'react';

interface PaginationProps {
  page: number;
  totalPages: number;
  handlePageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({ page, totalPages, handlePageChange }) => {
  const baseButtonStyles = "px-3 py-2 rounded-md text-sm font-medium transition-colors";
  const activeButtonStyles = "bg-blue-600 text-white hover:bg-blue-700";
  const inactiveButtonStyles = "bg-white text-gray-700 hover:bg-gray-50 border border-gray-300";
  const disabledButtonStyles = "bg-gray-100 text-gray-400 cursor-not-allowed border border-gray-300";

  return (
    <div className="flex justify-center mt-4">
      <ul className="flex space-x-2">
        <li>
          <button
            className={`${baseButtonStyles} ${page === 0 ? disabledButtonStyles : inactiveButtonStyles}`}
            onClick={() => handlePageChange(page !== 0 ? page - 1 : 0)}
            disabled={page === 0}
            aria-label="이전 페이지"
          >
            이전
          </button>
        </li>

        {Array.from({ length: totalPages }, (_, index) => (
          <li key={index}>
            <button
              className={`${baseButtonStyles} ${
                page === index ? activeButtonStyles : inactiveButtonStyles
              }`}
              onClick={() => handlePageChange(index)}
              aria-label={`${index + 1}페이지로 이동`}
              aria-current={page === index ? 'page' : undefined}
            >
              {index + 1}
            </button>
          </li>
        ))}

        <li>
          <button
            className={`${baseButtonStyles} ${
              page === totalPages - 1 ? disabledButtonStyles : inactiveButtonStyles
            }`}
            onClick={() => handlePageChange(page === totalPages - 1 ? page : page + 1)}
            disabled={page === totalPages - 1}
            aria-label="다음 페이지"
          >
            다음
          </button>
        </li>
      </ul>
    </div>
  );
};

export default Pagination;