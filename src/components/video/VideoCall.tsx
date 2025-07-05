import { useState, useEffect, useRef, useContext, useCallback } from 'react'
import AgoraRTC from 'agora-rtc-sdk-ng'
import type {
  IAgoraRTCClient,
  IAgoraRTCRemoteUser,
  IMicrophoneAudioTrack,
  ICameraVideoTrack,
  IRemoteVideoTrack
} from 'agora-rtc-sdk-ng'
import { Mic, MicOff, Video, VideoOff, PhoneOff, Loader2, User2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useNavigate } from 'react-router-dom'
import { AuthContext } from '@/context/AuthContext'

interface CustomRemoteUser extends IAgoraRTCRemoteUser {
  isSpeaking: boolean;
}

interface VideoCallProps {
  appId: string;
  channelName: string;
  token: string;
  uid: string;
}

const VideoPlayer = ({ track, isSpeaking, showAvatar = false }: { track: ICameraVideoTrack | IRemoteVideoTrack | undefined, isSpeaking: boolean, showAvatar?: boolean }) => {
  const ref = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (ref.current && track) {
      track.play(ref.current); // Phát video vào phần tử div
    }
    return () => {
      track?.stop(); // Dừng phát video khi component unmount
    }
  }, [track]);
  const speakingClass = isSpeaking ? 'border-4 border-blue-500' : 'border-4 border-transparent';

  return (
    <div ref={ref} className={`w-full h-full bg-black rounded-lg overflow-hidden relative transition-all duration-200 ${speakingClass}`}>
      {showAvatar && (
        <div className="w-full h-full flex items-center justify-center bg-slate-700">
          <User2 className="w-24 h-24 text-slate-500" />
        </div>
      )}
    </div>
  );
}

