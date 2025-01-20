"use client";
import {ChangeEvent, FormEvent, useEffect, useState} from "react";
import {useRouter} from "next/navigation";
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
    errorDetails: ErrorDetail[] | null;
}

function LoginForm() {
    const [formData, setFormData] = useState<LoginForm>({
        username: "",
        password: "",
    });
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [isClient, setIsClient] = useState(false);
    const [emailNotVerified, setEmailNotVerified] = useState(false); // 이메일 인증 실패 여부
    const [isEmailSent, setIsEmailSent] = useState<boolean>(false); // 이메일 전송 성공 여부
    const [fixedEmail, setFixedEmail] = useState<string | null>(null); // 고정된 이메일
    const router = useRouter();
    const { setUsername } = useUser();

    useEffect(() => {
        setIsClient(true);
    }, []);

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setErrors({});
        setEmailNotVerified(false);  // 로그인 시 이메일 인증 상태 초기화
        setFixedEmail(null); // 로그인 시마다 고정된 이메일 초기화

        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/login`, {
                method: "POST",
                credentials: 'include',
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                const errorData: ApiError = await response.json();

                if (errorData.code === "401-6") {
                    setErrors({
                        form: "이메일 인증을 하지 않았습니다.",
                    });
                    setEmailNotVerified(true);  // 이메일 인증 미완료 상태로 설정
                    setFixedEmail(formData.username); // 로그인 시도한 이메일로 고정
                    return;
                }

                setErrors({
                    form: errorData.message || "알 수 없는 오류가 발생했습니다.",
                });
                return;
            }

            const responseData = await response.json();
            if (responseData.success) {
                localStorage.setItem('username', responseData.data.username);
                setUsername(responseData.data.username);
            }

            console.log("로그인 성공!");
            router.push("/");
        } catch (error) {
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

    const handleResendEmail = async (email: string) => {
        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/code`, {
                method: "POST",
                credentials: 'include',
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    username: fixedEmail, // 고정된 이메일로 전송
                    verifyType: "SIGNUP",
                }),
            });

            if (!response.ok) {
                setErrors({
                    form: "이메일 재전송에 실패했습니다. 다시 시도해주세요.",
                });
                setIsEmailSent(false);  // 실패 시 이메일 전송 성공 상태를 false로 설정
                return;
            }

            setIsEmailSent(true);  // 전송 성공 시 상태 업데이트
        } catch (error) {
            setErrors({
                form: "서버와의 통신 중 오류가 발생했습니다.",
            });
            console.error(error);
            setIsEmailSent(false);  // 실패 시 이메일 전송 성공 상태를 false로 설정
        }
    };

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

                        {emailNotVerified && (
                            <div className="mt-4">
                                <p className="text-sm text-gray-600">이메일 인증이 필요합니다.</p>
                                {isEmailSent ? (
                                    <p className="mt-2 text-sm text-green-600">이메일을 성공적으로 재전송했습니다. 확인해주세요!</p>
                                ) : (
                                    <div>
                                        <button
                                            type="button"
                                            onClick={() => handleResendEmail(formData.username)}
                                            className="mt-2 w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-yellow-500 hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
                                        >
                                            이메일 재전송
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </form>
                </div>
            </div>
        </div>
    );
}

export default LoginForm;
