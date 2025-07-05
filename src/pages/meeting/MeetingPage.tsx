import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { useAuth } from '@/hooks/useAuth'
import { agoraService, type AgoraTokenResponse } from '@/services/agoraService'
import { VideoCall } from '@/components/video/VideoCall'
import { Loader2, AlertTriangle } from 'lucide-react'

export function MeetingPage() {
  const { appointmentId } = useParams<{ appointmentId: string }>();
  const { user } = useAuth();
  const [agoraCreds, setAgoraCreds] = useState<AgoraTokenResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchToken = async () => {
      if (!appointmentId || !user) {
        setError('Thông tin cuộc hẹn hoặc người dùng không hợp lệ');
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        // Coach và member đều là publishers trong cuộc gọi video
        const isPublisher = user?.role === 'COACH' || user?.role === 'PREMIUM_MEMBER';
        const creds = await agoraService.getAgoraToken(parseInt(appointmentId!), isPublisher);
        setAgoraCreds(creds);
      } catch (error) {
        console.error('❌ Lỗi khi lấy token Agora:', error);
        setError('Không thể kết nối đến cuộc gọi video. Vui lòng thử lại sau.');
      } finally {
        setLoading(false);
      }
    };

    fetchToken();
  }, [appointmentId, user]);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-slate-100 dark:bg-slate-900">
        <Loader2 className="w-12 h-12 animate-spin text-emerald-500 mb-4" />
        <p className="text-lg text-slate-700 dark:text-slate-300">Đang chuẩn bị cuộc gọi...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-slate-100 dark:bg-slate-900">
        <AlertTriangle className="w-12 h-12 text-red-500 mb-4" />
        <p className="text-lg text-red-600">Lỗi</p>
        <p className="text-slate-700 dark:text-slate-300">{error}</p>
      </div>
    )
  };

  if (!agoraCreds) {
    return <div>Không có thông tin để bắt đầu cuộc gọi.</div>
  }

  return (
    <VideoCall
      appId={agoraCreds.agoraAppId}
      channelName={agoraCreds.agoraChannelName}
      token={agoraCreds.agoraToken}
      uid={agoraCreds.agoraUid}
    />
  )
}