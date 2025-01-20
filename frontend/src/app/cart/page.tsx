"use client";
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

interface CartItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    productPrice: number;
    totalPrice: number;
    productImgUrl: string;
}

const Page = () => {
    const [cartItems, setCartItems] = useState<CartItem[]>([]);
    const navigate = useRouter();

    useEffect(() => {
        const fetchCartData = async () => {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/carts`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            const data = await response.json();
            setCartItems(data.data || []);
        };

        fetchCartData();
    }, []);

    const updateQuantity = async (productId: number, newQuantity: number) => {
        await fetch(`${process.env.NEXT_PUBLIC_API_URL}/carts`, {
            method: 'PATCH',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ productId: productId, quantity: newQuantity }),
        });

        setCartItems((prevItems) =>
            prevItems.map((item) =>
                item.productId === productId ? { ...item, quantity: newQuantity, totalPrice: item.productPrice * newQuantity } : item
            )
        );
    };

    // 장바구니 아이템 삭제 요청
    const deleteCartItem = async (productId: number) => {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/carts`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ productId: productId }),
        });

        if (response.ok) {
            setCartItems((prevItems) => prevItems.filter((item) => item.productId !== productId));
        } else {
            console.error('Failed to delete cart item');
        }
    };

    const handleOrderClick = () => {
        navigate.push('/order');
    };

    return (
        <div className="bg-gray-100 min-h-screen py-8">
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold text-center mb-6 text-black">장바구니</h1>
                <div className="flex justify-between gap-6">
                    <div className="w-full md:w-2/3 bg-white p-4 rounded-md shadow-md">
                        {cartItems.length > 0 ? (
                            <div className="space-y-4">
                                {cartItems.map((item) => (
                                    <div key={item.id} className="flex items-center p-4 border-b border-gray-200">
                                        <img src={item.productImgUrl} alt={item.productName} className="w-24 h-24 object-cover rounded-md" />
                                        <div className="ml-4 flex-grow">
                                            <h3 className="text-xl font-semibold text-black">{item.productName}</h3>
                                            <p className="text-gray-500 text-black">{`가격: ${item.productPrice.toLocaleString()} 원`}</p>
                                        </div>
                                        <div className="flex items-center space-x-4">
                                            <button
                                                onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                                                className="w-8 h-8 bg-black text-white rounded-full flex items-center justify-center disabled:bg-gray-300 disabled:cursor-not-allowed"
                                                disabled={item.quantity <= 1}
                                            >
                                                -
                                            </button>
                                            <span className="text-lg text-black">{item.quantity}</span>
                                            <button
                                                onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                                                className="w-8 h-8 bg-black text-white rounded-full flex items-center justify-center"
                                            >
                                                +
                                            </button>
                                            <p className="font-semibold text-black">{item.totalPrice.toLocaleString()} 원</p>
                                            <button
                                                onClick={() => deleteCartItem(item.productId)} // 삭제 버튼 클릭 시 삭제 요청
                                                className="ml-4 text-red-500 hover:text-red-700"
                                            >
                                                삭제
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p className="text-center text-xl text-black">장바구니에 상품이 없습니다.</p>
                        )}
                    </div>

                    <div className="w-full md:w-1/3 bg-white p-4 rounded-md shadow-md">
                        <h5 className="text-xl font-bold mb-4 text-black">주문 요약</h5>
                        <div className="space-y-2">
                            {cartItems.map((item) => (
                                <div key={item.id} className="flex justify-between text-black">
                                    <span>{item.productName}</span>
                                    <span>{item.quantity}개</span>
                                </div>
                            ))}
                        </div>
                        <hr className="my-4" />
                        <div className="flex justify-between text-xl font-semibold text-black">
                            <span>총금액</span>
                            <span>{cartItems.reduce((total, item) => total + item.totalPrice, 0).toLocaleString()} 원</span>
                        </div>
                        <button
                            onClick={handleOrderClick}
                            className="w-full mt-4 py-2 bg-black text-white rounded-md hover:bg-gray-800"
                        >
                            주문하기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Page;
