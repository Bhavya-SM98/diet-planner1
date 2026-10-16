import React, { useState, useEffect } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { User, Scale } from 'lucide-react';

export default function Profile() {
  const { user, refreshUser } = useAuth();
  const [form, setForm] = useState({ age: '', gender: 'male', weight: '', height: '', goal: 'maintenance', activityLevel: 'moderate', dietaryPreference: 'non_veg' });
  const [weight, setWeight] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (user) {
      setForm({
        age: user.age || '',
        gender: user.gender || 'male',
        weight: user.weight || '',
        height: user.height || '',
        goal: user.goal || 'maintenance',
        activityLevel: user.activityLevel || 'moderate',
        dietaryPreference: user.dietaryPreference || 'non_veg',
      });
    }
  }, [user]);

  const saveProfile = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await axios.put('/api/user/profile', { ...form, age: Number(form.age), weight: Number(form.weight), height: Number(form.height) });
      await refreshUser();
      toast.success('Profile updated! Calorie target recalculated.');
    } catch { toast.error('Failed to save'); }
    finally { setSaving(false); }
  };

  const logWeight = async () => {
    if (!weight) return;
    try {
      await axios.post('/api/user/weight', { weight: Number(weight) });
      toast.success('Weight logged!');
      setWeight('');
      await refreshUser();
    } catch { toast.error('Failed to log weight'); }
  };

  const Field = ({ label, name, type = 'number', min, placeholder }) => (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
      <input type={type} value={form[name]} onChange={e => setForm({ ...form, [name]: e.target.value })}
        min={min} placeholder={placeholder}
        className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-green-400 text-sm transition" />
    </div>
  );

  const Select = ({ label, name, options }) => (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
      <select value={form[name]} onChange={e => setForm({ ...form, [name]: e.target.value })}
        className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-green-400 text-sm transition bg-white">
        {options.map(([val, label]) => <option key={val} value={val}>{label}</option>)}
      </select>
    </div>
  );

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <h1 className="text-2xl font-bold text-gray-800 flex items-center gap-2"><User size={24} className="text-green-500" /> My Profile</h1>

      {/* Daily Target Badge */}
      {user?.dailyCalorieTarget && (
        <div className="bg-gradient-to-r from-green-500 to-emerald-600 rounded-2xl p-5 text-white flex items-center justify-between">
          <div>
            <p className="text-green-100 text-sm">Your Daily Calorie Target</p>
            <p className="text-3xl font-bold">{user.dailyCalorieTarget} <span className="text-lg font-normal">kcal</span></p>
          </div>
          <div className="text-4xl">🎯</div>
        </div>
      )}

      {/* Profile Form */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h2 className="font-semibold text-gray-800 mb-5">Personal Information</h2>
        <form onSubmit={saveProfile} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Age (years)" name="age" min={1} placeholder="25" />
            <Select label="Gender" name="gender" options={[['male','Male'],['female','Female'],['other','Other']]} />
            <Field label="Weight (kg)" name="weight" placeholder="70" />
            <Field label="Height (cm)" name="height" placeholder="175" />
          </div>
          <Select label="Fitness Goal" name="goal" options={[['weight_loss','🔥 Weight Loss'],['muscle_gain','💪 Muscle Gain'],['maintenance','⚖️ Maintenance']]} />
          <Select label="Activity Level" name="activityLevel" options={[
            ['sedentary','🪑 Sedentary (desk job)'],
            ['light','🚶 Light (1-3 days/week)'],
            ['moderate','🏃 Moderate (3-5 days/week)'],
            ['active','🏋️ Active (6-7 days/week)'],
            ['very_active','⚡ Very Active (athlete)'],
          ]} />
          <Select label="Dietary Preference" name="dietaryPreference" options={[['veg','🌱 Vegetarian'],['non_veg','🍗 Non-Vegetarian'],['vegan','🌿 Vegan']]} />
          <button type="submit" disabled={saving}
            className="w-full py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl hover:from-green-600 hover:to-emerald-700 transition-all shadow-md disabled:opacity-60">
            {saving ? 'Saving...' : 'Save Profile & Update Calorie Target'}
          </button>
        </form>
      </div>

      {/* Weight Logger */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h2 className="font-semibold text-gray-800 mb-4 flex items-center gap-2"><Scale size={18} className="text-green-500" /> Log Today's Weight</h2>
        <div className="flex gap-3">
          <input type="number" value={weight} onChange={e => setWeight(e.target.value)}
            placeholder="Enter weight in kg" step="0.1"
            className="flex-1 px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-green-400 text-sm transition" />
          <button onClick={logWeight}
            className="px-6 py-2.5 bg-green-500 text-white font-medium rounded-xl hover:bg-green-600 transition">
            Log
          </button>
        </div>
        {user?.weight && <p className="text-xs text-gray-400 mt-2">Current weight: {user.weight} kg</p>}
      </div>
    </div>
  );
}
