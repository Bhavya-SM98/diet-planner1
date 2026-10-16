import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Doughnut, Line } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, PointElement, LineElement, Filler } from 'chart.js';
import { useAuth } from '../context/AuthContext';
import { Flame, Beef, Wheat, Droplets, TrendingUp, Calendar } from 'lucide-react';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, PointElement, LineElement, Filler);

export default function Dashboard() {
  const { user } = useAuth();
  const [summary, setSummary] = useState(null);
  const [weightHistory, setWeightHistory] = useState([]);
  const today = new Date().toISOString().split('T')[0];

  useEffect(() => {
    axios.get(`/api/food/daily?date=${today}`).then(r => setSummary(r.data)).catch(() => {});
    axios.get('/api/user/weight').then(r => setWeightHistory(r.data.reverse())).catch(() => {});
  }, []);

  const macroData = summary ? {
    labels: ['Protein', 'Carbs', 'Fats'],
    datasets: [{
      data: [summary.totalProtein || 0, summary.totalCarbs || 0, summary.totalFats || 0],
      backgroundColor: ['#22c55e', '#3b82f6', '#f97316'],
      borderWidth: 0,
    }]
  } : null;

  const weightData = {
    labels: weightHistory.map(w => w.logDate),
    datasets: [{
      label: 'Weight (kg)',
      data: weightHistory.map(w => w.weight),
      borderColor: '#22c55e',
      backgroundColor: 'rgba(34,197,94,0.1)',
      fill: true,
      tension: 0.4,
    }]
  };

  const calTarget = user?.dailyCalorieTarget || 2000;
  const calConsumed = summary?.totalCalories || 0;
  const calPct = Math.min(100, Math.round((calConsumed / calTarget) * 100));

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">Good {new Date().getHours() < 12 ? 'Morning' : new Date().getHours() < 17 ? 'Afternoon' : 'Evening'}, {user?.name?.split(' ')[0]}! 👋</h1>
        <p className="text-gray-500 text-sm mt-1">{new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
      </div>

      {/* Calorie Progress */}
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-800 flex items-center gap-2"><Flame size={18} className="text-orange-500" /> Today's Calories</h2>
          <span className="text-sm text-gray-400">{calConsumed} / {calTarget} kcal</span>
        </div>
        <div className="w-full bg-gray-100 rounded-full h-4">
          <div className={`h-4 rounded-full transition-all duration-500 ${calPct >= 100 ? 'bg-red-500' : calPct >= 80 ? 'bg-orange-500' : 'bg-green-500'}`}
            style={{ width: `${calPct}%` }} />
        </div>
        <div className="flex justify-between mt-2 text-xs text-gray-400">
          <span>{calPct}% consumed</span>
          <span>{Math.max(0, calTarget - calConsumed)} remaining</span>
        </div>
      </div>

      {/* Macro Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'Calories', value: `${Math.round(calConsumed)}`, unit: 'kcal', icon: Flame, color: 'text-orange-500', bg: 'bg-orange-50' },
          { label: 'Protein', value: `${Math.round(summary?.totalProtein || 0)}`, unit: 'g', icon: Beef, color: 'text-green-500', bg: 'bg-green-50' },
          { label: 'Carbs', value: `${Math.round(summary?.totalCarbs || 0)}`, unit: 'g', icon: Wheat, color: 'text-blue-500', bg: 'bg-blue-50' },
          { label: 'Fats', value: `${Math.round(summary?.totalFats || 0)}`, unit: 'g', icon: Droplets, color: 'text-orange-400', bg: 'bg-orange-50' },
        ].map(({ label, value, unit, icon: Icon, color, bg }) => (
          <div key={label} className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
            <div className={`w-10 h-10 ${bg} rounded-xl flex items-center justify-center mb-3`}>
              <Icon size={18} className={color} />
            </div>
            <p className="text-2xl font-bold text-gray-800">{value}<span className="text-sm font-normal text-gray-400 ml-1">{unit}</span></p>
            <p className="text-sm text-gray-500 mt-1">{label}</p>
          </div>
        ))}
      </div>

      {/* Charts */}
      <div className="grid md:grid-cols-2 gap-6">
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="font-semibold text-gray-800 mb-4">Macro Distribution</h3>
          {macroData && (macroData.datasets[0].data.some(v => v > 0)) ? (
            <div className="h-48"><Doughnut data={macroData} options={{ maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }} /></div>
          ) : (
            <div className="h-48 flex items-center justify-center text-gray-400 text-sm">No food logged today</div>
          )}
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <h3 className="font-semibold text-gray-800 mb-4 flex items-center gap-2"><TrendingUp size={16} /> Weight Trend</h3>
          {weightHistory.length > 1 ? (
            <div className="h-48"><Line data={weightData} options={{ maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { y: { grid: { color: '#f3f4f6' } }, x: { grid: { display: false } } } }} /></div>
          ) : (
            <div className="h-48 flex items-center justify-center text-gray-400 text-sm">Log your weight to see trends</div>
          )}
        </div>
      </div>

      {/* Quick tip */}
      {!user?.dailyCalorieTarget && (
        <div className="bg-gradient-to-r from-green-500 to-emerald-600 rounded-2xl p-5 text-white">
          <p className="font-semibold">Complete your profile! 🎯</p>
          <p className="text-sm text-green-100 mt-1">Add your height, weight, and goals to get personalized calorie targets and AI diet plans.</p>
        </div>
      )}
    </div>
  );
}
