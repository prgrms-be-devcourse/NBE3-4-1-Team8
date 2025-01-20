import Link from 'next/link';
import Image from 'next/image';

interface ProductCardProps {
  product: Product;
}

export const ProductCard = ({ product }: ProductCardProps) => (
  <li className="flex flex-col p-4 border rounded-lg hover:shadow-md transition-shadow">
    <div className="w-full relative h-48">
      <Image
        src={product.imgUrl}
        alt={product.name}
        fill
        className="object-cover rounded"
      />
    </div>
    <div className="flex flex-col space-y-2 mt-4">
      <Link
        href={`/product/${product.Id}`}
        className="font-semibold text-black hover:text-gray-700 transition-colors text-lg"
      >
        {product.name}
      </Link>
      <p className="text-black font-medium">
        {product.price.toLocaleString()}원
      </p>
    </div>
  </li>
);