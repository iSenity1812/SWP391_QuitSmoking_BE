import { useEffect, useState } from 'react';
import { achievementService } from '@/services/achievementService';
import type { Achievement, MemberAchievement } from '@/types/achievement';
import type { ProgressResponse } from '@/services/achievementService';

export function useAchievements() {
  const [achievements, setAchievements] = useState<Achievement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    achievementService.getAllAchievements()
      .then((data) => setAchievements(data ?? []))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  return { achievements, loading, error };
}

export function useMemberAchievements(memberId: string) {
  const [memberAchievements, setMemberAchievements] = useState<MemberAchievement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!memberId) return;
    achievementService.getMemberAchievements(memberId)
      .then((data) => setMemberAchievements(data ?? []))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [memberId]);

  return { memberAchievements, loading, error };
}

export function useUnlockedAchievements(memberId: string) {
  const [unlockedAchievements, setUnlockedAchievements] = useState<Achievement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!memberId) return;
    achievementService.getUnlockedAchievements(memberId)
      .then((data) => setUnlockedAchievements(data ?? []))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [memberId]);

  return { unlockedAchievements, loading, error };
}

export function useLockedAchievements(memberId: string) {
  const [lockedAchievements, setLockedAchievements] = useState<Achievement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!memberId) return;
    achievementService.getLockedAchievements(memberId)
      .then((data) => setLockedAchievements(data ?? []))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [memberId]);

  return { lockedAchievements, loading, error };
}

export function useMemberProgress(memberId: string) {
  const [progress, setProgress] = useState<ProgressResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!memberId) return;
    achievementService.getMemberProgress(memberId)
      .then((data) => setProgress(data))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [memberId]);

  return { progress, loading, error };
}

export function useAutoUnlock(memberId: string) {
  const [unlocking, setUnlocking] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const checkAndUnlock = async () => {
    if (!memberId) return;
    
    setUnlocking(true);
    setError(null);
    
    try {
      await achievementService.checkAndUnlockAchievements(memberId);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to unlock achievements');
    } finally {
      setUnlocking(false);
    }
  };

  return { checkAndUnlock, unlocking, error };
} 