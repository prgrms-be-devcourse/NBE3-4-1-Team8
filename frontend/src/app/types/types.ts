interface Product {
  id: number;
  name: string;
  content: string;
  price: number;
  imgUrl: string;  // image → imgUrl로 변경
}

interface ApiResponse<T> {
  timestamp: string;
  message: string | null;
  data: {
    content: T[];
    pageable: {
      pageNumber: number | null;
      pageSize: number | null;
      offset: number | null;
    } | null;
    totalElements: number | null;
    totalPages: number | null;
    number: number | null;
    size: number | null;
  };
  success: boolean;
}