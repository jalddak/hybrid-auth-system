/**
 * 백엔드 validation 에러 메시지에서 한글 설명만 추출
 * 예: "username: 영문자와 숫자만 가능합니다." → "영문자와 숫자만 가능합니다."
 * 예: "인증 코드가 틀렸습니다." → 그대로
 */
export function parseError(message, fallback = '오류가 발생했습니다.') {
  if (!message) return fallback

  return message
    .split(', ')
    .map((part) => {
      const match = part.match(/^[a-zA-Z]+: (.+)$/)
      return match ? match[1] : part
    })
    .join('\n')
}
