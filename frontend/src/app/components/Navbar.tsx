"use client";
import Link from 'next/link';
import { useUser } from '@/app/components/UserProvider';

export default function NavBar() {
    const { username, setUsername } = useUser();

    const handleLogout = async () => {
        await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/logout`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        setUsername(''); // Context state update
    };

    return (
        <nav className="bg-gray-100 border-b border-gray-200">
            <div className="container mx-auto flex flex-wrap items-center justify-between p-4">
                <Link href="/" className="text-black text-xl font-semibold">Team8</Link>
                <div className="hidden w-full lg:flex lg:items-center lg:w-auto">
                    <ul className="flex flex-col lg:flex-row lg:space-x-4">
                        {username ? (
                            <>
                                <li className="nav-item">
                                    <span className="block py-2 pr-4 pl-3 text-gray-700 lg:p-0">
                                        Welcome, {username}
                                    </span>
                                </li>
                                <li className="nav-item">
                                    <Link
                                        href="/user/info"
                                        className="block py-2 pr-4 pl-3 text-gray-700 hover:bg-gray-100 lg:hover:bg-transparent lg:border-0 lg:hover:text-blue-700 lg:p-0"
                                    >
                                        내정보
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link href={`/cart`} className="block py-2 pr-4 pl-3 text-gray-700 lg:p-0">
                                        장바구니 보기
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <button
                                        onClick={handleLogout}
                                        className="block py-2 pr-4 pl-3 text-gray-700 hover:bg-gray-100 lg:hover:bg-transparent lg:border-0 lg:hover:text-blue-700 lg:p-0"
                                    >
                                        로그아웃
                                    </button>
                                </li>
                            </>
                        ) : (
                            <>
                                <li className="nav-item">
                                    <Link
                                        href="/login"
                                        className="block py-2 pr-4 pl-3 text-gray-700 hover:bg-gray-100 lg:hover:bg-transparent lg:border-0 lg:hover:text-blue-700 lg:p-0"
                                    >
                                    로그인
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link
                                        href="/signup"
                                        className="block py-2 pr-4 pl-3 text-gray-700 hover:bg-gray-100 lg:hover:bg-transparent lg:border-0 lg:hover:text-blue-700 lg:p-0"
                                    >
                                        회원가입
                                    </Link>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
}