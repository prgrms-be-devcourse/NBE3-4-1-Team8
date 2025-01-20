interface QuantitySelectorProps {
  quantity: number;
  onUpdate: (change: number) => void;
  disabled?: boolean;
}

export const QuantitySelector = ({ quantity, onUpdate, disabled = false }: QuantitySelectorProps) => {
  return (
    <div>
      <h2 className="font-semibold mb-2">수량</h2>
      <div className="flex items-center space-x-2">
        <button
          onClick={() => onUpdate(-1)}
          disabled={disabled}
          className="px-3 py-1 border border-gray-800 rounded hover:bg-gray-800 hover:text-white transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          -
        </button>
        <span className="w-12 text-center">{quantity}</span>
        <button
          onClick={() => onUpdate(1)}
          disabled={disabled}
          className="px-3 py-1 border border-gray-800 rounded hover:bg-gray-800 hover:text-white transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          +
        </button>
      </div>
    </div>
  );
};