import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
    images: {
    domains: ['imgur.com', 'i.imgur.com', 'images.unsplash.com'], // imgur 이미지를 허용하기 위한 설정
  },
};

export default nextConfig;
