"use client";
import { useState, ChangeEvent, FormEvent } from "react";

interface FormData {
  username: string;
  nickname: string;
  password: string;
  passwordCheck: string;
  city: string;
  district: string;
  country: string;
  detail: string;
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

const SignupForm = () => {
  const [formData, setFormData] = useState<FormData>({
    username: "",
    nickname: "",
    password: "",
    passwordCheck: "",
    city: "",
    district: "",
    country: "",
    detail: ""
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleSubmit = async (e: FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setErrors({});

    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/members/join`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData: ApiError = await response.json();
        const newErrors: Record<string, string> = {};

        errorData.errorDetails.forEach((error) => {
          if (error.field === 'memberSignupForm') {
            newErrors.form = error.reason;
          } else {
            newErrors[error.field] = error.reason;
          }
        });

        setErrors(newErrors);
        return;
      }

      // 성공 시 처리
      console.log('회원가입 성공!');

    } catch (error) {
      setErrors({
        form: '서버와의 통신 중 오류가 발생했습니다.',
      });
    }
  };

  const handleChange = (e: ChangeEvent<HTMLInputElement>): void => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="text-center text-3xl font-extrabold text-gray-900">
          회원가입
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
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="example@email.com"
              />
              {errors.username && (
                <p className="mt-1 text-sm text-red-600">{errors.username}</p>
              )}
            </div>

            <div>
              <label htmlFor="nickname" className="block text-sm font-medium text-gray-700">
                닉네임
              </label>
              <input
                type="text"
                id="nickname"
                name="nickname"
                value={formData.nickname}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="닉네임 입력"
              />
              {errors.nickname && (
                <p className="mt-1 text-sm text-red-600">{errors.nickname}</p>
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
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="비밀번호 입력"
              />
              {errors.password && (
                <p className="mt-1 text-sm text-red-600">{errors.password}</p>
              )}
            </div>

            <div>
              <label htmlFor="passwordCheck" className="block text-sm font-medium text-gray-700">
                비밀번호 확인
              </label>
              <input
                type="password"
                id="passwordCheck"
                name="passwordCheck"
                value={formData.passwordCheck}
                onChange={handleChange}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="비밀번호 재입력"
              />
              {errors.passwordCheck && (
                <p className="mt-1 text-sm text-red-600">{errors.passwordCheck}</p>
              )}
            </div>

            <div className="space-y-4">
              <h3 className="text-lg font-medium text-gray-900">주소 정보</h3>

              <div>
                <label htmlFor="city" className="block text-sm font-medium text-gray-700">
                  시/도
                </label>
                <input
                  type="text"
                  id="city"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="시/도 입력"
                />
                {errors.city && (
                  <p className="mt-1 text-sm text-red-600">{errors.city}</p>
                )}
              </div>

              <div>
                <label htmlFor="district" className="block text-sm font-medium text-gray-700">
                  시/군/구
                </label>
                <input
                  type="text"
                  id="district"
                  name="district"
                  value={formData.district}
                  onChange={handleChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="시/군/구 입력"
                />
                {errors.district && (
                  <p className="mt-1 text-sm text-red-600">{errors.district}</p>
                )}
              </div>

              <div>
                <label htmlFor="country" className="block text-sm font-medium text-gray-700">
                  국가
                </label>
                <input
                  type="text"
                  id="country"
                  name="country"
                  value={formData.country}
                  onChange={handleChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="국가 입력"
                />
                {errors.country && (
                  <p className="mt-1 text-sm text-red-600">{errors.country}</p>
                )}
              </div>

              <div>
                <label htmlFor="detail" className="block text-sm font-medium text-gray-700">
                  상세주소
                </label>
                <input
                  type="text"
                  id="detail"
                  name="detail"
                  value={formData.detail}
                  onChange={handleChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="상세주소 입력"
                />
                {errors.detail && (
                  <p className="mt-1 text-sm text-red-600">{errors.detail}</p>
                )}
              </div>
            </div>

            <button
              type="submit"
              className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              가입하기
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SignupForm;