export function VideoCall({ appId, channelName, token, uid }: VideoCallProps) {
  const client = useRef<IAgoraRTCClient | null>(null)
  const [remoteUsers, setRemoteUsers] = useState<CustomRemoteUser[]>([])
  const [localTracks, setLocalTracks] = useState<[IMicrophoneAudioTrack, ICameraVideoTrack] | null>(null)
  const [isMicOn, setIsMicOn] = useState(true)
  const [isVideoOn, setIsVideoOn] = useState(true)
  const [isJoining, setIsJoining] = useState(true)
  const navigate = useNavigate()
  const authContext = useContext(AuthContext) // Giả sử bạn có AuthContext để lấy thông tin người dùng
  const user = authContext?.user

  // Thêm state cho localIsSpeaking
  const [localIsSpeaking, setLocalIsSpeaking] = useState(false)


  // khởi tạo client và tham gia kênh
  useEffect(() => {
    if (!appId || !channelName || !token || !uid) {
      console.error('Missing required parameters for video call')
      return
    }

    const initAgora = async () => {
      client.current = AgoraRTC.createClient({ mode: 'rtc', codec: 'vp8' });
      client.current.enableAudioVolumeIndicator(); // Bật tính năng đo âm lượng;

      client.current.on('volume-indicator', (volumes) => {
        volumes.forEach((volume) => {
          console.log(`VOLUME: User ${volume.uid} is speaking at level ${volume.level}`)
          // Cai đặt trạng thái speaking cho người dùng cục bộ
          if (volume.uid.toString() === uid) {
            setLocalIsSpeaking(volume.level >= 50); // Nếu mức âm lượng lớn hơn 10, đặt localIsSpeaking là true, lọc nhiễu
          } else {
            // Cập nhật trạng thái speaking cho người dùng từ xa
            setRemoteUsers((prevUsers) => {
              return prevUsers.map((user) => {
                if (user.uid === volume.uid) {
                  return { ...user, isSpeaking: volume.level >= 50 }; // Cập nhật trạng thái speaking
                }
                return user;
              });
            });
          }
        });
      });

      // Lắng nghe sự kiện người dùng được published
      const handleUserPublished = async (user: IAgoraRTCRemoteUser, mediaType: 'audio' | 'video') => {
        await client.current?.subscribe(user, mediaType)
        if (mediaType === 'video') {
          setRemoteUsers((prevUsers) => {
            // Check if user already exists
            if (prevUsers.some((u) => u.uid === user.uid)) {
              // If user exists, update their track. Create a new object to ensure re-render.
              return prevUsers.map((existingUser) =>
                existingUser.uid === user.uid
                  ? { ...existingUser, videoTrack: user.videoTrack, hasVideo: user.hasVideo }
                  : existingUser,
              )
            }
            // If new user, add them to the state. Create a new plain object.
            return [
              ...prevUsers,
              {
                uid: user.uid,
                videoTrack: user.videoTrack,
                audioTrack: user.audioTrack,
                hasVideo: user.hasVideo,
                hasAudio: user.hasAudio,
                isSpeaking: false,
              },
            ]
          })
        }

        if (mediaType === 'audio') {
          user.audioTrack?.play() // Phát track âm thanh của người dùng
        }
      }

      // Lắng nghe sự kiện người dùng rời khỏi kênh
      const handleUserLeft = (user: IAgoraRTCRemoteUser) => {
        setRemoteUsers((prevUsers) => prevUsers.filter((u) => u.uid !== user.uid))
      }

      // Khởi tạo client
      client.current.on('user-published', handleUserPublished)
      client.current.on('user-left', handleUserLeft)

      try {
        await client.current.join(appId, channelName, token, uid) // Tham gia kênh
        const tracks = await AgoraRTC.createMicrophoneAndCameraTracks() // Tạo các track âm thanh và video
        setLocalTracks(tracks) // Lưu các track cục bộ
        await client.current.publish(tracks) // Phát các track cục bộ lên kênh
        setIsJoining(false) // Đặt trạng thái tham gia là false khi đã tham gia thành công
      } catch (error) {
        console.error('Error joining channel:', error)
        setIsJoining(false) // Đặt trạng thái tham gia là false nếu có lỗi
      }
    }

    initAgora()

    return () => {
      localTracks?.[0].close() // Đóng track âm thanh
      localTracks?.[1].close() // Đóng track video
      client.current?.leave() // Rời khỏi kênh
      client.current?.removeAllListeners() // Xóa tất cả các listener
    }
  }, [appId, channelName, token, uid])

  // Tắt/mở mic
  const handleToggleMic = async () => {
    if (localTracks?.[0]) {
      await localTracks[0].setEnabled(!isMicOn) // Chuyển đổi trạng thái mic
      setIsMicOn(!isMicOn) // Cập nhật trạng thái mic
    }
  }

  // Tắt/mở video
  const handleToggleVideo = async () => {
    if (localTracks?.[1]) {
      await localTracks[1].setEnabled(!isVideoOn) // Chuyển đổi trạng thái video
      setIsVideoOn(!isVideoOn) // Cập nhật trạng thái video
    }
  }

  const handleLeaveCall = useCallback(async () => {
    try {
      if (localTracks) {
        await localTracks[0].stop();
        await localTracks[1].stop();
        await localTracks[0].close();
        await localTracks[1].close();
        setLocalTracks(null);
      }

      if (client.current) {
        await client.current.leave();
        client.current.removeAllListeners(); // Xóa tất cả listener
        client.current = null; // Reset client sau khi rời
        console.log("Left Agora channel successfully.");
      }
      setRemoteUsers([]); // Xóa tất cả remote users khi rời

      // Redirect back (Premium -> /booking, Coach -> dashboard)
      if (user?.role === 'COACH') { // Kiểm tra user?.role để tránh lỗi nếu user là null
        navigate('/coach/dashboard');
      } else if (user?.role === 'PREMIUM_MEMBER') {
        navigate('/booking');
      } else {
        console.warn("Unknown user role or role not found. Redirecting to home.");
        navigate('/');
      }

    } catch (error) {
      console.error('Error leaving call:', error);
      alert("There was an error leaving the call. Please try again.");
    }
  }, [localTracks, user, navigate]);


  // Render giao diện video call
  if (isJoining) {
    return (
      <div className="flex flex-col items-center justify-center h-full bg-slate-900 text-white">
        < Loader2 className="w-12 h-12 animate-spin mb-4" />
        <p className="text-lg">Đang kết nối vào cuộc gọi...</p>
      </div >
    )
  }

  return (
    <div className="h-screen w-screen bg-slate-900 flex flex-col relative">
      <div className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-4 p-4">
        {/* localVideo */}
        <div id="local-player" className="bg-black rounded-lg overflow-hidden relative">
          <p className="absolute top-2 left-2 z-10 bg-black/50 text-white px-2 py-1 rounded">Bạn</p>
          {localTracks && <VideoPlayer track={localTracks[1]} isSpeaking={localIsSpeaking} showAvatar={!isVideoOn} />}
        </div>

        {/* Remote Users */}
        {remoteUsers.map((user) => (
          <div key={user.uid} id={`remote-player-${user.uid}`} className="bg-black rounded-lg overflow-hidden relative">
            <p className="absolute top-2 left-2 z-10 bg-black/50 text-white px-2 py-1 rounded">Đối tác</p>
            {user.videoTrack && <VideoPlayer track={user.videoTrack} isSpeaking={user.isSpeaking} showAvatar={!user.hasVideo} />}
          </div>
        ))}

        {/* Control */}
        <div className="absolute bottom-0 left-0 right-0 flex justify-center items-center p-4 bg-black/30">
          <div className="flex space-x-4">
            <Button onClick={handleToggleMic} variant={isMicOn ? 'secondary' : 'destructive'} size="lg" className="rounded-full w-16 h-16">
              {isMicOn ? <Mic /> : <MicOff />}
            </Button>
            <Button onClick={handleToggleVideo} variant={isVideoOn ? 'secondary' : 'destructive'} size="lg" className="rounded-full w-16 h-16">
              {isVideoOn ? <Video /> : <VideoOff />}
            </Button>
            <Button onClick={handleLeaveCall} variant="destructive" size="lg" className="rounded-full w-16 h-16">
              <PhoneOff />
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}