import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '../api/authApi'
import PasswordInput from '../components/PasswordInput'
import { parseError } from '../utils/errorMessage'

export default function RegisterPage() {
  const navigate = useNavigate()
  const [emailStep, setEmailStep] = useState('idle') // idle | sent | verified
  const [email, setEmail] = useState('')
  const [code, setCode] = useState('')
  const [emailToken, setEmailToken] = useState('')
  const [username, setUsername] = useState('')
  const [usernameStatus, setUsernameStatus] = useState('') // '' | available | taken
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSendCode = async () => {
    if (!email) return
    setError('')
    setLoading(true)
    try {
      await authApi.sendRegisterEmailCode(email)
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
      const res = await authApi.confirmRegisterEmailCode(email, code)
      setEmailToken(res.data.data.token)
      setEmailStep('verified')
    } catch (err) {
      setError(parseError(err.response?.data?.message, '인증 코드가 올바르지 않습니다.'))
    } finally {
      setLoading(false)
    }
  }

  const handleCheckUsername = async () => {
    if (!username) return
    setUsernameStatus('')
    try {
      await authApi.checkUsernameAvailable(username)
      setUsernameStatus('available')
    } catch (err) {
      if (err.response?.status === 409) {
        setUsernameStatus('taken')
      } else {
        setUsernameStatus('error')
      }
    }
  }

  const handleRegister = async (e) => {
    e.preventDefault()
    if (usernameStatus !== 'available') {
      setError('아이디 중복 확인을 해주세요.')
      return
    }
    if (password !== confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.')
      return
    }
    setError('')
    setLoading(true)
    try {
      await authApi.register({ username, password, confirmPassword, email, token: emailToken })
      navigate('/login', { state: { message: '회원가입이 완료되었습니다. 로그인해주세요.' } })
    } catch (err) {
      setError(parseError(err.response?.data?.message, '회원가입에 실패했습니다.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>회원가입</h2>

        <div className="form-group">
          <label>이메일</label>
          <div className="input-with-btn">
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="이메일 주소를 입력하세요"
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
          <form onSubmit={handleRegister}>
            <div className="form-group">
              <label>아이디</label>
              <div className="input-with-btn">
                <input
                  type="text"
                  value={username}
                  onChange={(e) => { setUsername(e.target.value); setUsernameStatus('') }}
                  placeholder="영문, 숫자만 사용 가능"
                />
                <button type="button" className="btn-secondary" onClick={handleCheckUsername}>
                  중복 확인
                </button>
              </div>
              {usernameStatus === 'available' && (
                <p className="success-msg">사용 가능한 아이디입니다.</p>
              )}
              {usernameStatus === 'taken' && (
                <p className="error-msg">이미 사용 중인 아이디입니다.</p>
              )}
            </div>
            <div className="form-group">
              <label>비밀번호</label>
              <PasswordInput
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="영문 대/소문자, 숫자, 특수문자 포함 8자 이상"
              />
            </div>
            <div className="form-group">
              <label>비밀번호 확인</label>
              <PasswordInput
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="비밀번호를 다시 입력하세요"
              />
            </div>
            {error && <div className="error-msg">{error}</div>}
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? '처리 중...' : '회원가입'}
            </button>
          </form>
        )}

        <div className="auth-links">
          <Link to="/login">이미 계정이 있으신가요? 로그인</Link>
        </div>
      </div>
    </div>
  )
}
