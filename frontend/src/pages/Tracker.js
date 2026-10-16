import React, { useState, useEffect } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import { Trash2, ChevronLeft, ChevronRight, Flame, Beef, Wheat, Droplets } from 'lucide-react';

const MEALS = ['breakfast', 'lunch', 'dinner', 'snack'];

export default function Tracker() {
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [summary, setSummary] = useState(null);

  useEffect(() => { fetchSummary(); }, [date]);

  const fetchSummary = async () => {
    try {
      const res = await axios.get(`/api/food/daily?date=${date}`);
      setSummary(res.data);
    } catch {}
  };

  const deleteLog = async (id) => {
    try {
      await axios.delete(`/api/food/${id}`);
      toast.success('Removed');
      fetchSummary();
    } catch { toast.error('Failed to remove'); }
  };

  const changeDate = (days) => {
    const d = new Date(date);
    d.setDate(d.getDate() + days);
    setDate(d.toISOString().split('T')[0]);
  };

  const logs = summary?.logs || [];
  const byMeal = MEALS.reduce((acc, m) => ({ ...acc, [m]: logs.filter(l => l.mealType === m) }), {});

  const mealEmojis = { breakfast: '🌅', lunch: '☀️', dinner: '🌙', snack: '🍎' };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Food Tracker</h1>

      {/* Date Selector */}
      <div className="flex items-center justify-between bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
        <button onClick={() => changeDate(-1)} className="p-2 hover:bg-gray-100 rounded-xl transition"><ChevronLeft size={20} /></button>
        <div className="text-center">
          <p className="font-semibold text-gray-800">{new Date(date).toLocaleDateString('en-IN', { weekday: 'long', month: 'long', day: 'numeric' })}</p>
          <p className="text-xs text-gray-400">{date === new Date().toISOString().split('T')[0] ? 'Today' : date}</p>
        </div>
        <button onClick={() => changeDate(1)} className="p-2 hover:bg-gray-100 rounded-xl transition"><ChevronRight size={20} /></button>
      </div>

      {/* Daily Summary Bar */}
      {summary && (
        <div className="grid grid-cols-4 gap-3">
          {[
            { label: 'Calories', value: Math.round(summary.totalCalories || 0), unit: 'kcal', icon: Flame, color: 'text-orange-500' },
            { label: 'Protein', value: Math.round(summary.totalProtein || 0), unit: 'g', icon: Beef, color: 'text-green-500' },
            { label: 'Carbs', value: Math.round(summary.totalCarbs || 0), unit: 'g', icon: Wheat, color: 'text-blue-500' },
            { label: 'Fats', value: Math.round(summary.totalFats || 0), unit: 'g', icon: Droplets, color: 'text-orange-400' },
          ].map(({ label, value, unit, icon: Icon, color }) => (
            <div key={label} className="bg-white rounded-xl p-3 shadow-sm border border-gray-100 text-center">
              <Icon size={16} className={`${color} mx-auto mb-1`} />
              <p className="font-bold text-gray-800 text-sm">{value}<span className="text-xs font-normal text-gray-400">{unit}</span></p>
              <p className="text-xs text-gray-400">{label}</p>
            </div>
          ))}
        </div>
      )}

      {/* Meals */}
      {MEALS.map(meal => (
        <div key={meal} className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div className="px-5 py-4 border-b border-gray-50 flex items-center justify-between">
            <h3 className="font-semibold text-gray-800 capitalize flex items-center gap-2">
              {mealEmojis[meal]} {meal}
            </h3>
            <span className="text-xs text-gray-400">
              {Math.round(byMeal[meal].reduce((s, l) => s + (l.calories || 0), 0))} kcal
            </span>
          </div>
          {byMeal[meal].length === 0 ? (
            <div className="px-5 py-6 text-center text-gray-400 text-sm">No food logged</div>
          ) : (
            <div className="divide-y divide-gray-50">
              {byMeal[meal].map(log => (
                <div key={log.id} className="px-5 py-3 flex items-center justify-between hover:bg-gray-50 transition">
                  <div className="flex items-center gap-3">
                    {log.imageUrl && <img src={log.imageUrl} alt="" className="w-10 h-10 rounded-lg object-cover" />}
                    <div>
                      <p className="font-medium text-gray-800 text-sm">{log.foodName}</p>
                      <p className="text-xs text-gray-400">{log.quantity} • P:{Math.round(log.protein)}g C:{Math.round(log.carbs)}g F:{Math.round(log.fats)}g</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-sm font-semibold text-orange-500">{Math.round(log.calories)} kcal</span>
                    <button onClick={() => deleteLog(log.id)} className="p-1.5 hover:bg-red-50 rounded-lg text-red-400 transition">
                      <Trash2 size={15} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
