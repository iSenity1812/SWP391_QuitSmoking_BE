import { useEffect, useState } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { achievementService } from '@/services/achievementService';
import { Lock } from 'lucide-react';
import type { Achievement } from '@/types/achievement';
import { Link } from "react-router-dom";
import MedalIcon from '@/components/ui/medalIcon';

export default function UserAchievements() {
  const { user } = useAuth();
  const memberId = user?.userId || '';
  const [allAchievements, setAllAchievements] = useState<Achievement[]>([]);
  const [unlockedIds, setUnlockedIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!memberId) return;
    setLoading(true);
    Promise.all([
      achievementService.getAllAchievements(),
      achievementService.getUnlockedAchievements(memberId),
    ])
      .then(([all, unlocked]) => {
        setAllAchievements(all);
        setUnlockedIds(unlocked.map((a: Achievement) => a.achievementId));
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [memberId]);

  if (loading) return <div>Đang tải thành tựu...</div>;
  if (error) return <div>Lỗi: {error}</div>;

  return (
    <div>
      <h2 className="text-xl font-bold mb-4 flex items-center justify-between">
        Thành tựu của bạn
        <Link to="/achievements" className="text-blue-600 hover:underline text-sm ml-2">Xem tất cả thành tựu</Link>
      </h2>
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        {allAchievements.length === 0 && <div>Chưa có thành tựu nào.</div>}
        {allAchievements.slice(0, 5).map((ach) => {
          const unlocked = unlockedIds.includes(ach.achievementId);
          return (
            <div
              key={ach.achievementId}
              className={`relative flex flex-col items-center p-3 rounded-lg border shadow transition-all duration-200
                ${unlocked ? 'bg-white' : 'bg-gray-100 opacity-60 grayscale'}`}
            >
              {ach.iconUrl ? (
                <img
                  src={ach.iconUrl}
                  alt={ach.name}
                  className={`w-14 h-14 mb-2 ${unlocked ? '' : 'opacity-40 grayscale'}`}
                />
              ) : (
                <MedalIcon size={56} locked={!unlocked} />
              )}
              <div className="font-semibold text-center text-sm">{ach.name}</div>
              <div className="text-xs text-gray-500 text-center mb-1">{ach.description}</div>
              {!unlocked && (
                <span className="absolute top-2 right-2 text-xl text-gray-400 bg-white rounded-full p-1"><Lock size={18} /></span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
} 