interface Product {
  Id: number;  // 대문자 I로 변경
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
      pageNumber: number;
      pageSize: number;
      offset: number;
    };
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
  };
  success: boolean;
}