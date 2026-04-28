import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '../api/authApi'
import PasswordInput from '../components/PasswordInput'
import { parseError } from '../utils/errorMessage'

export default function ResetPasswordPage() {
  const navigate = useNavigate()
  const [emailStep, setEmailStep] = useState('idle') // idle | sent | verified
  const [email, setEmail] = useState('')
  const [code, setCode] = useState('')
  const [emailToken, setEmailToken] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSendCode = async () => {
    if (!email) return
    setError('')
    setLoading(true)
    try {
      await authApi.sendResetPasswordEmailCode(email)
      setEmailStep('sent')
    } catch (err) {
      setError(parseError(err.response?.data?.message, '이메일 전송에 실패했습니다.'))
    } finally {
      setLoading(false)
    }
  }

  const handleVerifyCode = async () => {
    if (!code) return
    setError('')
    setLoading(true)
    try {
      const res = await authApi.confirmResetPasswordEmailCode(email, code)
      setEmailToken(res.data.data.token)
      setEmailStep('verified')
    } catch (err) {
      setError(parseError(err.response?.data?.message, '인증 코드가 올바르지 않습니다.'))
    } finally {
      setLoading(false)
    }
  }

  const handleResetPassword = async (e) => {
    e.preventDefault()
    if (password !== confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.')
      return
    }
    setError('')
    setLoading(true)
    try {
      await authApi.resetPassword({ email, token: emailToken, password, confirmPassword })
      navigate('/login', { state: { message: '비밀번호가 재설정되었습니다. 로그인해주세요.' } })
    } catch (err) {
      setError(parseError(err.response?.data?.message, '비밀번호 재설정에 실패했습니다.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>비밀번호 재설정</h2>

        <div className="form-group">
          <label>이메일</label>
          <div className="input-with-btn">
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="가입 시 사용한 이메일"
              disabled={emailStep !== 'idle'}
            />
            <button
              type="button"
              className="btn-secondary"
              onClick={handleSendCode}
              disabled={loading || emailStep !== 'idle'}
            >
              {emailStep === 'idle' ? '인증 코드 발송' : '발송 완료'}
            </button>
          </div>
          {error && emailStep === 'idle' && <p className="error-msg">{error}</p>}
        </div>

        {emailStep !== 'idle' && (
          <div className="form-group">
            <label>인증 코드</label>
            <div className="input-with-btn">
              <input
                type="text"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="인증 코드를 입력하세요"
                disabled={emailStep === 'verified'}
              />
              <button
                type="button"
                className="btn-secondary"
                onClick={handleVerifyCode}
                disabled={loading || emailStep === 'verified'}
              >
                {emailStep === 'verified' ? '인증 완료 ✓' : '확인'}
              </button>
            </div>
            {emailStep === 'sent' && (
              <p className="hint">이메일로 전송된 인증 코드를 입력하세요. (유효시간 5분)</p>
            )}
            {error && emailStep === 'sent' && <p className="error-msg">{error}</p>}
          </div>
        )}

        {emailStep === 'verified' && (
          <form onSubmit={handleResetPassword}>
            <div className="form-group">
              <label>새 비밀번호</label>
              <PasswordInput
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="영문 대/소문자, 숫자, 특수문자 포함 8자 이상"
              />
            </div>
            <div className="form-group">
              <label>새 비밀번호 확인</label>
              <PasswordInput
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="비밀번호를 다시 입력하세요"
              />
            </div>
            {error && <div className="error-msg">{error}</div>}
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? '처리 중...' : '비밀번호 재설정'}
            </button>
          </form>
        )}

        <div className="auth-links">
          <Link to="/login">로그인으로 돌아가기</Link>
        </div>
      </div>
    </div>
  )
}
