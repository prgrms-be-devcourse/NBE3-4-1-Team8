"use client";

import Image from "next/image";
import { useState } from "react";
import Link from "next/link";
import ProductList from "@/app/components/ProductList";

export default function Home() {


  return (
    <div className="container mx-auto px-4 py-8 text-black">
      <h1 className="text-3xl font-bold text-center mb-8">Grids & Circle</h1>

      <ProductList />
    </div>
  );
}