import React, { useState, useRef } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import { Camera, Upload, Zap, Flame, Beef, Wheat, Droplets, Leaf } from 'lucide-react';

const MEALS = ['breakfast', 'lunch', 'dinner', 'snack'];

export default function FoodAnalyzer() {
  const [image, setImage] = useState(null);
  const [preview, setPreview] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [mealType, setMealType] = useState('lunch');
  const fileRef = useRef();

  const handleFile = (file) => {
    if (!file) return;
    setImage(file);
    setPreview(URL.createObjectURL(file));
    setResult(null);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    handleFile(e.dataTransfer.files[0]);
  };

  const analyze = async () => {
    if (!image) return toast.error('Please select a food image first');
    setLoading(true);
    try {
      const fd = new FormData();
      fd.append('image', image);
      fd.append('mealType', mealType);
      fd.append('logDate', new Date().toISOString().split('T')[0]);
      const res = await axios.post('/api/food/analyze', fd, { headers: { 'Content-Type': 'multipart/form-data' } });
      setResult(res.data);
      toast.success('Food analyzed & logged! 🎉');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Analysis failed');
    } finally {
      setLoading(false);
    }
  };

  const NutrientCard = ({ icon: Icon, label, value, unit, color, bg }) => (
    <div className={`${bg} rounded-xl p-4 flex items-center gap-3`}>
      <Icon size={20} className={color} />
      <div>
        <p className="text-xs text-gray-500">{label}</p>
        <p className="font-bold text-gray-800">{Math.round(value)}<span className="text-xs font-normal ml-1">{unit}</span></p>
      </div>
    </div>
  );

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800 flex items-center gap-2"><Camera size={24} className="text-green-500" /> Food Analyzer</h1>
        <p className="text-gray-500 text-sm mt-1">Upload a photo of your food and get instant nutritional analysis powered by AI</p>
      </div>

      {/* Meal Type */}
      <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
        <p className="text-sm font-medium text-gray-700 mb-3">Select Meal Type</p>
        <div className="flex gap-2 flex-wrap">
          {MEALS.map(m => (
            <button key={m} onClick={() => setMealType(m)}
              className={`px-4 py-2 rounded-full text-sm font-medium transition-all capitalize ${mealType === m ? 'bg-green-500 text-white shadow-sm' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>
              {m}
            </button>
          ))}
        </div>
      </div>

      {/* Upload Zone */}
      <div
        onClick={() => fileRef.current?.click()}
        onDrop={handleDrop}
        onDragOver={e => e.preventDefault()}
        className="bg-white rounded-2xl border-2 border-dashed border-gray-200 hover:border-green-400 transition-colors cursor-pointer overflow-hidden shadow-sm">
        {preview ? (
          <div className="relative">
            <img src={preview} alt="food" className="w-full h-72 object-cover" />
            <div className="absolute inset-0 bg-black/20 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
              <p className="text-white font-medium">Click to change image</p>
            </div>
          </div>
        ) : (
          <div className="py-16 flex flex-col items-center gap-3 text-gray-400">
            <div className="w-16 h-16 bg-green-50 rounded-full flex items-center justify-center">
              <Upload size={28} className="text-green-400" />
            </div>
            <div className="text-center">
              <p className="font-medium text-gray-600">Drop your food photo here</p>
              <p className="text-sm">or click to browse</p>
            </div>
          </div>
        )}
        <input ref={fileRef} type="file" accept="image/*" className="hidden" onChange={e => handleFile(e.target.files[0])} />
      </div>

      <button onClick={analyze} disabled={!image || loading}
        className="w-full py-4 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-2xl hover:from-green-600 hover:to-emerald-700 transition-all shadow-md disabled:opacity-50 flex items-center justify-center gap-2">
        {loading ? (
          <><div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" /> Analyzing with AI...</>
        ) : (
          <><Zap size={20} /> Analyze Food</>
        )}
      </button>

      {/* Results */}
      {result && (
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 space-y-5 animate-fade-in">
          <div className="flex items-start justify-between">
            <div>
              <h2 className="text-xl font-bold text-gray-800">{result.foodName}</h2>
              <p className="text-gray-400 text-sm">{result.quantity}</p>
            </div>
            <div className="text-right">
              <div className="text-2xl font-bold text-green-600">{Math.round(result.calories)}</div>
              <div className="text-xs text-gray-400">calories</div>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <NutrientCard icon={Flame} label="Calories" value={result.calories} unit="kcal" color="text-orange-500" bg="bg-orange-50" />
            <NutrientCard icon={Beef} label="Protein" value={result.protein} unit="g" color="text-green-600" bg="bg-green-50" />
            <NutrientCard icon={Wheat} label="Carbohydrates" value={result.carbs} unit="g" color="text-blue-500" bg="bg-blue-50" />
            <NutrientCard icon={Droplets} label="Fats" value={result.fats} unit="g" color="text-orange-400" bg="bg-orange-50" />
            <NutrientCard icon={Leaf} label="Fiber" value={result.fiber} unit="g" color="text-green-500" bg="bg-green-50" />
            <div className="bg-purple-50 rounded-xl p-4 flex items-center gap-3">
              <span className="text-2xl">⭐</span>
              <div>
                <p className="text-xs text-gray-500">Health Score</p>
                <p className="font-bold text-gray-800">{result.healthScore}/10</p>
              </div>
            </div>
          </div>

          {result.tips && (
            <div className="bg-green-50 rounded-xl p-4 border border-green-100">
              <p className="text-sm font-medium text-green-800">💡 Nutrition Tip</p>
              <p className="text-sm text-green-700 mt-1">{result.tips}</p>
            </div>
          )}

          <div className="bg-gray-50 rounded-xl p-3 text-center">
            <p className="text-xs text-gray-500">✅ Logged to your <span className="font-medium capitalize">{mealType}</span> for today</p>
          </div>
        </div>
      )}
    </div>
  );
}
