"use client";
import { useState, ChangeEvent, FormEvent, useEffect } from "react";
import { useRouter } from "next/navigation";
import {useUser} from "@/app/component/UserProvider";

interface LoginForm {
    username: string;
    password: string;
}

interface ErrorDetail {
    field: string;
    reason: string;
}

interface ApiError {
    code: string;
    path: string;
    message: string;
    timeStamp: string;
    errorDetails: ErrorDetail[];
}

function LoginForm() {
    const [formData, setFormData] = useState<LoginForm>({
        username: "",
        password: "",
    });
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [isClient, setIsClient] = useState(false);
    const router = useRouter();
    const {setUsername} = useUser();

    // 클라이언트에서만 렌더링되도록 설정
    useEffect(() => {
        setIsClient(true);
    }, []);

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setErrors({});

        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/login`, {
                method: "POST",
                credentials: 'include',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                const errorData: ApiError = await response.json();

                // 서버에서 보낸 error 메시지를 form에 설정
                setErrors({
                    form: errorData.message || "알 수 없는 오류가 발생했습니다.",
                });
                return;
            }

            // 로그인 성공시 username localStorage 에 저장
            const responseData = await response.json();
            if (responseData.success) {
                localStorage.setItem('username', responseData.data.username);
                setUsername(responseData.data.username);
            }

            // 로그인 성공 후 메인페이지로 리다이렉트
            console.log("로그인 성공!");
            router.push("/"); // 메인 페이지로 리다이렉트
        } catch (error) {
            // 서버 통신 중 오류가 발생했을 때
            setErrors({
                form: "서버와의 통신 중 오류가 발생했습니다.",
            });
            console.error(error);
        }
    };

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    // 클라이언트에서만 렌더링되도록 처리
    if (!isClient) return null;

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
            <div className="sm:mx-auto sm:w-full sm:max-w-md">
                <h2 className="text-center text-3xl font-extrabold text-gray-900">
                    로그인
                </h2>
            </div>

            <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
                <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
                    {errors.form && (
                        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-md">
                            <p className="text-red-600 text-sm">{errors.form}</p>
                        </div>
                    )}

                    <form className="space-y-6" onSubmit={handleSubmit}>
                        <div>
                            <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                                이메일
                            </label>
                            <input
                                type="email"
                                id="username"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-black"
                                placeholder="example@email.com"
                            />
                            {errors.username && (
                                <p className="mt-1 text-sm text-red-600">{errors.username}</p>
                            )}
                        </div>

                        <div>
                            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                                비밀번호
                            </label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-black"
                                placeholder="비밀번호 입력"
                            />
                            {errors.password && (
                                <p className="mt-1 text-sm text-red-600">{errors.password}</p>
                            )}
                        </div>

                        <button
                            type="submit"
                            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                        >
                            로그인
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default LoginForm;
