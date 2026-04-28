import { useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../api/authApi'

export default function HomePage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    authApi.verifyToken()
  }, [])

  const handleLogout = async () => {
    try {
      await authApi.logout()
    } catch {
      // 로그아웃 API 실패해도 로컬 상태는 초기화
    } finally {
      logout()
      navigate('/login')
    }
  }

  return (
    <div className="home-container">
      <div className="home-card">
        <h2>안녕하세요, {user?.username}님!</h2>
        <p className="home-sub">HAS (Hybrid Auth System)에 로그인되었습니다.</p>
        <div className="home-actions">
          <Link to="/change-password" className="btn-secondary-link">
            비밀번호 변경
          </Link>
          <button className="btn-logout" onClick={handleLogout}>
            로그아웃
          </button>
        </div>
      </div>
    </div>
  )
}
