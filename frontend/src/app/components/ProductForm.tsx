import { useState, useEffect } from "react";

interface ProductFormProps {
    initialData?: {
        name: string;
        content: string;
        price: string; // 모든 필드는 string 타입으로 설정
        imgUrl: string;
        quantity: string;
    };
    onSubmit: (formData: {
        name: string;
        content: string;
        price: number;
        imgUrl: string;
        quantity: number;
    }) => Promise<void>; // Promise를 반환하도록 수정
}

export default function ProductForm({ initialData, onSubmit }: ProductFormProps) {
    const [formData, setFormData] = useState({
        name: initialData?.name || "",
        content: initialData?.content || "",
        price: initialData?.price || "",
        imgUrl: initialData?.imgUrl || "",
        quantity: initialData?.quantity || "",
    });

    const [message, setMessage] = useState("");

    useEffect(() => {
        if (initialData) {
            setFormData(initialData);
        }
    }, [initialData]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await onSubmit({
                ...formData,
                price: Number(formData.price), // 숫자로 변환
                quantity: Number(formData.quantity), // 숫자로 변환
            });
            setMessage("작업이 성공적으로 완료되었습니다.");
            setFormData({ name: "", content: "", price: "", imgUrl: "", quantity: "" });
        } catch (error) {
            console.error(error);
            setMessage("서버와의 통신 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="container mx-auto px-4 py-8 text-black">
            <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-lg p-6 space-y-4 max-w-lg mx-auto">
                <div>
                    <label htmlFor="name" className="block text-sm font-medium mb-1">상품명</label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                        className="w-full border rounded-lg px-4 py-2"
                    />
                </div>
                <div>
                    <label htmlFor="content" className="block text-sm font-medium mb-1">설명</label>
                    <textarea
                        id="content"
                        name="content"
                        value={formData.content}
                        onChange={handleChange}
                        required
                        className="w-full border rounded-lg px-4 py-2"
                        rows={4}
                    />
                </div>
                <div>
                    <label htmlFor="price" className="block text-sm font-medium mb-1">가격</label>
                    <input
                        type="number" // input 타입은 text로 유지
                        id="price"
                        name="price"
                        value={formData.price}
                        onChange={handleChange}
                        required
                        className="w-full border rounded-lg px-4 py-2"
                    />
                </div>
                <div>
                    <label htmlFor="imgUrl" className="block text-sm font-medium mb-1">이미지 URL</label>
                    <input
                        type="url"
                        id="imgUrl"
                        name="imgUrl"
                        value={formData.imgUrl}
                        onChange={handleChange}
                        required
                        className="w-full border rounded-lg px-4 py-2"
                    />
                </div>
                <div>
                    <label htmlFor="quantity" className="block text-sm font-medium mb-1">수량</label>
                    <input
                        type="number" // input 타입은 text로 유지
                        id="quantity"
                        name="quantity"
                        value={formData.quantity}
                        onChange={handleChange}
                        required
                        className="w-full border rounded-lg px-4 py-2"
                    />
                </div>
                <button
                    type="submit"
                    className="w-full bg-blue-500 text-white px-6 py-3 rounded-lg shadow hover:bg-blue-600 transition"
                >
                    제출하기
                </button>
            </form>
            {message && <p className="mt-4 text-center text-red-500">{message}</p>}
        </div>
    );
}
