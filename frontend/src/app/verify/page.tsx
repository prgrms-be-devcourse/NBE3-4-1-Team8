'use client';

import {useEffect, useState} from 'react';
import {useSearchParams, useRouter} from 'next/navigation';
import {error} from "next/dist/build/output/log";

export default function VerifyPage() {
    const searchParams = useSearchParams();
    const router = useRouter();
    const [verifying, setVerifying] = useState(true);
    const [status, setStatus] = useState<'verifying' | 'success' | 'error'>('verifying');
    const [message, setMessage] = useState('이메일 인증을 진행중입니다...');
    const [resendLoading, setResendLoading] = useState(false);
    const [resendCooldown, setResendCooldown] = useState(0);

    const username = searchParams.get('username');

    useEffect(() => {
        if (resendCooldown > 0) {
            const timer = setTimeout(() => {
                setResendCooldown(prev => prev - 1);
            }, 1000);
            return () => clearTimeout(timer);
        }
    }, [resendCooldown]);

    const handleResendEmail = async () => {
        if (resendCooldown > 0 || !username || resendLoading) return;

        try {
            setResendLoading(true);
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/code`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: username,
                    verifyType: 'SIGNUP'
                })
            });

            const data = await response.json();

            if (!response.ok) {
                if (data.code === '400-1') {
                    router.push('/login');
                    alert('이미 인증된 회원 입니다.')
                } else {
                    throw new Error('이메일 재전송 실패했습니다.');
                }
            }

            if (!data.success) {
                throw new Error(data.message || '이메일 재전송에 실패했습니다.');
            }

            setResendCooldown(180); // 3분 쿨다운
        } catch (error) {
            alert(error instanceof Error ? error.message : '이메일 재전송에 실패했습니다.');
        } finally {
            setResendLoading(false);
        }
    };

    useEffect(() => {
        const verifyEmail = async () => {
            try {
                const certificationCode = searchParams.get('certificationCode');
                const verifyType = searchParams.get('verifyType');

                if (!username || !certificationCode || !verifyType) {
                    throw new Error('필수 파라미터가 누락되었습니다.');
                }

                const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/verify`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        username,
                        certificationCode,
                        verifyType
                    })
                });

                const data = await response.json();

                if (!response.ok) {
                    if (data.code === '400-1') {
                        router.push('/login');
                        alert('이미 인증된 회원 입니다.');
                    } else {
                        throw new Error('인증에 실패했습니다.');
                    }
                }


                if (!data.success) {
                    throw new Error(data.message || '인증에 실패했습니다.');
                }

                setStatus('success');
                setMessage('이메일 인증이 완료되었습니다.');

                setTimeout(() => {
                    router.push('/login');
                }, 3000);

            } catch (error) {
                setStatus('error');
                setMessage(error instanceof Error ? error.message : '인증에 실패했습니다.');
            } finally {
                setVerifying(false);
            }
        };

        verifyEmail();
    }, [searchParams, router, username]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow-lg">
                <div className="text-center">
                    <h2 className="mt-6 text-3xl font-extrabold text-gray-900">
                        이메일 인증
                    </h2>
                    <div className="mt-4">
                        {status === 'verifying' && (
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mx-auto"></div>
                        )}
                        <p className={`mt-4 text-lg ${
                            status === 'success' ? 'text-green-600' :
                                status === 'error' ? 'text-red-600' :
                                    'text-gray-600'
                        }`}>
                            {message}
                        </p>

                        {(status === 'error' || status === 'verifying') && (
                            <div className="mt-6 space-y-4">
                                <p className="text-sm text-gray-600">
                                    이메일을 받지 못하셨나요?
                                </p>
                                <button
                                    onClick={handleResendEmail}
                                    disabled={resendLoading || resendCooldown > 0}
                                    className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-gray-800 hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    {resendLoading ? '전송중...' :
                                        resendCooldown > 0 ? `재전송 대기 (${resendCooldown}초)` :
                                            '인증 메일 재발송'}
                                </button>
                                <p className="text-xs text-gray-500">
                                    {username} 주소로 인증 메일이 발송됩니다.
                                </p>
                            </div>
                        )}

                        {status === 'success' && (
                            <p className="mt-2 text-sm text-gray-500">
                                잠시 후 로그인 페이지로 이동합니다...
                            </p>
                        )}
                        {status === 'error' && (
                            <button
                                onClick={() => router.push('/login')}
                                className="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-gray-800 hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
                            >
                                로그인 페이지로 이동
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}