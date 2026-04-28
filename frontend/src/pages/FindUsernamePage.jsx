import { useState } from 'react'
import { Link } from 'react-router-dom'
import { authApi } from '../api/authApi'

export default function FindUsernamePage() {
  const [emailStep, setEmailStep] = useState('idle') // idle | sent | verified
  const [email, setEmail] = useState('')
  const [code, setCode] = useState('')
  const [emailToken, setEmailToken] = useState('')
  const [foundUsername, setFoundUsername] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSendCode = async () => {
    if (!email) return
    setError('')
    setLoading(true)
    try {
      await authApi.sendFindUsernameEmailCode(email)
      setEmailStep('sent')
    } catch (err) {
      setError(err.response?.data?.message || '이메일 전송에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handleVerifyCode = async () => {
    if (!code) return
    setError('')
    setLoading(true)
    try {
      const res = await authApi.confirmFindUsernameEmailCode(email, code)
      setEmailToken(res.data.data.token)
      setEmailStep('verified')
    } catch (err) {
      setError(err.response?.data?.message || '인증 코드가 올바르지 않습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handleFindUsername = async () => {
    setError('')
    setLoading(true)
    try {
      const res = await authApi.findUsername(email, emailToken)
      setFoundUsername(res.data.data.username)
    } catch (err) {
      setError(err.response?.data?.message || '아이디를 찾을 수 없습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>아이디 찾기</h2>

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

        {emailStep !== 'idle' && !foundUsername && (
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

        {emailStep === 'verified' && !foundUsername && (
          <>
            {error && <p className="error-msg">{error}</p>}
            <button className="btn-primary" onClick={handleFindUsername} disabled={loading}>
              {loading ? '조회 중...' : '아이디 찾기'}
            </button>
          </>
        )}

        {foundUsername && (
          <div className="result-box">
            <p className="result-label">회원님의 아이디는</p>
            <strong className="found-value">{foundUsername}</strong>
            <p className="result-label">입니다.</p>
          </div>
        )}

        <div className="auth-links">
          <Link to="/login">로그인</Link>
          <span>|</span>
          <Link to="/reset-password">비밀번호 재설정</Link>
        </div>
      </div>
    </div>
  )
}
